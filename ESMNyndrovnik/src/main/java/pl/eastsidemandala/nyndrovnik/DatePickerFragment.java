package pl.eastsidemandala.nyndrovnik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

/**
* Created by konrad on 09.06.2013.
*/
public class DatePickerFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        MainActivity activity = (MainActivity) getActivity();
        c.setTime(activity.getProjectedFinishDate());
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH);
        int year = c.get(Calendar.YEAR);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }
    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        MainActivity activity = (MainActivity) getActivity();
        activity.setProjectedFinishDate(c.getTime());
        activity.computePace();
        activity.refresh();


    }

}
