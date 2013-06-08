package pl.eastsidemandala.nyndrovnik;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        EditCounterDialogFragment.EditCounterDialogListener {

    public static final String PROSTRATIONS_COUNTER_KEY = "prostrations_counter";
    public static final String DATE_OF_LAST_PRACTICE_KEY = "date_of_last_practice";
    public static final String PROSTRATIONS_PACE_KEY = "prostrations_pace";

    private static final int DEFAULT_PACE = 100;

    private int mMainCounter;
    private int mPace = 100;
    private Date mDateOfLastPractice;
    private Date mProjectedFinishDate;
    private DateFormat mDateFormat = SimpleDateFormat.getDateInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mMainCounter = prefs.getInt(PROSTRATIONS_COUNTER_KEY, 0);
        mPace = prefs.getInt(PROSTRATIONS_PACE_KEY, DEFAULT_PACE);
        long date = prefs.getLong(DATE_OF_LAST_PRACTICE_KEY, 0);
        mDateOfLastPractice = new Date();
        if (date > 0) { mDateOfLastPractice.setTime(date); }
//        setUpSpinner();
        computeProjectedFinishDate();
        refresh();
    }

/*
    private void setUpSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.pace_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pace_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new PaceSelectedListener());
    }
*/

    @Override
    protected void onStop() {
        super.onStop();
        getPreferences(MODE_PRIVATE).edit()
                .putInt(PROSTRATIONS_COUNTER_KEY, mMainCounter)
                .putLong(DATE_OF_LAST_PRACTICE_KEY, mDateOfLastPractice.getTime())
                .putInt(PROSTRATIONS_PACE_KEY, mPace)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_counter:
                EditCounterDialogFragment dialog = new EditCounterDialogFragment();
                dialog.setInitialValue(mMainCounter);
                dialog.show(getSupportFragmentManager(), "counter_edit");
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        mMainCounter += mPace;
        if (mMainCounter > 111111) { mMainCounter = 111111; }
        mDateOfLastPractice = new Date();
        computeProjectedFinishDate();
        refresh();
    }

    public void onPaceClick(View view) {
        DialogFragment frag = new PacePickerFragment();
        frag.show(getSupportFragmentManager(), "pace_dialog");
    }

    public void onDateClick(View view) {
        DialogFragment frag = new DatePickerFragment();
        frag.show(getSupportFragmentManager(), "date_dialog");
    }

    @Override
    public void onEditCounterDialogPositiveClick(int value) {
        mMainCounter = value;
        computeProjectedFinishDate();
        refresh();
    }

    protected void computeProjectedFinishDate() {
//      remaining repetitions divided by currently selected pace, plus one day for the remainder
        int remainingDays = 0;
        if (mPace > 0) {
             remainingDays = (111111 - mMainCounter) / mPace + 1;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, remainingDays);
        mProjectedFinishDate = c.getTime();
    }

    private void computePace() {
        long now = new Date().getTime();
        long then = mProjectedFinishDate.getTime();
        int days = (int) ((then - now) / (1000 * 60 * 60 * 24))+1;
        mPace = (111111 - mMainCounter) / days ;
    }

    private void refresh() {
        TextView counterView = (TextView) findViewById(R.id.main_counter);
        TextView dateView = (TextView) findViewById(R.id.date_of_last_practice);
        Button plus = (Button) findViewById(R.id.add_repetitions_button);
        Button pace = (Button) findViewById(R.id.pace_button);
        Button date = (Button) findViewById(R.id.date_button);
        counterView.setText(String.format("%,d", (Integer)mMainCounter));
        plus.setText("+" + String.valueOf(mPace));
        dateView.setText(String.format("%te %<tB %<tY", mDateOfLastPractice));
        pace.setText(String.valueOf(mPace));
//        computeProjectedFinishDate();
        if (mPace != 0 && mMainCounter != 111111) {
            date.setText(mDateFormat.format(mProjectedFinishDate));
        } else if (mMainCounter == 111111 ) {
            date.setText(R.string.finished);
        } else if (mPace == 0 ) {
            date.setText(R.string.never);
        }
//        paceToDateView.setText(getResources().getString(R.string.prostrations_pace_to_date, mPace, mProjectedFinishDate));
    }

    private class PacePickerFragment extends DialogFragment implements AlertDialog.OnClickListener{

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.pick_pace)
                    .setItems(R.array.pace_values, this)
                    .create();
            return dialog;
        }
        @Override
        public void onClick(DialogInterface dialogInterface, int which) {
            mPace = Integer.valueOf(getResources().getStringArray(R.array.pace_values)[which]);
            computeProjectedFinishDate();
            refresh();
        }

    }

    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            c.setTime(mProjectedFinishDate);
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            Calendar now = Calendar.getInstance();
            c.set(year, month, day);
            if (c.before(now)) {
                c = now;
            }
            mProjectedFinishDate = c.getTime();
            computePace();
            refresh();


        }

    }
}
