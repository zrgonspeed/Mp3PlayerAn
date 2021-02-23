package com.zrg.mp3player.tools;

import com.zrg.mp3player.main.Constant;

public class PathTools {
	private String url;
	private String fileName;

	public String getFileName() {
		return fileName;
	}

	public String getAndroidPath() {
		return Constant.sdPath + getFileName();
	}

	public PathTools(String url) {
		this.url = url;
		this.fileName = subName(url);
	}

	public String getUrl() {
		return url;
	}

	/*
	 * 截取下载文件名
	 */
	public static String subName(String path) {
		int lastIndex = path.lastIndexOf("/");
		String name = path.substring(lastIndex + 1);
		return name;
	}

}
