package com.zrg.mp3player.tools;

import java.io.File;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.zrg.mp3player.bean.Mp3Bean;
import com.zrg.mp3player.main.Constant;
import com.zrg.mp3player.main.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

/**
 * 客户端List工具
 *
 * @author ZRG
 *
 */
public class ListTool {
	/**
	 * 从本地文件夹文件中获得mp3列表
	 *
	 * @param files
	 * @return
	 */
	public static List<Mp3Bean> getMp3List(File[] files) {
		List<Mp3Bean> list = new ArrayList<Mp3Bean>();

		for (int i = 0; i < files.length; i++) {
			File file = files[i];

			if (file.getName().length() < 5) {
				System.out.println(file.getName() + "-长度小于5");
				continue;
			}

			if (!file.getName().matches(".+(\\.mp3)")) {
				System.out.println(file.getName() + "-不是mp3文件");
				continue;
			}

			Mp3Bean mp3Bean = new Mp3Bean();
			// 去后缀名.
			String name = file.getName().substring(0, file.getName().lastIndexOf("."));
			mp3Bean.setMp3Name(name);
			mp3Bean.setMp3Size(file.length());
			mp3Bean.setMp3Path(file.getPath());

			System.out.println(file.getPath());

			list.add(mp3Bean);
		}

		return list;
	}

	/**
	 * 请求服务器创建xml文件
	 */
	public static void pleaseCreateXml() {
		try {
			URL url = new URL(Constant.xmlCreatePath);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3 * 1000);
			int code = connection.getResponseCode();
			if (code == 200) {
				System.out.println("请求创建xml成功！");
			} else {
				System.out.println("请求创建xml失败！" + code);
			}

			connection.getInputStream().close();
		} catch (Exception e) {
			System.out.println("服务器访问异常-请求创建xml");
			e.printStackTrace();
		}
	}

	/**
	 * 从服务器获得Mp3Bean列表
	 *
	 * @return
	 */
	public static List<Mp3Bean> getMp3ListFromServer(final MainActivity mainActivity) {
		try {
			pleaseCreateXml();

			URL url = new URL(Constant.xmlPath);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(3 * 1000);

			int code = connection.getResponseCode();
			if (code == 200) {
				InputStream in = connection.getInputStream();

				// 返回解析xml文件获得的mp3列表
				return XmlTool.parsingXml(in);
			} else {
				System.out.println("响应不正常, code != 200, code is " + code);
			}

		} catch (Exception e) {
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mainActivity.getApplicationContext(), "连接服务器异常", Toast.LENGTH_LONG).show();
				}
			});

			System.out.println("服务器访问异常");
			e.printStackTrace();
		}

		System.out.println("返回空");
		return null;
	}
}
