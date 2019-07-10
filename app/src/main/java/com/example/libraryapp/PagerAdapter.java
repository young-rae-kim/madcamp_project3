package com.example.libraryapp;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.libraryapp.Fragment0.Fragment0;
import com.example.libraryapp.Fragment1.Fragment1;
import com.example.libraryapp.Fragment2.Fragment2;

public class PagerAdapter extends FragmentStatePagerAdapter {
    private int mPageCount;

    public PagerAdapter(FragmentManager fm, int pageCount) {
        super(fm);
        this.mPageCount = pageCount;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Fragment0();
            case 1:
                return new Fragment1();
            case 2:
                return new Fragment2();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mPageCount;
    }
}
