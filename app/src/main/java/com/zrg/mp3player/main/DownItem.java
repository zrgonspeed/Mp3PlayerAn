package com.zrg.mp3player.main;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.zrg.mp3play.R;
import com.zrg.mp3player.bean.Mp3Bean;
import com.zrg.mp3player.tools.FileTool;
import com.zrg.mp3player.tools.PathTools;

import android.app.Activity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

/**
 * 下载项
 *
 * @author ZRG
 *
 */
public class DownItem {
	private Mp3Bean bean;
	private int threadCount = 4;
	private List<ProgressBar> pbList = new ArrayList<ProgressBar>();
	private List<Thread> inList = new ArrayList<Thread>();
	private Activity mainActivity;
	private String requestMethod = "GET";
	private int connectionTimeOut = 5000;

	private int completeNums;

	public DownItem() {
	}

	public Mp3Bean getBean() {
		return bean;
	}

	public void setBean(Mp3Bean bean) {
		this.bean = bean;
	}

	public void setMainActivity(DownActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	public List<ProgressBar> getPbList() {
		return pbList;
	}

	public void initPb() {
		pbList.clear();
		for (int i = 0; i < threadCount; i++) {
			ProgressBar pb = (ProgressBar) View.inflate(mainActivity.getApplicationContext(), R.layout.item1, null);
			pbList.add(pb);
		}
	}

	public void start() {
		System.out.println("下载文件-" + bean.getMp3Name());
		PathTools pTools = new PathTools(bean.getMp3Path());

		try {
			URL url = new URL(pTools.getUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod(requestMethod);
			connection.setConnectTimeout(connectionTimeOut);

			int code = connection.getResponseCode();
			if (code == 200) {
				System.out.println("code=200, 响应正常");

				int length = connection.getContentLength();
				System.out.println("下载文件的大小: " + length + "字节");

				// 创建一个和下载文件一样大小的文件,为下载内容申请空间
				RandomAccessFile raf = new RandomAccessFile(pTools.getAndroidPath(), "rw");
				raf.setLength(length);
				raf.close();

				// 分配每个线程下载文件的开始位置和结束位置
				int blockSize = length / threadCount;
				for (int i = 0; i < threadCount; i++) {
					int start = i * blockSize;
					int end = (i + 1) * blockSize - 1;

					if (i == threadCount - 1) {
						end = length - 1;
					}

					// 开启线程下载内容
					DownloadThread thread = new DownloadThread(start, end, i);
					thread.setPathTools(pTools);
					inList.add(thread);
					thread.start();
				}

			} else {
				System.out.println("code!=200, 响应不正常");
			}

		} catch (MalformedURLException e) {
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mainActivity.getApplicationContext(), "构造URL发生错误.!", Toast.LENGTH_SHORT).show();
				}
			});

			System.out.println("构造URL发生错误.");
		}

		catch (Exception e) {
			System.out.println("服务器连接超时异常");
			mainActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mainActivity.getApplicationContext(), "服务器连接超时", Toast.LENGTH_LONG).show();
				}
			});

			e.printStackTrace();
		}
	}

	public void pause() {
		for (int i = 0; i < inList.size(); i++) {
			inList.get(i).interrupt();
		}
	}

	/*
	 * 多线程下载类
	 */
	private class DownloadThread extends Thread {
		private int start;
		private int end;
		private int threadId;
		private int pbMax;
		private int pbCurrent;
		private File recordFile;
		private PathTools pTools;

		public DownloadThread(int start, int end, int threadId) {
			this.start = start;
			this.end = end;
			this.threadId = threadId;

			this.recordFile = new File(Constant.sdPath + getRecordFileName());
		}

		public void setPathTools(PathTools pTools) {
			this.pTools = pTools;
		}

		public String getRecordFileName() {
			return threadId + ".txt";
		}

		public void run() {
			try {
				pbMax = end - start;

				URL url = new URL(pTools.getUrl());
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setRequestMethod("GET");
				connection.setConnectTimeout(5000);

				// 判断断点下载记录文件是否存在，用该文件来继续之前的下载
				if (recordFile.exists() && recordFile.length() > 0) {
					FileInputStream inputStream = new FileInputStream(recordFile);

					// 把输入流转换成字节流
					BufferedReader buf = new BufferedReader(new InputStreamReader(inputStream));

					String line = buf.readLine();
					int lastPos = Integer.parseInt(line);

					// 得到进度条初始化的当前位置
					pbCurrent = lastPos - start;
					start = lastPos;

					buf.close();
					System.out.println("当前线程 " + threadId + " 实际开始位置: " + start);
				}

				// 设置请求头"range",设置下载的开始和结束位置
				connection.setRequestProperty("Range", "bytes=" + start + "-" + end);

				int code = connection.getResponseCode();
				// code=200, 请求全部资源成功
				// code=206 请求部分资源成功
				if (code == 206) {
					System.out.println("code=206, 请求部分资源成功");

					RandomAccessFile raf = new RandomAccessFile(pTools.getAndroidPath(), "rw");
					// 设置文件的读写位置
					raf.seek(start);

					InputStream in = connection.getInputStream();
					BufferedInputStream bis = new BufferedInputStream(in);

					int len = -1;
					byte[] buffer = new byte[1024 * 1024];
					// 写数据到下载文件中
					while ((len = bis.read(buffer)) != -1) {
						raf.write(buffer, 0, len);

						// 实现断点下载
						// 1.记录当前下载位置
						start = start + len;

						// 2.打开记录下载位置的文件
						RandomAccessFile raf2 = new RandomAccessFile(recordFile, "rwd");

						// 字符 转 字节数组 写入文件
						raf2.write(String.valueOf(start).getBytes());
						raf2.close();

						pbCurrent += len;
						// 设置进度条的最大值和当前值
						pbList.get(threadId).setMax(pbMax);
						pbList.get(threadId).setProgress(pbCurrent);

						if (DownloadThread.interrupted()) {
							bis.close();
							raf.close();
							System.out.println("线程中断");
							return;
						}
					}

					System.out.println("线程id:--" + threadId + " 下载完毕");

					completeNums++;
					// 判断是否全部线程下载完毕
					if (completeNums == threadCount) {
						mainActivity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								Toast.makeText(mainActivity.getApplicationContext(),
										"文件 " + bean.getMp3Name() + " 下载完毕", Toast.LENGTH_SHORT).show();
							}
						});

						bis.close();
						raf.close();

						// 下载完成把文件从sd卡目录移到zrgmusic文件夹
						FileTool.copy(pTools.getAndroidPath(), Constant.mp3DirPath + "/" + pTools.getFileName());
						FileTool.deleteFile(pTools.getAndroidPath());

						((DownActivity) mainActivity).downloading = false;

						System.out.println("文件 " + bean.getMp3Name() + " 下载完毕");
					}

					// 删除保存下载位置的临时文件
					recordFile.delete();

				} else {
					System.out.println("响应不正常, code != 206");
				}

			} catch (Exception e) {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(mainActivity.getApplicationContext(), "网络异常-下载中断！", Toast.LENGTH_SHORT).show();
					}
				});

				pause();
				System.out.println("线程" + threadId + "异常");
				e.printStackTrace();
			}
		}
	}
}
