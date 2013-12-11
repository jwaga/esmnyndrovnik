package pl.eastsidemandala.nyndrovnik;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by konrad on 09.12.2013.
 */
public class SplashScreen extends Fragment implements View.OnClickListener {
    public interface Splashable {
        void closeSplashScreen();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.splash_screen, container, false);
        Button buttonGo = (Button) view.findViewById(R.id.splash_button_go);
        Button buttonMore = (Button) view.findViewById(R.id.splash_button_more);
        buttonGo.setOnClickListener(this);
        buttonMore.setOnClickListener(this);
        return view;
    }


    public void close() {
        FragmentActivity a = getActivity();
        a.getSupportFragmentManager().beginTransaction().
                setTransition(FragmentTransaction.TRANSIT_EXIT_MASK).remove(this).commit();
        ((Splashable) a).closeSplashScreen();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.splash_button_go:
                close();

        }
    }
}