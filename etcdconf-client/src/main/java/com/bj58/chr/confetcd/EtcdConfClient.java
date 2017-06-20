package com.bj58.chr.confetcd;

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import com.bj58.chr.confetcd.client.AvaiableSingleEtcdClient;
import com.bj58.chr.confetcd.client.EtcdThreadPool;
import com.bj58.chr.confetcd.client.ISysWatchCallBack;
import com.bj58.chr.confetcd.client.IWatchCallBack;
import com.bj58.chr.confetcd.client.KeyValueResponse;
import com.bj58.chr.confetcd.proxy.HttpAliableProxy;
import com.bj58.chr.confetcd.store.ConfFile;
import com.bj58.chr.confetcd.store.ConfFileMeta;
import com.bj58.chr.confetcd.store.StoreHandler;

import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;

@Slf4j
public class EtcdConfClient {
	
	private   AvaiableSingleEtcdClient etcdClient = null;
	private  String regNameSpace = null;
	private String bizName = null;
	
	private String [] confDns = null;
	
	private StoreHandler storeHandler = new StoreHandler();
	//最大创建实例上限
	private static final Integer MAX_INSTANCE_COUNT = 3;
	private AtomicInteger instanceCount = new AtomicInteger(0);
	//最在重试的次数-获取不到值时
	private static final Integer MAX_RETRY_COUNT =3;
	private AtomicInteger retryCount = new AtomicInteger(0);
	
	private ConcurrentHashMap<String,IWatchCallBack> watchContext = new ConcurrentHashMap<String,IWatchCallBack>();
	//key的缓存
	private ConcurrentHashMap<String, String> cache = new ConcurrentHashMap<String, String>();
	//定期获取远程配置时的间隔
	private static final Integer DEFAULT_SLEEP_TIME = 1000 ; 
	
	private static volatile boolean isInited = false;
	
	//从dnshttp服务中获取配置地址
	private void lookUpDNSAndInitEtcdClient(){
		log.info("begin to lookup dns dnsname{},bizName{}",confDns,bizName);
		HttpAliableProxy httpProxy = new HttpAliableProxy(confDns, bizName);
		URI [] uris = null;
		try{
			uris = httpProxy.getEtcdConfIpURI();
		}catch(Exception e){
			log.error("EtcdConfClient.init.error", e);
		}
		//从配置的dns里找到etcd的地址
		if(null != uris){
			etcdClient = new AvaiableSingleEtcdClient(uris);
			etcdClient.connect();
			etcdClient.check();
			addShutDownHook();
		}
	}
	
	private void init(){
		if(instanceCount.incrementAndGet() > MAX_INSTANCE_COUNT){
			throw new RuntimeException("EtcdConfClient instance max create  is " + MAX_INSTANCE_COUNT);
		}
		//如果找不到---使用本地配置
		lookUpDNSAndInitEtcdClient();
		checkDNSAndFlushAllConf();
	}
	private void addShutDownHook(){
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					if(null != etcdClient){
						etcdClient.colse();
					}
				} catch (IOException e) {
					log.error("etcdClient close faild ",e);
				}
			}
		}));
	}
	
	public EtcdConfClient(String [] confDns,String regNameSpace,String bizName){
		this.regNameSpace = regNameSpace;
		this.bizName = bizName;
		this.confDns = confDns;
		init();
	}
	public void watch(final String key,final IWatchCallBack callBack){
			try {
				if(null == etcdClient){
					log.warn("------------------------cant't init etcd client-------------can't watch");
					return;
				}
				watchContext.put(key, callBack);
				
				etcdClient.regWatcher(getEtcdKey(key), callBack,
						new ISysWatchCallBack() {
							@Override
							public void callSys(KeyValueResponse response) {
								//清除掉原有的缓存
								cache.remove(key);
								ConfFile confFile = new ConfFile(response);
								//重新watch这个节点
								watch(key,callBack);
								//更新版本号
								try {
									confFile.setNameSpace(regNameSpace);
									confFile.setKeyName(key);
									storeHandler.storeConfFile(confFile);
								} catch (Exception e) {
									log.error("EtcdConfClient.watch.storeConfFile", e);
								}
								
							}
						});
			} catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
				log.error("EtcdConfClient.watch", e);
			}
	}
	
	private String getEtcdKey(String key){
		return regNameSpace + "/" + key;
	}
	
	private ConfFile loadFromEtcd(String key){
		try {
			if(null == etcdClient){
				return null;
			}
			KeyValueResponse keyValue = etcdClient.get(getEtcdKey(key));
			if(null != keyValue){
				ConfFile confFile = new ConfFile(keyValue);
				confFile.setNameSpace(regNameSpace);
				confFile.setKeyName(key);
				return confFile;
			}
		} catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
			log.error("EtcdConfClient.loadFromEtcd", e);
		}
		return null;
	}
	
	private ConfFile loadFromLocal(String key){
		if(!storeHandler.dirExists(regNameSpace, key)){
			log.info("store file not exist return null key {}",key);
			return null;
		};
		ConfFileMeta confFileMeta = new ConfFileMeta();
		confFileMeta.setNameSpace(regNameSpace);
		confFileMeta.setKeyName(key);
		try {
			return storeHandler.getFromStore(confFileMeta);
		} catch (Exception e) {
			log.error("EtcdConfClient.loadFromLocal", e);
		}
		return null;
	}
	/**
	 * 如果用户没有注册回调则注册一个默认回调方法
	 * @param key
	 */
	private void regDefaultWatch(String key){
		watch(key,new IWatchCallBack() {
			@Override
			public void call(KeyValueResponse response) {
				log.info("default call back invoked key {},value {}",response.getKey(),response.getValue());
			}
		});
	}
	
	public synchronized void checkDNSAndFlushAllConf(){
		if(!isInited){
			EtcdThreadPool.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					log.info("..................fluashAllConfStart...............");
					isInited = true;
					while(true){
						try{
							//如果etcd客户端从proxy中拿不到配置则定时去获取
							if(null != etcdClient){
								for (Entry<String,String> entry : cache.entrySet()) {
									String key = entry.getKey();
									try {
										getOrStoreEtcdConfFile(key, false);
									} catch (Exception e) {
										log.error("flushAllConf", e);
									}
								}
							}else{
								lookUpDNSAndInitEtcdClient();
							}
						}catch(Exception e){
							log.error("flushAllConf", e);
						}finally{
							try {
								Thread.sleep(DEFAULT_SLEEP_TIME);
							} catch (InterruptedException e) {
								log.error("flushAllConf.sleep", e);
							}
						}
					}
				}
			});
		}
	}
	
	private String  getOrStoreEtcdConfFile(String key,boolean needRetry) throws Exception{
		ConfFile remoteConf = loadFromEtcd(key);
		ConfFile localConf = loadFromLocal(key);
		
		String content = null;
		if(remoteConf == null &&  localConf == null){
			throw new IllegalAccessException("etcd client local,and remote null ");
		}
		//远程的如果 比本地的版本号大，则替换本地的
		if(null != remoteConf){
			Long remoteVersion = remoteConf.getVersion();
			content = remoteConf.getContent();
			//文件目录不存在,本地配置文件读取空,本地配置文件版本小于远端,则更新配置文件
			if((null != localConf && localConf.getVersion() < remoteVersion) || localConf == null ){
				log.info("local file replace....value is {},version is {},key is {}",remoteConf.getContent(),remoteConf.getVersion(),remoteConf.getKeyName());
				storeHandler.storeConfFile(remoteConf);
			}
			cache.put(key, content);
			return content;
		//如果远程获取失败则使用本地
		}else {
			if(needRetry){
				log.warn("can't get remote,sys while use local config key {},namespace {}",key,regNameSpace);
				if(retryCount.incrementAndGet() > MAX_RETRY_COUNT || etcdClient == null){
					log.warn("remote sys failed while use and not retry local config key {},namespace {}",key,regNameSpace);
					cache.put(key, localConf.getContent());
				};
			}
			return localConf.getContent();
		}
	}
	
	public String getConf(String key,String defalut){
		try {
			if(etcdClient == null){
				log.warn("----etcdclient get dns faild----sys use local config");
			}
			if(!watchContext.containsKey(key) && etcdClient != null){
				regDefaultWatch(key);
			}
			if(cache.containsKey(key)){
				return cache.get(key);
			}else{
				return getOrStoreEtcdConfFile(key,true);
			}
		} catch (Exception e) {
			log.error("EtcdConfClient", e);
		}
		return defalut;
	}
	public String getBizName() {
		return bizName;
	}
	public void setBizName(String bizName) {
		this.bizName = bizName;
	}
}
