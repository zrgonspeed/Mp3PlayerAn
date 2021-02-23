package com.zrg.mp3player.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileTool {
	public static void copy(String sourceFilePath, String targetFilePath) throws IOException {
		BufferedInputStream bufis = new BufferedInputStream(new FileInputStream(sourceFilePath));
		BufferedOutputStream bufos = new BufferedOutputStream(new FileOutputStream(targetFilePath));
		int by = 0;
		while ((by = bufis.read()) != -1) {
			bufos.write(by);
		}

		bufis.close();
		bufos.close();
	}

	public static void deleteFile(String sourceFilePath) {
		File file = new File(sourceFilePath);
		if (file.exists()) {
			file.delete();
		}
	}
}
