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
        args.putString("practice", NyndroFragment.Practice.values()[i].toString());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        return 6;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        int[] practice_page_names = {R.string.short_refuge_short, R.string.prostrations_short, R.string.diamond_mind_short,
            R.string.mandala_offering_short, R.string.guru_yoga_short, R.string.amitabha_short};
        return mContext.getResources().getString(practice_page_names[position]);

    }
}
