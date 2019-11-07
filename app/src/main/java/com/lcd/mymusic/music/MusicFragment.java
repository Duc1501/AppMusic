package com.lcd.mymusic.music;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.lcd.mymusic.MainActivity;
import com.lcd.mymusic.R;
import com.lcd.mymusic.databinding.FragmentMusicBinding;
import com.lcd.mymusic.musiconline.ServiceMusicOnline;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicFragment extends Fragment implements MusicAdapter.IMusic, MediaPlayer.OnCompletionListener, View.OnClickListener {
    private FragmentMusicBinding binding;
    private Animation animation;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private List<MusicOffline> musicOffines;
    private MediaPlayer mp;
    private int songNext=-1;
    private int currentPosition = -1;
    private Random random;
    private boolean btnShuffle = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        musicOffines = new ArrayList<>();
        requestRead();
    }

    public MediaPlayer getMp() {
        return mp;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMusicBinding.inflate(inflater, container, false);
        binding.rcListmusic.setLayoutManager(new LinearLayoutManager(getActivity()));
        binding.rcListmusic.setAdapter(new MusicAdapter(this));
        mp = new MediaPlayer();
        binding.tvTenBaiHatOffline.setText(musicOffines.get(0).getName());
        binding.tvTenCasiOffline.setText(musicOffines.get(0).getNameAtist());
        if (musicOffines.get(0).getPathAlbum() != null &&
                !musicOffines.get(0).getPathAlbum().equals("")){
            Glide.with(getContext())
                    .load(new File(musicOffines.get(0).getPathAlbum()))
                    .into(binding.ivAvatarOffline);
        }
        try {
            mp.setDataSource(musicOffines.get(0).getPath());
            mp.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        setEvenButton();
        setAnimation();
        return binding.getRoot();
    }

    private void setEvenButton(){
        binding.btnPlayOffline.setOnClickListener(this);
        binding.btnNextOffline.setOnClickListener(this);
        binding.btnPreviousOffline.setOnClickListener(this);
        binding.rlBottom.setOnClickListener(this);
    }

    private void setAnimation() {
        animation = AnimationUtils.loadAnimation(getActivity(),R.anim.anim_oval_image_play_music);
    }

    public void requestRead() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            readFile();
        }
    }

    public void readFile() {
        pareAllMusic();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readFile();
            } else {
                // Permission Denied
                Toast.makeText(getContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void pareAllMusic(){
        Cursor c =
                getActivity().getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        null,
                        null,
                        null,
                        "_display_name ASC"
                );
        List<Long> albumIds = new ArrayList<>();
        List<Long> artistIds = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        //_data, _display_name, duration
        int indexData = c.getColumnIndex("_data");
        int indexId = c.getColumnIndex("_id");
        int indexAlbum= c.getColumnIndex("album_id");
        int indexArtist= c.getColumnIndex("artist_id");
        int indexDisplayName = c.getColumnIndex("_display_name");
        int indexDuration = c.getColumnIndex("duration");
        int indexDateAdd = c.getColumnIndex("date_added");
        int indexName = c.getColumnIndex("ARTIST");
        c.moveToFirst();
        while (!c.isAfterLast()){
            String data = c.getString(indexData);
            String name = c.getString(indexDisplayName);
            long duration = c.getLong(indexDuration);
            long date = c.getLong(indexDateAdd);
            long albumId =c.getLong(indexAlbum);
            String artist= c.getString(indexName);
            if (albumId > 0){
                albumIds.add(albumId);
            }
            long artistId =c.getLong(indexArtist);
            if (artistId > 0 ){
                artistIds.add(artistId);
            }
            ids.add(c.getLong(indexId));
            musicOffines.add(new MusicOffline(data, name,
                    duration, date, albumId,artist));
            c.moveToNext();
        }
        c.close();
        if (ids.size() > 0){
            String srtId = "("+albumIds.get(0);
            for ( int i = 1; i < ids.size(); i++){
                srtId+=(","+albumIds.get(i));
            }
            srtId+=")";


            c =
                    getActivity().getContentResolver().query(
                            MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                            null,
                            "_id IN "+srtId,
                            null,
                            null
                    );
            int indexLink = c.getColumnIndex("album_art");
            int indexIdA = c.getColumnIndex("_id");
            c.moveToFirst();
//            album_art
            while (!c.isAfterLast()){
                String album = c.getString(indexLink);
                long id = c.getLong(indexIdA);
                for (MusicOffline musicOffline : musicOffines) {
                    if (musicOffline.getAlbumId() == id){
                        musicOffline.setPathAlbum(album);
                    }
                }
                c.moveToNext();
            }

        }

    }

    @Override
    public int getCount() {
        if (musicOffines == null){
            return 0;
        }
        return musicOffines.size();
    }

    @Override
    public MusicOffline getData(int position) {
        return musicOffines.get(position);
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        next();
        binding.btnPlayOffline.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
        animation.cancel();
    }

    @Override
    public void onClickItem(int position, boolean isOpen) {
        if (musicOffines.get(position).getPath()!=null){
            currentPosition = position;
            if (position == 0 || position == musicOffines.size()-1){
                songNext = position;
            }else {
                songNext = position + 1;
            }
            play(position);
            if(isOpen){
                ((MainActivity)getActivity()).openPlayerMusicOffline();
            }
            return;
        }
        currentPosition = position;
        songNext = position +1;
        if(isOpen){
            ((MainActivity)getActivity()).openPlayerMusicOffline();
        }
        binding.ivAvatarOffline.startAnimation(animation);
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
            mp.prepare();
            mp.start();
            if (mp!=null) {
                if (musicOffines.get(position).getPathAlbum() != null &&
                        !musicOffines.get(position).getPathAlbum().equals("")) {
                    Glide.with(getContext())
                            .load(new File(musicOffines.get(position).getPathAlbum()))
                            .into(binding.ivAvatarOffline);
                }
                binding.tvTenBaiHatOffline.setText(musicOffines.get(position).getName());
                binding.tvTenCasiOffline.setText(musicOffines.get(position).getNameAtist());
                binding.btnPlayOffline.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
                binding.ivAvatarOffline.startAnimation(animation);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void next() {
        if (currentPosition == musicOffines.size() - 1) {
            return;
        }
        onClickItem(currentPosition + 1,false);
        binding.ivAvatarOffline.startAnimation(animation);
        binding.btnPlayOffline.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
    }

    public void previous(){
        if ( currentPosition <=0){
            return;
        }
        onClickItem(currentPosition-1, false);
    }

    public void play(){
        if (mp != null && mp.isPlaying()){
            mp.pause();
            binding.btnPlayOffline.setImageResource(R.drawable.ic_play_circle_outline_white_36dp);
        }else {
            mp.start();
            binding.ivAvatarOffline.startAnimation(animation);
            binding.btnPlayOffline.setImageResource(R.drawable.ic_pause_circle_outline_white_48dp);
        }
    }

    public void btnShuffle(){
        if (btnShuffle = true){
            btnShuffle = false;
        }else {
            btnShuffle = true;

        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_next_offline:
                next();
                break;
            case R.id.btn_previous_offline:
                previous();
                break;
            case R.id.btn_play_offline:
                play();
                break;
            case R.id.rl_bottom:
        }
    }

}
