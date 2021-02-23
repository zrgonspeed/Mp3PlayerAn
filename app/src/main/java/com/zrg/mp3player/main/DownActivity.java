package com.zrg.mp3player.main;

import java.util.ArrayList;
import java.util.List;

import com.zrg.mp3play.R;
import com.zrg.mp3player.bean.Mp3Bean;
import com.zrg.mp3player.other.MyIntent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DownActivity extends Activity {
	private Button bt_backMain; // 返回主界面按钮
	private LinearLayout linear_down1;
	private DownItem downItem = new DownItem();
	private List<LinearLayout> ln_downItems = new ArrayList<LinearLayout>();

	public boolean downloading = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 第一次执行该下载列表界面不显示
		moveTaskToBack(true);
		setContentView(R.layout.activity2);

		bt_backMain = (Button) findViewById(R.id.bt_backMain);
		System.out.println("bt_backMain对象值" + bt_backMain);

		bt_backMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// setContentView(R.layout.activity_main);

				// Intent intent = new Intent();
				/* 指定intent要启动的类 */
				MyIntent.intent.setClass(DownActivity.this, MainActivity.class);
				/* 启动一个新的Activity */
				startActivity(MyIntent.intent);
				/* 关闭当前的Activity */
				moveTaskToBack(true);
				// Main2.this.finish();
			}
		});

		linear_down1 = (LinearLayout) findViewById(R.id.linear_main2);

		CommonActivity.downActivity = DownActivity.this;
		System.out.println(CommonActivity.downActivity);
	}

	public void addDownload(Mp3Bean bean) {
		downItem = new DownItem();
		downItem.setMainActivity(DownActivity.this);
		downItem.setBean(bean);
		downItem.initPb();

		// 清除下载项，只能有一项
		for (int i = 0; i < ln_downItems.size(); i++) {
			linear_down1.removeView(ln_downItems.get(i));
		}
		ln_downItems.clear();

		// 获取下载项的布局文件将其转换成视图
		LinearLayout ln_downItem = (LinearLayout) View.inflate(this.getApplicationContext(), R.layout.item_download,
				null);

		// 加入下载项视图
		linear_down1.addView(ln_downItem);
		// 加入下载项视图列表
		ln_downItems.add(ln_downItem);

		// 下载项视图列表中加入进度条
		LinearLayout ln_downItem_ln3 = (LinearLayout) findViewById(R.id.linear_down3);
		// 加入进度条
		for (int i = 0; i < downItem.getPbList().size(); i++) {
			ln_downItem_ln3.addView(downItem.getPbList().get(i));
		}

		// 设置下载文件名
		TextView tv_downmp3Name = (TextView) findViewById(R.id.tv_downmp3Name);
		tv_downmp3Name.setText(bean.getMp3Name());

		// 开始下载
		new Thread(new Runnable() {
			@Override
			public void run() {
				downItem.start();
				downloading = true;
			}
		}).start();

		System.out.println("下载-" + bean.getMp3Name());
	}

	/**
	 * 在activity结束的时候回收资源
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();

		System.out.println("DownActivity结束");
	}

	/**
	 * 按下返回键触发事件
	 */
	@Override
	public void onBackPressed() {

		MyIntent.intent.setClass(this, MainActivity.class);
		startActivity(MyIntent.intent);
		moveTaskToBack(true);

		System.out.println("down按下了back键");
	}
}
