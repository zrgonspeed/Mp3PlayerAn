package com.zrg.mp3player.tools;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;

import com.zrg.mp3player.bean.Mp3Bean;

import android.util.Xml;

public class XmlTool {
	public static List<Mp3Bean> parsingXml(InputStream in) {
		List<Mp3Bean> list = new ArrayList<Mp3Bean>();
		Mp3Bean bean = null;

		// 1.获得解析器对象
		XmlPullParser parser = Xml.newPullParser();

		// 2.设置解析器参数
		try {
			parser.setInput(in, "UTF-8");

			// 3.获取解析器事件类型
			int type = parser.getEventType();

			// 4.循环解析xml中的标签
			while (type != XmlPullParser.END_DOCUMENT) {
				String name = parser.getName();

				switch (type) {
					case XmlPullParser.START_TAG:
						if ("mp3info".equals(name)) {
							bean = new Mp3Bean();
						} else if ("mp3name".equals(name)) {
							String mp3Name = parser.nextText().toString().trim();
							bean.setMp3Name(mp3Name);
						} else if ("mp3path".equals(name)) {
							String mp3Path = parser.nextText().toString().trim();
							System.out.println(mp3Path);
							bean.setMp3Path(mp3Path);
						}
						break;

					case XmlPullParser.END_TAG:
						if ("mp3info".equals(name)) {
							list.add(bean);
						}
						break;

					default:
						break;
				}

				type = parser.next();
			}

			in.close();
		} catch (Exception e) {
			System.out.println("解析xml异常！");
			e.printStackTrace();
		}

		return list;
	}
}
