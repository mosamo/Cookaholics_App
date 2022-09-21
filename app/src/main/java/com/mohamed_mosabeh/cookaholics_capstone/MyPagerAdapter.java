package com.mohamed_mosabeh.cookaholics_capstone;

import android.util.Log;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class MyPagerAdapter extends FragmentStateAdapter {

    /* An adapter, consisting of three fragments */

    public MyPagerAdapter (@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    @NonNull
    @Override
    public Fragment createFragment (int position ) {
        Log.i("FRAG", String.valueOf ( position ));
        switch(position){
            case 0:
                return new Fragment1();
            case 1:
                return new Fragment2();
            case 2:
                return new Fragment3();
        }
        return null;
    }
    @Override
    public int getItemCount(){
        return 3;
    }

}
