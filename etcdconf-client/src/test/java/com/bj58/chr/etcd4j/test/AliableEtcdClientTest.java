package com.bj58.chr.etcd4j.test;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeoutException;

import com.bj58.chr.confetcd.client.AvaiableSingleEtcdClient;
import com.bj58.chr.confetcd.client.ConfClientBuilder;
import com.bj58.chr.confetcd.client.ISysWatchCallBack;
import com.bj58.chr.confetcd.client.IWatchCallBack;
import com.bj58.chr.confetcd.client.KeyValueResponse;

import junit.framework.TestCase;
import mousio.etcd4j.responses.EtcdAuthenticationException;
import mousio.etcd4j.responses.EtcdException;

public class AliableEtcdClientTest extends TestCase{
	
	public void testEtcdClient() throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException{
		AvaiableSingleEtcdClient client = ConfClientBuilder.newBuilder().confAddress(URI.create("http://xxxxxxx:2379")).buildEtcdClient();
		client.connect();
		KeyValueResponse keyValueResponse = client.get("vba0");
		assertNotNull(keyValueResponse);
	}
	
	public void testWatch() throws IOException, EtcdException, EtcdAuthenticationException, TimeoutException, InterruptedException{
		final CountDownLatch countDown = new CountDownLatch(1);
		AvaiableSingleEtcdClient client = ConfClientBuilder.newBuilder().confAddress(URI.create("http://xxxxxxxx:2379")).buildEtcdClient();
		client.connect();
		//NodeChecker nodeChecker= new NodeChecker();
		//nodeChecker.check();
		KeyValueResponse keyValueResponse = client.get("vba0");
		System.out.println("get success");
		client.regWatcher("vba0", new IWatchCallBack() {
			@Override
			public void call(KeyValueResponse response) {
				
			}
		},new ISysWatchCallBack() {
			@Override
			public void callSys(KeyValueResponse response) {
			}
		});
		System.out.println("watch success");
		countDown.await();
	}
}
