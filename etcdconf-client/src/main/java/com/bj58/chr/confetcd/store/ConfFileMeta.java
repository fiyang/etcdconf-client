package com.bj58.chr.confetcd.store;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(doNotUseGetters=true)
public class ConfFileMeta {
	private String nameSpace;
	private String fileName;
	private Long version;
	private String keyName;
	
	
	
}
