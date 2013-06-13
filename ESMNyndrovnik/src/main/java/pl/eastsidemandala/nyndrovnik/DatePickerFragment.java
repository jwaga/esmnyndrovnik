package pl.eastsidemandala.nyndrovnik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;

/**
* Created by konrad on 09.06.2013.
*/
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener mListener;

    public void setListener (DatePickerDialog.OnDateSetListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        MainActivity activity = (MainActivity) getActivity();
        c.setTime(activity.getProjectedFinishDate());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        return new DatePickerDialog(getActivity(), mListener, year, month, day);
    }

}
