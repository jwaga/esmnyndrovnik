package pl.eastsidemandala.nyndrovnik;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;

/**
 * Created by konrad on 13.06.2013.
 */
public class MainActivity extends FragmentActivity {

    Practice mActivePractice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getPreferences(this.MODE_PRIVATE);
        mActivePractice = Practice.valueOf(
                prefs.getString(PracticeData.ACTIVE_PRACTICE_KEY, Practice.PROSTRATIONS.toString()
                ));
        setContentView(R.layout.main_activity);
        PracticePagerAdapter adapter = new PracticePagerAdapter(getSupportFragmentManager(), this);
        final ViewPager pager = (ViewPager) findViewById(R.id.pager);
        pager.setAdapter(adapter);
        pager.setCurrentItem(mActivePractice.ordinal(), true);

        SplashScreen splash = (SplashScreen) getSupportFragmentManager().findFragmentByTag("splash_screen");
        if (splash == null && savedInstanceState == null) {
            splash = new SplashScreen();
            splash.show(getSupportFragmentManager(), "splash_screen");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}


