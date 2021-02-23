package com.zrg.mp3player.bean;

public class Mp3Bean {
    private String mp3Name;
    private long mp3Size;
    private String mp3Path;

    public String getMp3Path() {
        return mp3Path;
    }

    public void setMp3Path(String mp3Path) {
        this.mp3Path = mp3Path;
    }

    public void setMp3Size(long l) {
        this.mp3Size = l;
    }

    public long getMp3Size() {
        return mp3Size;
    }

    public String getMp3Name() {
        return mp3Name;
    }

    public void setMp3Name(String mp3Name) {
        this.mp3Name = mp3Name;
    }

}
