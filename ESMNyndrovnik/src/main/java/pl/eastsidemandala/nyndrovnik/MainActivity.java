package pl.eastsidemandala.nyndrovnik;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

/**
 * Created by konrad on 13.06.2013.
 */
public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
//        if (null == savedInstanceState) {
//            NyndroFragment fragment = new NyndroFragment();
//            fragment.setPractice(NyndroFragment.PROSTRATIONS);
//            Bundle args = new Bundle();
//            args.putString("practice", NyndroFragment.Practice.PROSTRATIONS.toString());
//            fragment.setArguments(args);
//            getSupportFragmentManager().beginTransaction().add(R.id.main_layout, fragment).commit();
            PracticePagerAdapter adapter = new PracticePagerAdapter(getSupportFragmentManager());
            ViewPager pager = (ViewPager) findViewById(R.id.pager);
            pager.setAdapter(adapter);
//        }
    }

}


