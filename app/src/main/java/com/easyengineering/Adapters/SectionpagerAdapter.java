package com.easyengineering.Adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.easyengineering.Login_Section;
import com.easyengineering.Register_Section;


public class SectionpagerAdapter extends FragmentPagerAdapter {

    public SectionpagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0 :
                return new Login_Section();
            case 1 :
                return new Register_Section();
            default:
                return null;
         }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
