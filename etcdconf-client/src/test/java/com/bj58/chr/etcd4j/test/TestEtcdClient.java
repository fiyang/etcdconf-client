package com.bj58.chr.etcd4j.test;


import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Test;



import junit.framework.TestCase;


public class TestEtcdClient extends TestCase{
	
	/*
	private EtcdClient client;
	private EtcdWatch watchClient;
	private EtcdKV kvClient;
	
	private ByteSequence key = ByteSequence.fromString("/v2/keys/message");
	private ByteSequence value = ByteSequence.fromString("test_val");
	
	@Test
	public void testEtcdWatch() throws ConnectException, AuthFailedException, InterruptedException, ExecutionException{
		client = EtcdClientBuilder.newBuilder().endpoints("xxxxx:2379").build();
	    watchClient = client.getWatchClient();
	    kvClient = client.getKVClient();
	    
	    
	    WatchOption option = WatchOption.DEFAULT;
	    
	    Watcher watcher = watchClient.watch(key, option, new WatchCallback() {
			@Override
			public void onWatch(EtcdHeader header, List<WatchEvent> events) {
				System.out.println("watch");
			}
			
			@Override
			public void onResuming() {
				System.out.println("onResuming");
			}
		}).get();
	}
	
	public void testGetEtcdValue() throws ConnectException, AuthFailedException, UnsupportedEncodingException{
		client = EtcdClientBuilder.newBuilder().endpoints("http://xxxxx:2379").build();
	    kvClient = client.getKVClient();
	    
	    ByteString sampleKey = ByteString.copyFrom("web_index", "UTF-8");
		ByteString sampleValue = ByteString.copyFrom("web_index", "UTF-8");
		
		
	}
	
	@Test
	public void testEtcdPut() throws ConnectException, AuthFailedException, UnsupportedEncodingException, InterruptedException, ExecutionException{
		client = EtcdClientBuilder.newBuilder().endpoints("xxxxx:2379").build();
		kvClient = client.getKVClient();
		ByteString sampleKey = ByteString.copyFrom("sample_key", "UTF-8");
		ByteString sampleValue = ByteString.copyFrom("sample_value", "UTF-8");
		ListenableFuture<PutResponse> response = kvClient.put(sampleKey, sampleValue);
		
		
		ListenableFuture<RangeResponse> responseKvGet = kvClient.get(sampleKey);
		RangeResponse responseFurther = responseKvGet.get();
		List<KeyValue> resultList = responseFurther.getKvsList();
		System.out.println(resultList.size());
		System.out.println(response.get());
		
	}
	*/
}
