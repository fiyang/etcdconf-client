package com.bj58.chr.confetcd;

public class EtcdClientBuilder {
	private String [] dns;
	private String nameSpace;
	private String bizName;
	
	public static EtcdClientBuilder newBuilder(){
		return new EtcdClientBuilder();
	}
	
	public EtcdClientBuilder buildURI(String [] dns){
		this.dns = dns;
		return this;
	}
	
	public EtcdClientBuilder buildNameSpace(String nameSpace){
		this.nameSpace = nameSpace;
		return this;
	}
	
	public EtcdConfClient build(){
		return new EtcdConfClient(dns,nameSpace,bizName);
	}

	public String getBizName() {
		return bizName;
	}

	public void setBizName(String bizName) {
		this.bizName = bizName;
	}
}
