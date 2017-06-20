package com.bj58.chr.etcd4j.test;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

import junit.framework.TestCase;
import mousio.client.promises.ResponsePromise;
import mousio.client.promises.ResponsePromise.IsSimplePromiseResponseHandler;
import mousio.etcd4j.EtcdClient;
import mousio.etcd4j.promises.EtcdResponsePromise;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;
import mousio.etcd4j.responses.EtcdKeysResponse;

public class TestEtcdClient4j extends TestCase{
	
	final CountDownLatch coutDownLatch = new CountDownLatch(10);
	
	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testEtcd4j() throws IOException, InterruptedException, EtcdException, EtcdAuthenticationException, TimeoutException{
		String [] address = {"http://127.0.0.1:2379"};
		EtcdClient etcdClient = new EtcdClient(URI.create("http://127.0.0.1:2379"));
		for(int i = 0;i<100;i++){
			send("vba"+i,etcdClient);
		}
		for(int i=0;i<100;i++){
			testNewWatch("vba"+i,etcdClient);
		}
		coutDownLatch.await();
	}
	
	private void send(String key,EtcdClient etcdClient) throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException{
		EtcdKeysResponse etcdKeysResponse = etcdClient.put(key, key).send().get();
		etcdKeysResponse.getNode().getKey();
		etcdKeysResponse.getNode().getTTL();
		etcdKeysResponse.getNode().getValue();
	}
	
	private void testNewWatch(String key,EtcdClient etcdClient) throws IOException{
		EtcdResponsePromise promise = etcdClient.get(key).waitForChange().send();
		IsSimplePromiseResponseHandler callBack = new IsSimplePromiseResponseHandler() {
			@Override
			public void onResponse(ResponsePromise innerPromise) {
				System.out.println("need reload to response");
				coutDownLatch.countDown();
				try {
					EtcdKeysResponse keyResponse = (EtcdKeysResponse) innerPromise.get();
					keyResponse.getNode();
					String key = keyResponse.getNode().getKey();
					String value = keyResponse.getNode().getValue();
					System.out.println(key);
					System.out.println(value);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		promise.addListener(callBack);
		
		System.out.println("main thread exit ");
	}
}
