package com.lcd.mymusic.formlogin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.lcd.mymusic.R;

public class FormLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_login);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_form_login, new LoginFragment(), LoginFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public void openRegister() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_form_login, new RegisterFragment(), RegisterFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }

    public void onpenLogin() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_form_login, new LoginFragment(), LoginFragment.class.getName())
                .addToBackStack(null)
                .commit();
    }
}
