package com.zrg.mp3player.main;

import java.util.List;

import com.zrg.mp3player.bean.Mp3Bean;

public class RecordPlay {
	private int currentPos = -1;
	private Mp3Bean currentBean;
	private List<Mp3Bean> mp3List;

	public int getCurrentPos() {
		return currentPos;
	}

	public void setCurrentPos(int currentPos) {
		this.currentPos = currentPos;
	}

	public Mp3Bean getCurrentBean() {
		return currentBean;
	}

	public void setCurrentBean(Mp3Bean currentBean) {
		this.currentBean = currentBean;
	}

	public List<Mp3Bean> getMp3List() {
		return mp3List;
	}

	public void setMp3List(List<Mp3Bean> mp3List) {
		this.mp3List = mp3List;
		if (!mp3List.isEmpty()) {
			currentPos = 0;
		}
	}

	public Mp3Bean nextBean() {
		if (mp3List.isEmpty()) {
			return null;
		}

		if (currentPos == mp3List.size() - 1) {
			currentPos = 0;
		} else {
			currentPos++;
		}

		return mp3List.get(currentPos);
	}

	public Mp3Bean lastBean() {
		if (mp3List.isEmpty()) {
			return null;
		}

		if (currentPos == 0) {
			currentPos = mp3List.size() - 1;
		} else {
			currentPos--;
		}

		return mp3List.get(currentPos);
	}
}