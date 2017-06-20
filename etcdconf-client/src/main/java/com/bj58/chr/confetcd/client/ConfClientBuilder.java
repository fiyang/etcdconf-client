package com.bj58.chr.confetcd.client;

import java.net.URI;

import lombok.Data;

@Data
public class ConfClientBuilder {
	
	private URI [] confAddress;
	
	private AvaiableSingleEtcdClient etcdClient;
	
	public static ConfClientBuilder newBuilder(){
		return new ConfClientBuilder();
	}
	
	public ConfClientBuilder confAddress(URI... confAddress){
		this.confAddress = confAddress;
		return this;
	}
	
	public AvaiableSingleEtcdClient buildEtcdClient(){
		return new AvaiableSingleEtcdClient(confAddress);
	}
}

