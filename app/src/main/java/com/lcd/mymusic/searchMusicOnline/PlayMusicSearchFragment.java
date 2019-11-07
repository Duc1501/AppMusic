package com.lcd.mymusic.searchMusicOnline;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.lcd.mymusic.R;
import com.lcd.mymusic.databinding.PlayMusicSearchFragmentBinding;

import java.text.SimpleDateFormat;
import java.util.concurrent.Executors;

public class PlayMusicSearchFragment extends Fragment implements View.OnClickListener {
    private PlayMusicSearchFragmentBinding binding;
    private ServiceConnection conn;
    private ServiceSearchMusicOnline service;
    private Animation animation;
    private boolean isRandomMusic;
    private boolean isRunning;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        binding = PlayMusicSearchFragmentBinding.inflate(inflater, container, false);
        connectService();
        binding.ivBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        isRunning = true;
        setAnimation();
        init();
        startAsyn();
        binding.avatarSong.startAnimation(animation);
        return binding.getRoot();
    }

    public void setAnimation(){
        animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_oval_image_play_music);
    }

    private void connectService(){
        conn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                ServiceSearchMusicOnline.MyBind bind =
                        (ServiceSearchMusicOnline.MyBind) iBinder;
                service = bind.getService();
                updateInfo();

            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {

            }
        };
        Intent intent = new Intent();
        intent.setClassName(getContext(), ServiceSearchMusicOnline.class.getName());
        getContext().bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    private void init() {
        updateInfo();
        binding.btnNextRunningSong.setOnClickListener(this);
        binding.btnPreviousRunningSong.setOnClickListener(this);
        binding.btnPauseAndPlay.setOnClickListener(this);
        binding.ivBtnRepeat.setOnClickListener(this);
        binding.btnShuffleRunningSong.setOnClickListener(this);
    }

    private void updateInfo() {
        if (service == null){
            return;
        }

        ItemMusicSearch itemMusicSearch = service.getCurrentItemSearch();
        if (itemMusicSearch == null){
            return;
        }

        binding.nameSong.setText(itemMusicSearch.getSongName());
        binding.nameSinger.setText(itemMusicSearch.getArtistName());
        if(itemMusicSearch.getLinkImage() != null){
            Glide.with(binding.avatarSong)
                    .load(itemMusicSearch.getLinkImage())
                    .error(R.drawable.logo_music_error)
                    .placeholder(R.drawable.logo_music_error)
                    .into(binding.avatarSong);

            if(itemMusicSearch.getLinkImage() != null) {
                Glide.with(binding.avatarSong)
                        .load(itemMusicSearch.getLinkImage())
                        .error(R.drawable.logo_music_error)
                        .placeholder(R.drawable.logo_music_error)
                        .into(binding.backgroundPlayMusicOnline);
            }
        }

    }

    @Override
    public void onDestroyView() {
        getContext().unbindService(conn);
        isRunning = false;
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if (service == null){
            return;
        }
        MediaPlayer mp = service.getMp();
        switch (view.getId()){
            case R.id.btn_next_running_song:
                service.next();
                updateInfo();
                break;
            case R.id.btn_previous_running_song:
                service.previous();
                updateInfo();
                break;
            case R.id.btn_pause_and_play:
                if (mp != null && mp.isPlaying()){
                    mp.pause();
                    binding.btnPauseAndPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
                }else {
                    mp.start();
                    binding.btnPauseAndPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                }
                break;
            case R.id.iv_btn_repeat:
                if (mp!=null && !mp.isLooping()){
                    mp.setLooping(true);
                    binding.ivBtnRepeat.setImageResource(R.drawable.ic_repeat_one_blue_24dp);
                } else if (mp.isLooping()){
                    mp.setLooping(false);
                    binding.ivBtnRepeat.setImageResource(R.drawable.ic_repeat_one_white_24dp);
                }
                break;
            case R.id.btn_shuffle_running_song:
                if (isRandomMusic) {
                    binding.btnShuffleRunningSong.setImageResource(R.drawable.ic_shuffle_white_24dp);
                    isRandomMusic = true;
                }else {
                    binding.btnShuffleRunningSong.setImageResource(R.drawable.ic_shuffle_blue_24dp);
                    isRandomMusic = false;
                }
                break;

        }
    }

    private void startAsyn() {
        new AsyncTask<Void, Integer, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {

                while (isRunning) {
                    SystemClock.sleep(300);
                    if (service == null || !service.isPreared()) {
                        continue;
                    }

                    MediaPlayer mp = service.getMp();
                    publishProgress(mp.getDuration(),
                            mp.getCurrentPosition());
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                binding.tvTimeSong.setText(
                        new SimpleDateFormat("mm:ss").format(values[0])
                );
                binding.tvTimeSongRunning.setText(
                        new SimpleDateFormat("mm:ss").format(values[1])
                );
                binding.arcSeek.setProgress(
                        (int)(values[1]*100/values[0])
                );
            }
        }.executeOnExecutor(Executors.newFixedThreadPool(1));
    }
}
