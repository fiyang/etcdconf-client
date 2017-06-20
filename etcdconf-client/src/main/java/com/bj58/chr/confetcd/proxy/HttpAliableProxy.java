package com.bj58.chr.confetcd.proxy;

import java.net.URI;

import com.google.gson.Gson;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class HttpAliableProxy {
	
	private static final String PREFIX = "conf";
	
	private String [] hostString;
	
	private String bizName;
	
	private static final int ITERCOUNT= 2;
	
	private String pickUpRand(){
		int len = hostString.length;
		int index = (int) (System.currentTimeMillis() % len);
		return hostString[index];
	}
	
	public String []  getEtcdConfIp() throws Exception{
		for(int i=0;i<hostString.length *ITERCOUNT;i++){
			try{
				String [] result = getEtcdURIFromOne(pickUpRand() + "/"+PREFIX+"/" + bizName);
				if(null != result && result.length > 0){
					return result;
				}
			}catch(Exception e){
				log.error("HttpAliableProxy.getEtcdURI", e);
			}
		}
		return null;
	}
	public URI [] getEtcdConfIpURI() throws Exception{
		String [] uris = getEtcdConfIp();
		if(null != uris && uris.length > 0){
			URI [] uriArray = new URI[uris.length];
			for (int i= 0;i<uriArray.length;i++) {
				uriArray[i] = new URI(uris[i]);
			}
			return uriArray;
		}
		return null;
	}
	private String [] getEtcdURIFromOne(String string) throws Exception{
		HttpGetRequest httpGetRequest = new HttpGetRequest(string);
		String json = httpGetRequest.doGet();
		Gson gson = new Gson();
		ReturnValue value = gson.fromJson(json, ReturnValue.class);
		String urls = value.getData();
		return urls.split(",");
	}
	public HttpAliableProxy(String[] hostString,String bizName) {
		this.hostString = hostString;
		this.bizName = bizName;
	}
}
