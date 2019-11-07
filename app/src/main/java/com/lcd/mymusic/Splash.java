package com.lcd.mymusic;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.lcd.mymusic.databinding.SplashBinding;

public class Splash extends AppCompatActivity {
    private SplashBinding binding;
    private Animation animation, animLogo;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.splash);
        animation = AnimationUtils.loadAnimation(this,R.anim.anim_splash);
        animLogo = AnimationUtils.loadAnimation(this, R.anim.anim_logo_splash);
        binding.logoSplash.startAnimation(animLogo);
        binding.tvSplash.startAnimation(animation);
        final Intent intent = new Intent(this, MainActivity.class);
        Thread time = new Thread(){
            public void run(){
                try {
                    sleep(4500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finally {
                    startActivity(intent);
                    finish();
                }
            }
        };
        time.start();
    }
}
