package com.lcd.mymusic.common;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class Utils {
    public static void hideAllFragment(FragmentManager manager, FragmentTransaction transaction){
        // Lấy ra tất cả các fragment được quản lý bởi manager
        List<Fragment> fragments= manager.getFragments();
        for (Fragment fragment : fragments) {
            if (fragment != null){
                if(fragment.isVisible()){
                    transaction.hide(fragment);
                }
            }
        }
    }
}
