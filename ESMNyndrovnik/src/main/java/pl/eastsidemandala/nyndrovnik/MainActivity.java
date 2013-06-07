package pl.eastsidemandala.nyndrovnik;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import java.text.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

public class MainActivity extends FragmentActivity implements View.OnClickListener,
        EditCounterDialogFragment.EditCounterDialogListener {

    public static final String PROSTRATIONS_COUNTER = "prostrations_counter";
    public static final String DATE_OF_LAST_PRACTICE = "date_of_last_practice";

    private int mMainCounter;
    private int mPace = 100;
    private Date mDateOfLastPractice;
    private Date mProjectedFinishDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        mMainCounter = prefs.getInt(PROSTRATIONS_COUNTER, 0);
        long date = prefs.getLong(DATE_OF_LAST_PRACTICE, 0);
        mDateOfLastPractice = new Date();
        if (date > 0) { mDateOfLastPractice.setTime(date); }
        setUpSpinner();
        refresh();
    }

    private void setUpSpinner() {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.pace_values, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new PaceSelectedListener());
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPreferences(MODE_PRIVATE).edit()
                .putInt("prostrations_counter", mMainCounter)
                .putLong("date_of_last_practice", mDateOfLastPractice.getTime())
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
                EditCounterDialogFragment dialog = new EditCounterDialogFragment(mMainCounter);
                dialog.show(getSupportFragmentManager(), "counter_edit");
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        mMainCounter += mPace;
        mDateOfLastPractice = new Date();
        refresh();
    }

    @Override
    public void onEditCounterDialogPositiveClick(int value) {
        mMainCounter = value;
        refresh();
    }

    private void computeProjectedFinishDate() {
//      remaining repetitions divided by currently selected pace, plus one day for the remainder
        int remainingDays = (111111 - mMainCounter) / mPace + 1;
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, remainingDays);
        mProjectedFinishDate = c.getTime();
    }

    private void refresh() {
        TextView counterView = (TextView) findViewById(R.id.main_counter);
        TextView dateView = (TextView) findViewById(R.id.date_of_last_practice);
        TextView paceToDateView = (TextView) findViewById(R.id.pace_to_date);
        counterView.setText(String.format("%,d", (Integer)mMainCounter));
        dateView.setText(String.format("%te %<tB %<tY", mDateOfLastPractice));
        computeProjectedFinishDate();
        paceToDateView.setText(getResources().getString(R.string.prostrations_pace_to_date, mPace, mProjectedFinishDate));
    }

    private class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            Calendar c = Calendar.getInstance();
            int day = c.get(Calendar.DAY_OF_MONTH);
            int month = c.get(Calendar.MONTH);
            int year = c.get(Calendar.YEAR);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {


        }
    }

    private class PaceSelectedListener implements AdapterView.OnItemSelectedListener {
        private AdapterView<?> adapterView;
        private View view;
        private int i;
        private long l;

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            mPace = Integer.valueOf((String) adapterView.getItemAtPosition(i));
            refresh();
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {

        }
    }
}
