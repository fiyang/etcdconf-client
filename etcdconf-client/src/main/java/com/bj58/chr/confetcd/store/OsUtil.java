package com.bj58.chr.confetcd.store;

import java.io.File;

public final class OsUtil {
	private OsUtil() {
	}

	public static boolean makeDirs(final String filePath) {
		File f = new File(filePath);
		if (!f.exists()) {
			return f.mkdirs();
		}
		return true;
	}
	
	public static boolean isFileExist(final String filePathString) throws Exception {
		File f = new File(filePathString);
		return f.exists();
	}

	public static String pathJoin(final String... pathElements) {
		final String path;
		if (pathElements == null || pathElements.length == 0) {
			path = File.separator;
		} else {
			final StringBuilder sb = new StringBuilder();
			for (final String pathElement : pathElements) {
				if (pathElement.length() > 0) {
					sb.append(pathElement);
					sb.append(File.separator);
				}
			}
			if (sb.length() > 0) {
				sb.deleteCharAt(sb.length() - 1);
			}
			path = sb.toString();
		}
		return (path);
	}

	public static String getRelativePath(File file, File folder) {
		String filePath = file.getAbsolutePath();
		String folderPath = folder.getAbsolutePath();
		if (filePath.startsWith(folderPath)) {
			return filePath.substring(folderPath.length() + 1);
		} else {
			return null;
		}
	}
	/*
	public static void transferFile(File src, File dest) throws Exception {
		if (dest.exists()) {
			dest.delete();
		}
		FileHelper.copyFile(src, dest);
	}

	public static void transferFileAtom(File src, File dest, boolean isDeleteSource) throws Exception {
		// 文件锁所在文件
		File lockFile = new File(dest + ".lock");
		FileOutputStream outStream = null;
		FileLock lock = null;
		try {
			int tryTime = 0;
			while (tryTime < 3) {
				try {
					outStream = new FileOutputStream(lockFile);
					FileChannel channel = outStream.getChannel();

					lock = channel.tryLock();
					if (lock != null) {

						if (dest.exists()) {
							// 判断内容是否一样
							if (FileHelper.isFileEqual(src, dest)) {
								// 内容如果一样，就只需要删除源文件就行了
								if (isDeleteSource) {
									src.delete();
								}
								break;
							}
						}
						log.debug("start to replace " + src.getAbsolutePath() + " to " + dest.getAbsolutePath());
						// 转移
						transferFile(src, dest);
						// 删除源文件
						if (isDeleteSource) {
							src.delete();
						}
						break;
					}
				} catch (FileNotFoundException e) {
					// 打不开文件，则后面进行重试
					log.warn(e.toString(),e);
				} finally {
					// 释放锁，通道；删除锁文件
					if (null != lock) {
						try {
							lock.release();
						} catch (IOException e) {
							log.warn(e.toString(),e);
						}
						if (lockFile != null) {
							lockFile.delete();
						}
					}
					if (outStream != null) {
						try {
							outStream.close();
						} catch (IOException e) {
							log.warn(e.toString());
						}
					}
				}
				// 进行重试
				log.warn("try lock failed. sleep and try " + tryTime);
				tryTime++;
				try {
					Thread.sleep(1000 * tryTime);
				} catch (Exception e) {
					log.error("sleep", e);
				}
			}
		} catch (IOException e) {
			log.error("ioexception", e);
		}
	}
	*/
}
