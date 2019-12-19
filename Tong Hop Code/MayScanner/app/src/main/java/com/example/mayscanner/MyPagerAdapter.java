package com.example.mayscanner;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    private FragmentGrid fragmentImages;
    private FragmentList fragmentPdfs;
    public MyPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    public MyPagerAdapter(FragmentManager fm,FragmentGrid fragmentImages,FragmentList fragmentPdfs) {
        super(fm);
        this.fragmentImages=fragmentImages;
        this.fragmentPdfs=fragmentPdfs;
    }


    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return fragmentImages;
            case 1:
                return fragmentPdfs;
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
            return "IMAGES";
       }
       else if(position==1)
        {
            return "PDFS";
        }
        return "__";
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


    public void notifyDataSetChanged_1(){
        fragmentImages.notifyDataSetChanged();
        fragmentPdfs.notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {

        super.notifyDataSetChanged();
        MainActivity.getFilePaths(MainActivity.getArrImages(),"Images");
        MainActivity.getFilePaths(MainActivity.getArrPdfs(),"PDFs");


    }
}
