package com.bj58.chr.confetcd.store;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StoreHandler {
	
	private static final String DEFAULT_SAVE_PATH = "/opt/confdown";
	
	private static final String ENCODING = "utf-8";
	
	public void storeConfFile(ConfFile confFile) throws Exception{
		storeFileAndMetaFile(confFile.getNameSpace(),confFile.getKeyName(),confFile.getContent(),confFile.getVersion());
	}
	
	public ConfFile getFromStore(ConfFileMeta meta) throws Exception{
		String saveFile = getSaveFileDir(meta.getNameSpace(),meta.getKeyName());
		String content = getContentFromFile(saveFile);
		String metaFile = getEncryptSaveFileDir(meta.getNameSpace(),meta.getKeyName());
		String contentMeta = getContentFromFile(metaFile);
		if(null != contentMeta && null != content){
			Gson gson = new Gson();
			ConfFileMeta metaJson = gson.fromJson(contentMeta, ConfFileMeta.class);
			ConfFile conf = new ConfFile();
			conf.setFileName(metaJson.getFileName());
			conf.setKeyName(metaJson.getKeyName());
			conf.setNameSpace(metaJson.getNameSpace());
			conf.setContent(content);
			conf.setVersion(metaJson.getVersion());
			return conf;
		}
		return null;
	}
	
	private String getContentFromFile(String fileName) throws Exception{
		File file = new File(fileName);
		return FileUtils.readFileToString(file, ENCODING);
	}
	
	private void storeFile(String nameSpace,String keyName,String content) throws Exception{
		String fileName = getSaveFileDir(nameSpace,keyName);
		createDirs(nameSpace);
		File file = new File(fileName);
		FileUtils.writeStringToFile(file, content, ENCODING);
	}
	
	private String getSaveFileDir(String nameSpace,String keyName){
		return DEFAULT_SAVE_PATH + File.separator + nameSpace + File.separator + keyName;
	}
	private void createDirs(String nameSpace) throws Exception{
		 String dir = DEFAULT_SAVE_PATH + File.separator + nameSpace;
		if(!OsUtil.isFileExist(dir)){
			OsUtil.makeDirs(dir);
		}
	}
	
	public boolean dirExists(String nameSpace,String keyName){
		String dir = DEFAULT_SAVE_PATH + File.separator + nameSpace;
		try{
			return OsUtil.isFileExist(dir);
		}catch(Exception e){
			log.error("dirExists", e);
		}
		return false; 
	}
	
	private String getEncryptSaveFileDir(String nameSpace,String keyName){
		String fileName = Encrypt.MD532(keyName+nameSpace);
		return DEFAULT_SAVE_PATH + File.separator + nameSpace + File.separator + fileName;
	}
	private void storeMetaFile(String nameSpace,String keyName,Long version) throws IOException{
		String encFileName = getEncryptSaveFileDir(nameSpace,keyName);
		ConfFileMeta metaFile = new ConfFileMeta();
		metaFile.setFileName(getSaveFileDir(nameSpace,keyName));
		metaFile.setVersion(version);
		metaFile.setNameSpace(nameSpace);
		metaFile.setKeyName(keyName);
		Gson gson = new Gson();
		String content = gson.toJson(metaFile);
		File file = new File(encFileName);
		FileUtils.writeStringToFile(file, content, ENCODING);
	}
	
	private void storeFileAndMetaFile(String nameSpace,String keyName,String content,Long version) throws Exception{
		storeMetaFile(nameSpace,keyName,version);
		storeFile(nameSpace,keyName,content);
	}
}
