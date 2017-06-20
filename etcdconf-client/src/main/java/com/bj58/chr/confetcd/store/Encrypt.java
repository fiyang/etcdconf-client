package com.bj58.chr.confetcd.store;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import lombok.extern.slf4j.Slf4j;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Slf4j
public class Encrypt {
	private Encrypt() {
	};
	public static String Base64Decode(String code_type, String code) {
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] bytes = decoder.decodeBuffer(code);
			return new String(bytes, code_type);
		} catch (Exception e) {
			log.error("Encrypt.Base64Decode", e);
			return code;
		}
	}

	public static String MD532(String plainText) {
		String str = "";
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuilder buf = new StringBuilder("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			str = buf.toString();
		} catch (NoSuchAlgorithmException e) {
			log.error("Encrypt.MD532", e);
		}
		return str.toLowerCase();
	}

	public static String MD516(String str) {
		return MD532(str).substring(8, 24);
	}
}
