package pl.eastsidemandala.nyndrovnik;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import pl.eastsidemandala.nyndrovnik.NyndroFragment.Practice;

/**
 * Created by konrad on 13.06.2013.
 */
public class MainActivity extends FragmentActivity {
    boolean dmUnlocked, mandalaUnlocked, guruYogaUnlocked;
    Practice mActivePractice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getPreferences(this.MODE_PRIVATE);
        dmUnlocked = prefs.getBoolean("dm_unlocked", false);
        mandalaUnlocked = prefs.getBoolean("mandala_unlocked", false);
        guruYogaUnlocked = prefs.getBoolean("guru_yoga_unlocked", false);
        mActivePractice = Practice.valueOf(
                prefs.getString(PracticeData.ACTIVE_PRACTICE_KEY, Practice.PROSTRATIONS.toString()
                ));
        setContentView(R.layout.main_activity);
//        if (null == savedInstanceState) {
//            NyndroFragment fragment = new NyndroFragment();
//            fragment.setPractice(NyndroFragment.PROSTRATIONS);
//            Bundle args = new Bundle();
//            args.putString("practice", NyndroFragment.Practice.PROSTRATIONS.toString());
//            fragment.setArguments(args);
//            getSupportFragmentManager().beginTransaction().add(R.id.main_layout, fragment).commit();
        PracticePagerAdapter adapter = new PracticePagerAdapter(getSupportFragmentManager(), this);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(mActivePractice.ordinal(), true);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor prefs = getPreferences(MODE_PRIVATE).edit();
        prefs.putBoolean("dm_unlocked", dmUnlocked);
        prefs.putBoolean("mandala_unlocked", mandalaUnlocked);
        prefs.putBoolean("guru_yoga_unlocked", guruYogaUnlocked);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}


