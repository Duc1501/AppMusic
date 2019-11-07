package com.lcd.mymusic.searchMusicOnline;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.lcd.mymusic.musiconline.ItemMusicOnline;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ServiceSearchMusicOnline extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {
    private List<ItemMusicSearch> onlines;
    private MediaPlayer mpOnline;
    private boolean isPreared;
    private int currentPosition = -1;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind(this);
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mpOnline.start();
        isPreared = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    public ItemMusicSearch getCurrentItemSearch(){
        return onlines.get(currentPosition);
    }

    public void setSearchOnline(List<ItemMusicSearch> itemMusicSearches) {
        onlines = itemMusicSearches;
    }

    public boolean checkEmpty() {
        return onlines == null || onlines.size() == 0;
    }

    public MediaPlayer getMp(){
        return mpOnline;
    }

    public int sizeItemMusicSearch() {
        if (onlines == null){
            return 0;
        }
        return onlines.size();
    }

    public ItemMusicSearch getData(int position) {
        return onlines.get(position);
    }

    public int getCurrenPosition() {
        return currentPosition;
    }

    public void onClickItem(int position) {
        isPreared = false;
        if (onlines.get(position).getLinkPlayMusic() != null){
            play(position);
            currentPosition = position;
            return;
        }
        getLinkMp3(onlines.get(position).getLinkSong(), position);
        currentPosition = position;
    }

    public static class MyBind extends Binder{
        private ServiceSearchMusicOnline service;

        public MyBind(ServiceSearchMusicOnline service) {
            this.service = service;
        }

        public ServiceSearchMusicOnline getService() {
            return service;
        }
    }

    public void next(){
        isPreared = false;
        if ( currentPosition == onlines.size()-1){
            return;
        }
        currentPosition = currentPosition+1;
        if (onlines.get(currentPosition).getLinkPlayMusic() == null){
            getLinkMp3(
                    onlines.get(currentPosition).getLinkSong(),
                    currentPosition
            );
        }else {
            play(currentPosition);
        }
    }

    public void previous(){
        isPreared = false;
        if ( currentPosition <=0){
            return;
        }
        currentPosition = currentPosition-1;
        if (onlines.get(currentPosition).getLinkPlayMusic() == null){
            getLinkMp3(
                    onlines.get(currentPosition).getLinkSong(),
                    currentPosition
            );
        }else {
            play(currentPosition);
        }
    }

    public void randomMusic(){
        isPreared= false;
        Random random = new Random();
        currentPosition = random.nextInt(onlines.size());
        if(onlines.get(currentPosition).getLinkPlayMusic()==null){
            getLinkMp3(onlines.get(currentPosition).getLinkSong(),
                    currentPosition);
        }else{
            play(currentPosition);
        }
    }

    public boolean isPreared(){
        return isPreared;
    }

    public void getLinkMp3(String linkHtml, final int position){
        new AsyncTask<String, Void, String>(){
            @Override
            protected String doInBackground(String... strings) {
                try {
                    Document c =  Jsoup.connect(strings[0]).get();
                    Elements els =
                            c.select("div.tab-content").first().select("a.download_item");
                    if (els.size() >= 2){
                        return els.get(1).attr("href");
                    }else {
                        return els.get(0).attr("href");
                    }

                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                onlines.get(position).setLinkPlayMusic(s);
                if (s != null){
                    play(position);
                }
            }
        }.execute(linkHtml);
    }

    private void play(int position) {
        if (mpOnline != null ){
            mpOnline.release();
        }
        mpOnline = new MediaPlayer();
        try {
            mpOnline.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mpOnline.setDataSource(this,
                    Uri.parse(onlines.get(position)
                            .getLinkPlayMusic()));
            mpOnline.setOnBufferingUpdateListener(this);
            mpOnline.setOnCompletionListener(this);
            mpOnline.setOnPreparedListener(this);
            mpOnline.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
