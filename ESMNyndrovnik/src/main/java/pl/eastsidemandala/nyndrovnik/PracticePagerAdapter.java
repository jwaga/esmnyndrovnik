package pl.eastsidemandala.nyndrovnik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by konrad on 14.06.2013.
 */
public class PracticePagerAdapter extends FragmentPagerAdapter {
    public PracticePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        NyndroFragment fragment = new NyndroFragment();
        Bundle args = new Bundle();
        args.putString("practice", NyndroFragment.Practice.values()[i].toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 4;
    }
}
