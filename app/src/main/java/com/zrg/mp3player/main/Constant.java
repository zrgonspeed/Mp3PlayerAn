package com.zrg.mp3player.main;

import android.os.Environment;

public class Constant {
	// 内存卡目录
	public final static String sdPath = Environment.getExternalStorageDirectory().getPath() + "/";
	// 本地mp3文件目录
	public final static String mp3DirPath = Environment.getExternalStorageDirectory().getPath() + "/zrgmusic";
	// 服务器xml文件路径
	public final static String xmlPath = "http://192.168.43.47:8080/AndroidServer/mp3List.xml";
	// 服务器创建xml的servlet路径
	public final static String xmlCreatePath = "http://192.168.43.47:8080/AndroidServer/servlet/CreateXml";
}
