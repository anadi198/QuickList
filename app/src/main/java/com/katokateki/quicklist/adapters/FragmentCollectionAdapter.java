package com.katokateki.quicklist.adapters;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.katokateki.quicklist.fragments.StoreListingsFragment;
import com.katokateki.quicklist.fragments.UserListingsFragment;

public class FragmentCollectionAdapter  extends FragmentStateAdapter {
    public FragmentCollectionAdapter(FragmentActivity fragment) {
        super(fragment);
    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if(position==0)
        {
            return new StoreListingsFragment();
        }
        else
        {
            return new UserListingsFragment();
        }
    }
    @Override
    public int getItemCount() {
        return 2;
    }
}
