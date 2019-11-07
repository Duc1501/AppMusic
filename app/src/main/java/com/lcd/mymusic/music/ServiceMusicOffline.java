package com.lcd.mymusic.music;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.lcd.mymusic.MainActivity;
import com.lcd.mymusic.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class ServiceMusicOffline extends Service implements MediaPlayer.OnCompletionListener {
    private boolean isPreared;
    private List<MusicOffline> musicOffines;
    private MediaPlayer mp;
    private int songNext=0;
    private int currentPosition = -1;

    public MediaPlayer getMp() {
        return mp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MyBind(this);
    }

    public List<MusicOffline> getMusicOffines() {
        return musicOffines;
    }

    public void setMusicOffines(List<MusicOffline> musicOffines) {
        this.musicOffines = musicOffines;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    public int sizeItemMusicOffline() {
        if (musicOffines == null){
            return 0;
        }
        return musicOffines.size();
    }

    public MusicOffline getData(int position) {
        return musicOffines.get(position);
    }

    public void onClickItem(int position) {
        isPreared = false;
        if (musicOffines.get(position).getPath()!=null){
            currentPosition = position;
            songNext = position + 1;
            play(position);
            return;
        }
        currentPosition = position;
        songNext = position +1;
    }

    public static class MyBind extends Binder{
        private ServiceMusicOffline serviceOff;

        public MyBind(ServiceMusicOffline serviceOff) {
            this.serviceOff = serviceOff;
        }

        public ServiceMusicOffline getServiceOff() {
            return serviceOff;
        }
    }

    public MusicOffline getCurrentItem(){
        return musicOffines.get(currentPosition);
    }

    public MusicOffline getCurrentItemNext(){
        return musicOffines.get(songNext);
    }

    private void play(int position){
        if (mp != null){
            mp.release();
        }
        mp = new MediaPlayer();
        try {
            mp.setDataSource(musicOffines.get(position).getPath());
            mp.setOnCompletionListener(this);
            mp.setLooping(true);
            mp.prepare();
            mp.start();
            isPreared = true;
//            if (mp!=null) {
//                if (musicOffines.get(position).getPathAlbum() != null &&
//                        !musicOffines.get(position).getPathAlbum().equals("")) {
//                    Glide.with(getContext())
//                            .load(new File(musicOffines.get(position).getPathAlbum()))
//                            .into(binding.ivAvatarOffline);
//                }
//                binding.tvTenBaiHatOffline.setText(musicOffines.get(position).getName());
//                binding.tvTenCasiOffline.setText(musicOffines.get(position).getNameAtist());
//                binding.btnPlayOffline.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
//                binding.ivAvatarOffline.startAnimation(animation);
//            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void next(){
        isPreared = false;
        if ( currentPosition == musicOffines.size()-1){
            return;
        }
        onClickItem(currentPosition+1);
    }

    public void previous(){
        isPreared = false;
        if ( currentPosition <=0){
            return;
        }
        onClickItem(currentPosition-1);
    }

    public boolean checkEmpty() {
        return musicOffines == null || musicOffines.size() == 0;
    }

    public boolean isPreared() {
        return isPreared;
    }
}
