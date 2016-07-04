package pl.eastsidemandala.nyndrovnik;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by konrad on 14.06.2013.
 */
public class PracticePagerAdapter extends FragmentPagerAdapter {

    Context mContext;

    public PracticePagerAdapter(FragmentManager fm, Context c) {
        super(fm);
        mContext = c;
    }

    @Override
    public Fragment getItem(int i) {
        NyndroFragment fragment = new NyndroFragment();
        Bundle args = new Bundle();
        args.putString("practice", Practice.values()[i].toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return Practice.values().length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(Practice.values()[position].getNameRes());

    }
}
