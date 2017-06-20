package com.bj58.chr.etcd4j.test;

import com.bj58.chr.confetcd.store.ConfFile;
import com.bj58.chr.confetcd.store.ConfFileMeta;
import com.bj58.chr.confetcd.store.StoreHandler;
import com.google.gson.Gson;

import junit.framework.TestCase;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StoreHandlerTest extends TestCase{
	
	public void testStoreHandler() throws Exception{
		StoreHandler storeHandler = new StoreHandler();
		ConfFile confFile = new ConfFile();
		confFile.setNameSpace("web_index");
		confFile.setContent("abc111");
		confFile.setVersion(11111L);
		confFile.setKeyName("jobinfo.properties");
		storeHandler.storeConfFile(confFile);
		ConfFileMeta meta = new ConfFileMeta();
		meta.setKeyName("jobinfo.properties");
		meta.setNameSpace("web_index");
		meta.setVersion(11111L);
		ConfFile newFile = storeHandler.getFromStore(meta);
		assertNotNull(newFile);
		Gson gson = new Gson();
		log.info("json file is {}",gson.toJson(newFile));
	}
}
