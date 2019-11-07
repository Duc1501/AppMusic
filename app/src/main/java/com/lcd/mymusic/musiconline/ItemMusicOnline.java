package com.lcd.mymusic.musiconline;

public class ItemMusicOnline {
    private String linkImage;
    private String songName;
    private String artistName;
    private String linkSong;
    private String linkPlayMusic;

    public ItemMusicOnline(String linkImage, String songName, String artistName, String linkSong) {
        this.linkImage = linkImage;
        this.songName = songName;
        this.artistName = artistName;
        this.linkSong = linkSong;
    }

    public String getLinkImage() {
        return linkImage;
    }

    public void setLinkImage(String linkImage) {
        this.linkImage = linkImage;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public String getArtistName() {
        return artistName;
    }

    public void setArtistName(String artistName) {
        this.artistName = artistName;
    }

    public String getLinkSong() {
        return linkSong;
    }

    public void setLinkSong(String linkSong) {
        this.linkSong = linkSong;
    }

    public String getLinkPlayMusic() {
        return linkPlayMusic;
    }

    public void setLinkPlayMusic(String linkPlayMusic) {
        this.linkPlayMusic = linkPlayMusic;
    }
}
