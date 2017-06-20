package com.bj58.chr.confetcd.store;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import org.apache.commons.io.FileUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class FileHelper extends FileUtils {


    private FileHelper() {

    }

    /**
     * 关闭文件流
     */
    public static final void closeWriter(Writer w) {
        if (w != null) {
            try {
                w.close();
            } catch (Exception e) {
            	log.warn(e.toString(),e);
            }
        }
    }

    /**
     * 关闭文件流
     */
    public static final void closeReader(Reader r) {
        if (r != null) {
            try {
                r.close();
            } catch (Exception e) {
            	log.warn(e.toString(),e);
            }
        }
    }

    /**
     * 关闭文件流
     */
    public static final void closeOutputStream(OutputStream os) {
        if (os != null) {
            try {
                os.close();
            } catch (Exception e) {
            	log.warn(e.toString());
            }
        }
    }

    /**
     * 关闭文件流
     */
    public static final void closeInputStream(InputStream is) {
        if (is != null) {
            try {
                is.close();
            } catch (Exception e) {
            	log.warn(e.toString(),e);
            }
        }
    }

    /**
     * 使用jar包：commons-codec-1.4.jar的md5比较方法 <br/>
     * http://blog.csdn.net/very365_1208/article/details/8824033
     *
     * @param oldName
     * @param newName
     *
     * @return
     */
    public static boolean isFileUpdate(String oldName, String newName) {
        return isFileEqual(new File(oldName), new File(newName));
    }

    /**
     * http://blog.csdn.net/very365_1208/article/details/8824033
     * http://www.avajava.com/tutorials/lessons/whats-a-quick-way
     * -to-tell-if-the-contents-of-two-files-are-identical-or-not.html
     *
     * @param oldFile
     * @param newFile
     *
     * @return
     */
    public static boolean isFileEqual(File oldFile, File newFile) {
        try {
            return contentEquals(oldFile, newFile);
        } catch (IOException e) {
        	log.warn(e.toString(),e);
            return false;
        }
    }
}
