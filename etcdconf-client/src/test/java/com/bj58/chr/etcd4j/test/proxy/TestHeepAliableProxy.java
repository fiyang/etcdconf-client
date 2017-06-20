package com.bj58.chr.etcd4j.test.proxy;

import com.bj58.chr.confetcd.proxy.HttpAliableProxy;

import junit.framework.TestCase;

public class TestHeepAliableProxy extends TestCase{
	
	public void testHttpProxy() throws Exception{
		String url [] = {"http://127.0.0.1:8080/","http://127.0.0.1:8080/"};
		HttpAliableProxy proxy = new HttpAliableProxy(url,"qz");
		String [] confCenter = proxy.getEtcdConfIp();
		for (String string : confCenter) {
			System.out.println(string);
		}
	}
}
