package com.lcd.mymusic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;

import com.lcd.mymusic.common.Utils;
import com.lcd.mymusic.music.MusicFragment;
import com.lcd.mymusic.music.MusicOffline;
import com.lcd.mymusic.music.RunningSongFragment;
import com.lcd.mymusic.musiconline.MusicOnlineFragment;
import com.lcd.mymusic.musiconline.PlayMusicOnlineFragment;
import com.lcd.mymusic.musiconline.ServiceMusicOnline;
import com.lcd.mymusic.searchMusicOnline.PlayMusicSearchFragment;
import com.lcd.mymusic.searchMusicOnline.SearchOnlineFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        startService();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, new MainFragment(),
                        MainFragment.class.getName())
        .commit();
        openPlayClick(getIntent());

    }
    public void openRuningSong(MediaPlayer mp, MusicOffline item, List<MusicOffline> listMusic){
        FragmentManager manager = getSupportFragmentManager();
        RunningSongFragment fg = new RunningSongFragment();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.hide(manager.findFragmentByTag(MainFragment.class.getName()))
                .add(R.id.content, fg, RunningSongFragment.class.getName());
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void openPlayerMusicOffline(){
        RunningSongFragment fg = new RunningSongFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .hide(getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName()))
                .add(R.id.content, fg, RunningSongFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public void openSearch(){
        SearchOnlineFragment fg = new SearchOnlineFragment();
        getSupportFragmentManager()
                .beginTransaction().hide(getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName()))
                .add(R.id.content, fg, SearchOnlineFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public void openPlaySearch(){
        PlayMusicSearchFragment fg = new PlayMusicSearchFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .hide(getSupportFragmentManager().findFragmentByTag(SearchOnlineFragment.class.getName()))
                .add(R.id.content, fg, PlayMusicSearchFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public void openPlayer(){
        PlayMusicOnlineFragment fg = new PlayMusicOnlineFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .hide(getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName()))
                .add(R.id.content, fg, PlayMusicOnlineFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public MusicOnlineFragment getMusicOnlineFragment(){
        MainFragment fragment = (MainFragment)getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName());
        if (fragment == null ){
            return null;
        }
        return (MusicOnlineFragment)
                fragment.getChildFragmentManager()
                        .findFragmentByTag(MusicOnlineFragment.class.getName());
    }

    public MusicFragment getMusicFragment(){
        MainFragment fragment = (MainFragment)getSupportFragmentManager().findFragmentByTag(MainFragment.class.getName());
        if (fragment == null ){
            return null;
        }
        return (MusicFragment)
                fragment.getChildFragmentManager()
                        .findFragmentByTag(MusicFragment.class.getName());
    }

    private void startService(){
        //unbound
        Intent intent = new Intent();
        intent.setClassName(this, ServiceMusicOnline.class.getName());
        startService(intent);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        openPlayClick(intent);
    }

    public void openPlayClick(Intent intent) {
        String key = intent.getStringExtra("KEY");
        if ("NEW_PLAY".equals(key)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    openPlay();
                }
            }, 300);
        }
    }

    private void openPlay() {
        FragmentManager manager =
                getSupportFragmentManager();
        PlayMusicOnlineFragment fr = (PlayMusicOnlineFragment)
                manager.findFragmentByTag(
                        PlayMusicOnlineFragment.class.getName());

        if (fr == null){
            openPlayer();
            return;
        }
        if (fr.isVisible() == false){
            FragmentTransaction transaction = manager.beginTransaction();
            Utils.hideAllFragment(manager, transaction);
            transaction.show(fr)
                    .commit();
        }
    }
}
