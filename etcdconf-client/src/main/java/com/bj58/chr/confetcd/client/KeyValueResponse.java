package com.bj58.chr.confetcd.client;

import lombok.Data;
/**
 * etcd回调包装器
 * @author 58
 *
 */
@Data
public class KeyValueResponse {
	private String key;
	private String value;
	private Long ttl;
	private Long createIndex;
	private Long modifyIndex;
	
}
