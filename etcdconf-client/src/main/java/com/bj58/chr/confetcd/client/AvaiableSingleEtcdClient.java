package com.bj58.chr.confetcd.client;

import java.io.IOException;
import java.net.URI;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import lombok.extern.slf4j.Slf4j;
import mousio.client.promises.ResponsePromise;
import mousio.client.promises.ResponsePromise.IsSimplePromiseResponseHandler;
import mousio.client.retry.RetryNTimes;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.requests.EtcdKeyGetRequest;
import mousio.etcd4j.requests.EtcdKeyPutRequest;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;
/**
 * 高可用连接器
 * 内置检测器保活watch
 * @author 58
 *
 */

@Slf4j
public class AvaiableSingleEtcdClient {

	private static final int TIME_OUT = 500;
	private static final int RETURY_COUNT = 2;
	EtcdClient etcdClient = null;
	private static final int DEFAULT_SLEEP_TIME = 1000;
	private volatile static  boolean flag = false;
	private ISysWatchCallBack sysWatchCallBack;
	private IWatchCallBack iWatchCallBack;
	private URI [] confAddress;
	public AvaiableSingleEtcdClient(URI [] confAddress) {
		this.confAddress = confAddress;
	}
	private ConcurrentHashMap<String, NodeConfig> nameSpaceContext = new ConcurrentHashMap<String,NodeConfig>();
	
	public void connect(){
		if(null != confAddress && confAddress.length > 0){
			etcdClient = new EtcdClient(confAddress);
			etcdClient.setRetryHandler(new RetryNTimes(TIME_OUT, RETURY_COUNT));
		}
	}
	
	public synchronized void check(){
		if(!flag){
			EtcdThreadPool.getThreadPool().execute(new Runnable() {
				@Override
				public void run() {
					log.info("etcd checker start........");
					flag = true;
					while(true){
						try{
							for(Entry<String,NodeConfig> entry : nameSpaceContext.entrySet()){
								if(entry.getValue().getStat() == NodeWatchStat.WATCH_ERROR){
									log.info("find watch_error,now begin to rewatch ");
									String key = entry.getKey();
									regWatcher(key, iWatchCallBack,sysWatchCallBack);
								}
							}
						}catch(Exception e){
							log.error("NodeChecker.check()", e);
						}finally{
							try {
								Thread.sleep(DEFAULT_SLEEP_TIME);
							}
							catch (Exception e) {
								log.error("Thread.sleep", e);
							}
						}
					}
				}
			});
		}
	}
	
	public void colse() throws IOException{
		etcdClient.close();
	}
	
	public KeyValueResponse put(String key,String value) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException{
		EtcdKeyPutRequest request = etcdClient.put(key,value);
		request.timeout(TIME_OUT, TimeUnit.MILLISECONDS);
		return transfer(request.send().get());
	}
	
	public KeyValueResponse get(String key) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException{
		EtcdKeyGetRequest request = etcdClient.get(key);
		request.timeout(TIME_OUT, TimeUnit.MILLISECONDS);
		return transfer(request.send().get());
	}
	private void newNodeConfigAndRegister(String key,IWatchCallBack callBack,ISysWatchCallBack sysCallBack){
		NodeConfig nodeConfig = new NodeConfig();
		nodeConfig.setNodeName(key);
		nodeConfig.setStat(NodeWatchStat.CONNETIONG);
		nodeConfig.setSysWatchCallBack(sysCallBack);
		nodeConfig.setWatchCallBack(callBack);
		nameSpaceContext.put(key, nodeConfig);
	}
	
	private void setWatchError(String key,IWatchCallBack callBack,ISysWatchCallBack sysCallBack){
		if(!nameSpaceContext.containsKey(key)){
			newNodeConfigAndRegister(key,callBack,sysCallBack);
		}
		nameSpaceContext.get(key).setStat(NodeWatchStat.WATCH_ERROR);
	}
	
	public void regWatcher(final String key,final IWatchCallBack callBack,final ISysWatchCallBack sysCallBack) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException{
		sysWatchCallBack = sysCallBack;
		iWatchCallBack = callBack;
		//注册进待扫描程序中
		newNodeConfigAndRegister(key,callBack,sysCallBack);
		EtcdResponsePromise<EtcdKeysResponse> promise = etcdClient.get(key).waitForChange().send();
		IsSimplePromiseResponseHandler callBackHandler = new IsSimplePromiseResponseHandler() {
			@Override
			public void onResponse(ResponsePromise innerPromise) {
				EtcdKeysResponse keyResponse = null;
				try {
					keyResponse = (EtcdKeysResponse) innerPromise.get();
				} catch (Exception e) {
					log.error("regWatcher.inner", e);
					setWatchError(key,callBack,sysCallBack);
				}
				if(null != keyResponse){
					KeyValueResponse keyValueResponse = transfer(keyResponse);
					//先调用系统注册的回调，再调用用户
					sysCallBack.callSys(keyValueResponse);
					callBack.call(keyValueResponse);
				}
			}
		};
		promise.addListener(callBackHandler);
	}
	
	private KeyValueResponse transfer(EtcdKeysResponse etcdResonse){
		KeyValueResponse keyValue = new KeyValueResponse();
		keyValue.setKey(etcdResonse.getNode().getKey());
		keyValue.setValue(etcdResonse.getNode().getValue());
		keyValue.setTtl(etcdResonse.getNode().getTTL());
		keyValue.setModifyIndex(etcdResonse.getNode().getModifiedIndex());
		keyValue.setCreateIndex(etcdResonse.getNode().getCreatedIndex());
		return keyValue;
	}
	
	
	
}
