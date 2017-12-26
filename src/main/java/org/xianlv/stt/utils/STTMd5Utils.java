package org.xianlv.stt.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * @author smc
 *
 */
public class STTMd5Utils {
	/**
	 * 获取文件的MD5值
	 * 
	 * @param file
	 * @return
	 */
	public static String genMd5(File file) {
		FileInputStream inputStream = null;
		MappedByteBuffer byteBuffer = null;
		String value = null;
		try {
			inputStream = new FileInputStream(file);

			byteBuffer = inputStream.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(byteBuffer);

			BigInteger integer = new BigInteger(1, digest.digest());
			value = integer.toString(16);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return value;
	}
}
