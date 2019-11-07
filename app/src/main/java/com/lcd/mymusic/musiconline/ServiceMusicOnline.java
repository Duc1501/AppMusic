package com.lcd.mymusic.musiconline;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.lcd.mymusic.MainActivity;
import com.lcd.mymusic.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class ServiceMusicOnline extends Service implements MediaPlayer.OnBufferingUpdateListener,
        MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    private boolean isPreared;
    private MediaPlayer mpOnline;
    private int currentPosition =-1;
    private List<ItemMusicOnline> onlines;
    private OnCompleteMusicListenner onCompleteMusicListenner;

    public void setOnCompleteMusicListenner(OnCompleteMusicListenner onCompleteMusicListenner) {
        this.onCompleteMusicListenner = onCompleteMusicListenner;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind(this);
    }

    public void setMusicOnline(List<ItemMusicOnline> itemMusicOnlines) {
        onlines = itemMusicOnlines;
    }

    public int sizeItemMusicOnline() {
        if (onlines == null){
            return 0;
        }
        return onlines.size();
    }

    public ItemMusicOnline getData(int position) {
        return onlines.get(position);
    }

    public boolean checkEmpty() {
        return onlines == null || onlines.size() == 0;
    }

    public MediaPlayer getMp() {
        return mpOnline;
    }

    public void playMusicOnline(int position){
        if (mpOnline != null ){
            mpOnline.release();
        }
        mpOnline = new MediaPlayer();
        try {
            mpOnline.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mpOnline.setDataSource(this,
                    Uri.parse(onlines.get(position).getLinkPlayMusic()));
            mpOnline.setOnBufferingUpdateListener(this);
            mpOnline.setOnCompletionListener(this);
            mpOnline.setOnPreparedListener(this);
            mpOnline.prepareAsync();
            createNotification();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        onCompleteMusicListenner.musicCompleter();
    }

    public static class MyBind extends Binder{

        private ServiceMusicOnline service;

        public MyBind(ServiceMusicOnline service) {
            this.service = service;
        }

        public ServiceMusicOnline getService() {
            return service;
        }
    }

    public ItemMusicOnline getCurrentItem(){
        return onlines.get(currentPosition);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mpOnline.start();
        isPreared = true;
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
            playMusicOnline(currentPosition);
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
            playMusicOnline(currentPosition);
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
            playMusicOnline(currentPosition);
        }
    }

    public void onClickItem(int position) {
        isPreared = false;
        if (onlines.get(position).getLinkPlayMusic() != null){
            playMusicOnline(position);
            currentPosition = position;
            return;
        }
        getLinkMp3(onlines.get(position).getLinkSong(), position);
        currentPosition = position;
    }

    public boolean isPreared() {
        return isPreared;
    }

    public void getLinkMp3(String linkHtml, final int position){
        linkHtml = "https://chiasenhac.vn"+linkHtml;
        new AsyncTask<String, Void, String>(){

            @Override
            protected String doInBackground(String... strings) {
                try {
                    Document c = Jsoup.connect(strings[0]).get();
                    Elements els =
                            c.select("div.tab-content").first().select("a.download_item");
                    if (els.size()>=2){
                        return els.get(1).attr("href");
                    }else {
                        return els.get(0).attr("href");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                onlines.get(position).setLinkPlayMusic(s);
                if (s!=null){
                    playMusicOnline(position);
                }
            }
        }.execute(linkHtml);
    }

    private void createNotification(){
        ItemMusicOnline data = getCurrentItem();

        Intent intentOpen = new Intent();
        intentOpen.setClassName(this,
                MainActivity.class.getName()
        );
        intentOpen.putExtra("KEY", "NEW_PLAY");

        //tao Pending
        PendingIntent pending = PendingIntent.getActivity(this,
                100, intentOpen, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification no = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_pause_circle_filled_black_24dp)
                .setLargeIcon(
                        BitmapFactory.decodeResource(getResources(),
                                R.drawable.logo_music_error)
                )
                .setContentTitle(data.getSongName())
                .setContentText(data.getArtistName())
                .setContentIntent(pending)
                .build();
        startForeground(10, no);
    }

    public interface OnCompleteMusicListenner{
        void musicCompleter();
    }
}
