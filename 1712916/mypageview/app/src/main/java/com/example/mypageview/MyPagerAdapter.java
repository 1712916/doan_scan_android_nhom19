package com.example.mypageview;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {
   //private List<Fragment> fragmentList=new ArrayList<>();

    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public  void add(Fragment fragment){
       // fragmentList.add(fragment);
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return new FragmentGrid();
            case 1:
                return new FragmentList();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }
    @Override
    public CharSequence getPageTitle(int position) {
       if(position==0)
       {
            return "GRID VIEW";
       }
       else if(position==1)
        {
            return "LIST VIEW";
        }
        return "__";
    }
}
