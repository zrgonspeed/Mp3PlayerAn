package com.zrg.mp3player.main;

import java.io.File;
import java.util.List;

import com.zrg.mp3play.R;
import com.zrg.mp3player.bean.Mp3Bean;
import com.zrg.mp3player.other.MyIntent;
import com.zrg.mp3player.tools.ListTool;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends Activity {
    public PlayController playController = new PlayController();
    public ListView lv;
    public ImageButton btMainPlay;
    public RecordPlay recordPlay = new RecordPlay();
    public int flag = 0;
    private Mp3ListAdapter adapterLocal = new Mp3ListAdapter();
    private Mp3ListAdapter adapterOnline = new Mp3ListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lv = (ListView) findViewById(R.id.lv_mp3);

        // 加载本地mp3列表
        loadLocalData();

        // 给该界面的按钮注册事件
        setButtonClick();

        // 加载显示下载列表的Activity，但不显示界面
        //loadDownActivity();
    }

    /**
     * 加载下载列表界面
     */
    public void loadDownActivity() {
        // 制定intent要启动的类
        MyIntent.intent.setClass(MainActivity.this, DownActivity.class);

        // 启动一个新的Activity
        startActivity(MyIntent.intent);

        // 关闭当前的
        //moveTaskToBack(true);
    }

    /**
     * 加载本地mp3列表
     */
    public void loadLocalData() {
        flag = 0;

        File dirFile = new File(Constant.mp3DirPath);
        System.out.println("指定的文件夹是一个目录吗? " + dirFile.isDirectory());
        System.out.println("是否创建文件夹: " + dirFile.mkdir());

        File[] dirFiles = dirFile.listFiles();
        System.out.println("文件夹总文件数: " + dirFiles.length);

        // 从本地文件夹的文件中筛选出mp3文件，再生成Mp3Bean列表
        final List<Mp3Bean> mp3List = ListTool.getMp3List(dirFiles);
        System.out.println("本地mp3文件数: " + mp3List.size());

        recordPlay.setMp3List(mp3List);

        adapterLocal.setMp3List(mp3List);
        adapterLocal.setMainActivity(MainActivity.this);
        lv.setAdapter(adapterLocal);
        lv.requestFocus();
        lv.setOnItemClickListener(adapterLocal);
    }

    /**
     * 加载服务器mp3列表
     */
    public void loadOnlineData() {
        // 从服务器的xml中获取Mp3Bean列表
        new Thread(new Runnable() {
            @Override
            public void run() {
                final List<Mp3Bean> mp3List = ListTool.getMp3ListFromServer(MainActivity.this);
                if (mp3List != null) {
                    flag = 1;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapterOnline.setMp3List(mp3List);
                            adapterOnline.setMainActivity(MainActivity.this);
                            lv.setAdapter(adapterOnline);
                            lv.requestFocus();
                            lv.setOnItemClickListener(adapterOnline);

                            recordPlay.setMp3List(mp3List);
                        }
                    });
                }

            }
        }).start();
    }

    /**
     * 给一些按钮注册事件
     */
    public void setButtonClick() {
        Button btLocal = (Button) findViewById(R.id.bt_localMusic);
        btLocal.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadLocalData();
                System.out.println("点击本地音乐");
            }
        });

        Button btOnlione = (Button) findViewById(R.id.bt_onlineMusic);
        btOnlione.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loadOnlineData();
                System.out.println("点击在线音乐");
            }
        });

        // 播放和暂停的按钮
        btMainPlay = (ImageButton) findViewById(R.id.bt_mainplay);
        btMainPlay.setOnClickListener(new OnClickListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onClick(View v) {
                playController.pause();
                if (playController.isPlaying()) {
                    btMainPlay.setImageDrawable(getResources().getDrawable(R.drawable.pause0));
                }
                if (playController.isPause()) {
                    btMainPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));
                }

                System.out.println("点击了暂停按钮");
            }
        });

        // 播放下一首的按钮
        ImageButton btNext = (ImageButton) findViewById(R.id.bt_next);
        btNext.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playController.setBean(recordPlay.nextBean());
                playController.play();
            }
        });

        // 播放上一首的按钮
        ImageButton btLast = (ImageButton) findViewById(R.id.bt_last);
        btLast.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                playController.setBean(recordPlay.lastBean());
                playController.play();
            }
        });

        // 跳转下载界面的按钮
        Button bt_downui = (Button) findViewById(R.id.bt_downloadui);
        bt_downui.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 制定intent要启动的类
                MyIntent.intent.setClass(MainActivity.this, DownActivity.class);

                // 启动一个新的Activity
                startActivity(MyIntent.intent);

                // 关闭当前的
                moveTaskToBack(true);
            }
        });
    }

    /**
     * 控制mp3播放的类
     *
     * @author ZRG
     */
    class PlayController {
        private MediaPlayer mp = new MediaPlayer();
        private Mp3Bean bean;
        private boolean pause = false;
        private boolean stop = false;

        public PlayController() {
        }

        public void setBean(Mp3Bean bean) {
            this.bean = bean;
        }

        public boolean isPlaying() {
            return mp.isPlaying();
        }

        public boolean isPause() {
            return pause;
        }

        public boolean isStop() {
            return stop;
        }

        /**
         * 播放mp3
         *
         * @param
         */
        public void play() {
            try {
                if (bean == null) {
                    System.out.println("bean=null不播放");
                    return;
                }

                //mp.stop();
                mp.reset(); // 之前是seek(0)
                mp.setDataSource(bean.getMp3Path());
                mp.prepare();
                mp.setOnPreparedListener(new OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        System.out.println("开始播放mp3--" + bean.getMp3Name());
                        mp.start();
                    }
                });

/*				mp.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer mp) {
						mp.start();
						stop = false;
						pause = false;
						btMainPlay.setImageDrawable(getResources().getDrawable(R.drawable.pause0));
						System.out.println("开始播放mp3--" + bean.getMp3Name());
					}
				});

				// 监听播放结束
				mp.setOnCompletionListener(new OnCompletionListener() {
					@Override
					public void onCompletion(MediaPlayer mp) {
						stop = true;
						pause = true;
						btMainPlay.setImageDrawable(getResources().getDrawable(R.drawable.play));

						Toast.makeText(getApplicationContext(), "播放完成！" + bean.getMp3Name(), Toast.LENGTH_LONG).show();
						System.out.println("播放完成！" + bean.getMp3Name());
					}
				});*/

            } catch (Exception e) {
                System.out.println("播放异常！" + bean.getMp3Name());
                e.printStackTrace();
            }
        }

        /**
         * 播放暂停和恢复播放
         */
        public void pause() {
            if (mp.isPlaying()) {
                pause = true;
                stop = false;
                mp.pause();
                System.out.println("暂停");
            } else if (pause) {
                mp.start();
                stop = false;
                pause = false;
                System.out.println("恢复播放");
            }
        }

        /**
         * 销毁对象，释放内存
         */
        public void onDestroy() {
            if (mp != null) {
                mp.stop();
                mp.release();
                mp = null;
            }
        }
    }

    /**
     * 按下返回键触发事件
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        playController.onDestroy();
        CommonActivity.downActivity.finish();

        System.out.println("main按下了back键");
    }

    /**
     * 在activity结束的时候回收资源
     */
    @Override
    protected void onDestroy() {
        playController.onDestroy();
        super.onDestroy();
        System.out.println("MainActivity结束");

        CommonActivity.downActivity.onDestroy();
    }
}
