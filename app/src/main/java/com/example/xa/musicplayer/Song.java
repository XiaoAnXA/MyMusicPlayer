package com.example.xa.musicplayer;

public class Song {

    private String fileName;
    private String title;
    private int duration;//时长
    private String singer;
    private String album;//专辑
    private String size;
    private String filePath;

    public Song(String fileName, String title, int duration, String singer, String album, String size, String filePath) {
        this.fileName = fileName;
        this.title = title;
        this.duration = duration;
        this.singer = singer;
        this.album = album;
        this.size = size;
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
}
