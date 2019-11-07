package com.lcd.mymusic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.lcd.mymusic.common.Utils;
import com.lcd.mymusic.databinding.FragmentMainBinding;
import com.lcd.mymusic.musiconline.MusicOnlineFragment;
import com.lcd.mymusic.music.MusicFragment;
import com.lcd.mymusic.searchMusicOnline.SearchOnlineFragment;

public class MainFragment extends Fragment {

    private FragmentMainBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMainBinding.inflate(inflater, container, false);
        init();
        binding.btnSearchSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity)getActivity()).openSearch();

            }
        });
        return binding.getRoot();
    }

    private void init(){
        MusicOnlineFragment musicOnlineFragment = new MusicOnlineFragment();
        loadFragment(musicOnlineFragment);
        BottomNavigationView bottomNavigationView = binding.botomNavigation;
        bottomNavigationView.setOnNavigationItemSelectedListener(naviListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener naviListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    FragmentManager manager = getChildFragmentManager();
                    FragmentTransaction transaction =manager.beginTransaction();
                    Fragment fragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.btn_music_online :
                            fragment = manager.findFragmentByTag(MusicOnlineFragment.class.getName());
                            if (fragment != null && fragment.isVisible()){
                                return true;
                            }
                            if (fragment != null && !fragment.isVisible()){
                                Utils.hideAllFragment(manager, transaction);
                                transaction.show(fragment);
                                transaction.commit();
                                return true;
                            }
                            fragment = new MusicOnlineFragment();
                            Utils.hideAllFragment(manager, transaction);
                            transaction.add(R.id.frame_container,fragment, fragment.getClass().getName());
                            transaction.commit();
                            return true;
                        case R.id.nav_music:
                            fragment = manager.findFragmentByTag(MusicFragment.class.getName());
                            if (fragment != null && fragment.isVisible()){
                                return true;
                            }
                            if (fragment != null && !fragment.isVisible()){
                                Utils.hideAllFragment(manager, transaction);
                                transaction.show(fragment);
                                transaction.commit();
                                return true;
                            }
                            fragment = new MusicFragment();
                            Utils.hideAllFragment(manager, transaction);
                            transaction.add(R.id.frame_container,fragment, fragment.getClass().getName());
                            transaction.commit();
                            return true;
                        case  R.id.nav_favorite :
                            fragment = manager.findFragmentByTag(VideoFragmment.class.getName());
                            if (fragment != null && fragment.isVisible()){
                                return true;
                            }
                            if (fragment != null && !fragment.isVisible()){
                                Utils.hideAllFragment(manager, transaction);
                                transaction.show(fragment);
                                transaction.commit();
                                return true;
                            }
                            fragment = new VideoFragmment();
                            Utils.hideAllFragment(manager, transaction);
                            transaction.add(R.id.frame_container,fragment, fragment.getClass().getName());
                            transaction.commit();
                            return true;
                    }
                    return true;

                }
            };

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


}
