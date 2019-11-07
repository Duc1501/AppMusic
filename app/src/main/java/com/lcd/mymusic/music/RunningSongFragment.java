package com.lcd.mymusic.music;

import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import com.lcd.mymusic.MainActivity;
import com.lcd.mymusic.R;
import com.lcd.mymusic.databinding.RunningSongBinding;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RunningSongFragment extends Fragment implements MediaPlayer.OnCompletionListener, View.OnClickListener {
    private RunningSongBinding binding;
    Animation animation;
    private boolean btnShuffle = false;
    private boolean isRunning;
    private ExecutorService ex;
    private MyAsyn asy;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding= RunningSongBinding.inflate(inflater,container,false);
        setAnimation();
        isRunning= true;
        initAsyn();
        btnFavorite();
        binding.circleImageSong.startAnimation(animation);
        setEventButton();
        updateInfoSongNext();
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });
        return binding.getRoot();
    }

    private void setEventButton(){
        updateInfo();
        binding.btnNextRunningSong.setOnClickListener(this);
        binding.btnPreviousRunningSong.setOnClickListener(this);
        binding.ivBtnRepeat.setOnClickListener(this);
        binding.btnShuffleRunningSong.setOnClickListener(this);
        binding.btnPauseAndPlay.setOnClickListener(this);
        binding.songNext.setOnClickListener(this);

    }

    private void updateInfo(){
        MusicOffline msOff = ((MainActivity)getActivity()).getMusicFragment().getCurrentItem();
        binding.tvNameSongRunningsong.setText(msOff.getName());
        binding.tvRunningSongNameSinger.setText(msOff.getNameAtist());
        binding.tvTimeSong.setText(new SimpleDateFormat("mm:ss").format(msOff.getDuration()));
        if (msOff.getPathAlbum() != null &&
                !msOff.getPathAlbum().equals("")){
            Glide.with(getContext())
                    .load(new File(msOff.getPathAlbum()))
                    .into(binding.circleImageSong);
        }
        if (msOff.getPathAlbum() != null &&
                !msOff.getPathAlbum().equals("")){
            Glide.with(getContext())
                    .load(new File(msOff.getPathAlbum()))
                    .into(binding.backgroundRunningSong);
        }
    }

    private void updateInfoSongNext(){
        MusicOffline msOff = ((MainActivity)getActivity()).getMusicFragment().getCurrentItemNext();
        binding.tvNameSongOfflineNext.setText(msOff.getName());
        binding.nameSingerOfflineNext.setText(msOff.getNameAtist());
        binding.timeSongNext.setText(new SimpleDateFormat("mm:ss").format(msOff.getDuration()));
        if (msOff.getPathAlbum() != null &&
                !msOff.getPathAlbum().equals("")){
            Glide.with(getContext())
                    .load(new File(msOff.getPathAlbum()))
                    .into(binding.avatarOfflineSongNext);
        }
    }

    private void btnFavorite() {
        binding.imYeuthich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    binding.imYeuthich.setImageResource(R.drawable.ic_favorite_red_a700_36dp);
            }
        });
    }


    public void setAnimation(){
        animation = AnimationUtils.loadAnimation(getActivity(),R.anim.anim_oval_image_play_music);
    }

    private void initAsyn(){
        if (ex != null){
            ex.shutdown();
        }
        ex =  Executors.newFixedThreadPool(1);
        asy = new MyAsyn();
        asy.executeOnExecutor(ex);
    }

    private class MyAsyn extends AsyncTask<Void, Integer, Void>{
        private boolean isRun=true;
        @Override
        protected Void doInBackground(Void... voids) {
            while (isRunning && isRun){
                try {
                    MediaPlayer mpOff = ((MainActivity)getActivity()).getMusicFragment().getMp();
                    publishProgress(mpOff.getCurrentPosition(), mpOff.getDuration());
                }catch (RuntimeException  e){
                    e.printStackTrace();
                }


                SystemClock.sleep(300);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            if (!isRun || values[1] == 0){
                return;
            }
            binding.arcSeek.setProgress(values[0] * 100/values[1]);
            binding.tvTimeSongRunning.setText(
                    new SimpleDateFormat("mm:ss").format(values[0]));
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        isRunning = false;
        super.onDestroyView();
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        binding.btnPauseAndPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
        animation.cancel();
    }

    @Override
    public void onClick(View view) {
        MediaPlayer mpOff = ((MainActivity)getActivity()).getMusicFragment().getMp();
        switch (view.getId()){
            case R.id.btn_pause_and_play:
                if (mpOff != null && mpOff.isPlaying()){
                    mpOff.pause();
                    binding.btnPauseAndPlay.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
                }else {
                    mpOff.start();
                    binding.btnPauseAndPlay.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                }
                break;
            case R.id.btn_next_running_song:
                if(asy != null){
                    asy.isRun=false;
                }
                ((MainActivity)getActivity()).getMusicFragment().next();
                updateInfo();
                initAsyn();
                updateInfoSongNext();
                break;
            case R.id.btn_previous_running_song:
                if(asy != null){
                    asy.isRun=false;
                }
                ((MainActivity)getActivity()).getMusicFragment().previous();
                updateInfo();
                initAsyn();
                updateInfoSongNext();
                break;
            case R.id.song_next:
                if(asy != null){
                    asy.isRun=false;
                }
                ((MainActivity)getActivity()).getMusicFragment().next();
                updateInfo();
                initAsyn();
                updateInfoSongNext();
                break;
            case R.id.iv_btn_repeat :
                if (mpOff!=null && !mpOff.isLooping()){
                    mpOff.setLooping(true);
                    binding.ivBtnRepeat.setImageResource(R.drawable.ic_repeat_one_blue_24dp);
                } else if (mpOff.isLooping()){
                    mpOff.setLooping(false);
                    binding.ivBtnRepeat.setImageResource(R.drawable.ic_repeat_one_white_24dp);
                }
                break;
            case R.id.btn_shuffle_running_song:
//                if (btnShuffle = true){
//                    ((MainActivity)getActivity()).getMusicFragment().btnShuffle();
//                    binding.btnShuffleRunningSong.setImageResource(R.drawable.ic_shuffle_blue_a700_24dp);
//                }else {
//                    binding.btnShuffleRunningSong.setImageResource(R.drawable.ic_shuffle_grey_300_24dp);
//                }

                break;
        }
    }

}
