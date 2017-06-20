package com.bj58.chr.etcd4j.test;

import java.net.URI;
import java.util.concurrent.CountDownLatch;

import com.bj58.chr.confetcd.EtcdClientBuilder;
import com.bj58.chr.confetcd.EtcdConfClient;
import com.bj58.chr.confetcd.client.IWatchCallBack;
import com.bj58.chr.confetcd.client.KeyValueResponse;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;
@Slf4j
public class EtcdClienTest extends TestCase{
	
	public void testEtcdClient() throws InterruptedException{
		EtcdClientBuilder builder = EtcdClientBuilder.newBuilder();
		builder.buildNameSpace("web_index");
		String address [] = {"http://xxxxxxx/","http://xxxxxx/"};
		EtcdConfClient confClient = new EtcdConfClient(address, "web_index","qz");
		//URI etcd = URI.create("http://115.28.104.130:2379");
		//URI [] uri = {etcd};
		//builder.buildURI(uri);
		final CountDownLatch countDownLatch = new CountDownLatch(1);
	    //EtcdConfClient	confClient = builder.build();
	    String value = null;
	    confClient.watch("wiki_11",new IWatchCallBack() {
			@Override
			public void call(KeyValueResponse response) {
				log.info(response.getValue());
				countDownLatch.countDown();
			}
		});
	    while(true){
	    	value = confClient.getConf("wiki_11", null);
	    	if(null != value && !value.equals("abc111111222tttytuittyi")){
	    		//log.info("wiki_111 is {}",value);
	    	}
	    	Thread.sleep(100);
	    }
	    //countDownLatch.await();
	}
}
