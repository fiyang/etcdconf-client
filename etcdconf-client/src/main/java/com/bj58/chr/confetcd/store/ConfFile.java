package com.bj58.chr.confetcd.store;

import com.bj58.chr.confetcd.client.KeyValueResponse;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=true)
public class ConfFile extends ConfFileMeta{
	private String content;
	
	public ConfFile(KeyValueResponse keyValueResponse){
		this.content = keyValueResponse.getValue();
		this.setVersion(keyValueResponse.getModifyIndex());
	}
	
	public ConfFile(){
	}
}
