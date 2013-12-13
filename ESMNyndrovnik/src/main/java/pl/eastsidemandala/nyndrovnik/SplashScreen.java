package pl.eastsidemandala.nyndrovnik;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * Created by konrad on 09.12.2013.
 */
public class SplashScreen extends DialogFragment implements View.OnClickListener {
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


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.splash_button_go:
                dismiss();
                break;
            case R.id.splash_button_more:
                Intent intent = new Intent(getActivity(), AboutActivity.class);
                startActivity(intent);
                dismiss();
                break;

        }
    }
}