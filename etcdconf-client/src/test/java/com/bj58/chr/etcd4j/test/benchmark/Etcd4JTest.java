package com.bj58.chr.etcd4j.test.benchmark;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.commons.lang.time.StopWatch;

import com.bj58.chr.confetcd.client.AvaiableSingleEtcdClient;
import com.bj58.chr.confetcd.client.ISysWatchCallBack;
import com.bj58.chr.confetcd.client.IWatchCallBack;
import com.bj58.chr.confetcd.client.KeyValueResponse;

import lombok.extern.slf4j.Slf4j;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;

@Slf4j
public class Etcd4JTest {
	
	private static final int DEFAULT_TIMEOUT = 50;
	private static final int DEFAULT_SET_COUNT = 100;
	private static final String KEY_NAME = "web_index/ida";
	
	public static void watch(final AvaiableSingleEtcdClient etcdClient,final String key,final IWatchCallBack callBack){
		try {
			etcdClient.regWatcher(key, callBack,
					new ISysWatchCallBack() {
						@Override
						public void callSys(KeyValueResponse response) {
							//清除掉原有的缓存
							//重新watch这个节点
							watch(etcdClient,key,callBack);
						}
					});
		} catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
			log.error("EtcdConfClient.watch", e);
		}
}
	
	public static void main(String [] args) throws URISyntaxException, IOException, EtcdException, EtcdAuthenticationException, TimeoutException{
		URI [] uriArray = { new URI("http://127.0.0.1:5379"),new URI("http://127.0.0.1:5379"),new URI("http://127.0.0.1:5379")};
		final AvaiableSingleEtcdClient client = new AvaiableSingleEtcdClient(uriArray);
		client.connect();
		client.check();
		final AtomicLong totalTime = new AtomicLong();
		final AtomicLong totalCount = new AtomicLong();
		final AtomicLong totalSet = new AtomicLong();
		
		Timer t=new Timer();
		TimerTask timeTask = new TimerTask() {
			@Override
			public void run(){
				log.info("totalTime: {}",totalTime.get());
				log.info("totalCount: {}",totalCount.get());
				try{
					log.info("avg : {}",totalTime.get()/totalCount.get());
				}catch(Exception e){
				}
				totalCount.set(0);
				totalTime.set(0);
			}
		};
		watch(client,KEY_NAME,new IWatchCallBack() {
			@Override
			public void call(KeyValueResponse response) {
				log.info("callbak");
			}
		});
		/*
		final ISysWatchCallBack callBack = new ISysWatchCallBack() {
			@Override
			public void callSys(KeyValueResponse response) {
				try {
					client.regWatcher(KEY_NAME, new IWatchCallBack() {
						@Override
						public void call(KeyValueResponse response) {
							log.info("watchCallBack");
						}
					}, callBack);
				} catch (IOException | EtcdException | EtcdAuthenticationException | TimeoutException e) {
					log.error("logger.watcher error", e);
				}
			}
		};
		
		client.regWatcher(KEY_NAME, new IWatchCallBack() {
			@Override
			public void call(KeyValueResponse response) {
				log.info("watchCallBack");
			}
			}, callBack);
		
		*/
		
		t.schedule(timeTask, 1000, 5000L);
		while(true){
			if(totalSet.incrementAndGet() > DEFAULT_SET_COUNT){
				totalSet.set(0);
				client.put("web_index/ida", "那你的这个里面只需要组件、和适配吧,反编译一下，我们web常用框架wf(这里吐槽一下wf,没有必要这样依赖spring,这样使用者没有办法使用spring).是否还有其它的解决方案，使用spring mvc？----流量大的页面监控怎么做?是否可以使用wf的spring动态的加入spring的配置文件，使用wf的spring，使用部分简单的ioc功能？我们想到了spring的----BeanFactoryPostProcessor科普一下：");
			}
			StopWatch obj = new StopWatch();
			obj.start();
			try{
				KeyValueResponse kevResponse = client.get("web_index/ida");
			}catch(Exception e){
				log.error("get", e);
			}
			obj.stop();
			totalTime.getAndAdd(obj.getTime());
			if(obj.getTime() > DEFAULT_TIMEOUT){
				log.info("time out {} " ,obj.getTime());
			}
			totalCount.incrementAndGet();
			totalTime.get();
		}
		
		
	}
}
