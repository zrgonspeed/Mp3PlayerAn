package com.zrg.mp3player.main;

import java.util.List;

import com.zrg.mp3play.R;
import com.zrg.mp3player.bean.Mp3Bean;
import com.zrg.mp3player.other.MyIntent;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * mp3列表适配器
 *
 * @author ZRG
 *
 */
public class Mp3ListAdapter extends BaseAdapter implements OnItemClickListener {
	private List<Mp3Bean> mp3List;
	private MainActivity mainActivity;

	public Mp3ListAdapter(List<Mp3Bean> mp3List) {
		this.mp3List = mp3List;
	}

	public Mp3ListAdapter() {
	}

	public void setMp3List(List<Mp3Bean> mp3List) {
		this.mp3List = mp3List;
	}

	public void setMainActivity(MainActivity mainActivity) {
		this.mainActivity = mainActivity;
	}

	@Override
	public int getCount() {
		return mp3List.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyViewHolder myViewHolder = new MyViewHolder();
		final Mp3Bean bean = mp3List.get(position);

		if (convertView == null) {
			convertView = View.inflate(mainActivity.getApplicationContext(), R.layout.item_mp3, null);

			myViewHolder = new MyViewHolder();

			// 注意，必须加view
			myViewHolder.bt_download = (ImageButton) convertView.findViewById(R.id.bt_play);
			myViewHolder.tv_mp3Name = (TextView) convertView.findViewById(R.id.tv_mp3Name);
			myViewHolder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);

			LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.linear2);

			if (mainActivity.flag == 0)
				layout.removeView(myViewHolder.bt_download);

			convertView.setTag(myViewHolder);
		} else {
			myViewHolder = (MyViewHolder) convertView.getTag();
		}

		myViewHolder.tv_count.setText(String.valueOf(position));
		myViewHolder.tv_mp3Name.setText(bean.getMp3Name());

		if (mainActivity.flag == 1)
			myViewHolder.bt_download.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (!CommonActivity.downActivity.downloading) {
						CommonActivity.downActivity.addDownload(bean);
						// 制定intent要启动的类
						MyIntent.intent.setClass(mainActivity, DownActivity.class);

						// 启动一个新的Activity
						((Activity) mainActivity).startActivity(MyIntent.intent);

						// 关闭当前的
						mainActivity.moveTaskToBack(true);
					} else {
						Toast.makeText(mainActivity.getApplicationContext(), "请等待当前下载完成", Toast.LENGTH_LONG).show();
						System.out.println("请等待当前下载完成");
					}

					System.out.println("点击了下载按钮-" + bean.getMp3Name());
				}
			});

		return convertView;
	}

	class MyViewHolder {
		ImageButton bt_download;
		TextView tv_mp3Name;
		TextView tv_count;
	}

	/**
	 * 单击每一项的事件
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		mainActivity.recordPlay.setCurrentPos(position);

		final Mp3Bean bean = mp3List.get(position);
		mainActivity.playController.setBean(bean);
		mainActivity.playController.play();
		mainActivity.btMainPlay.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.pause0));
		System.out.println("单击" + position + bean.getMp3Name());
	}
}
