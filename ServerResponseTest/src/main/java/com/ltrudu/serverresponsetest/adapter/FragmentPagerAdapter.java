package com.ltrudu.serverresponsetest.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.ltrudu.serverresponsetest.fragment.TestFragment;
import com.ltrudu.serverresponsetest.fragment.ServerListFragment;
import com.ltrudu.serverresponsetest.fragment.SettingsFragment;

public class FragmentPagerAdapter extends FragmentStateAdapter {
    
    public FragmentPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new TestFragment();
            case 1:
                return new ServerListFragment();
            case 2:
                return new SettingsFragment();
            default:
                return new TestFragment();
        }
    }
    
    @Override
    public int getItemCount() {
        return 3;
    }
}