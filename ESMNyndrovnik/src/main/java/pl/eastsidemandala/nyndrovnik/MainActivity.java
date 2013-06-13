package pl.eastsidemandala.nyndrovnik;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by konrad on 13.06.2013.
 */
public class MainActivity extends FragmentActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.main_layout, new NyndroFragment()).commit();
        }
    }

}


