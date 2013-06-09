package pl.eastsidemandala.nyndrovnik;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// TODO: encapsulate mPace and round to full hundreds on set
public class MainActivity extends FragmentActivity implements View.OnClickListener,
        EditCounterDialogFragment.EditCounterDialogListener,
        PacePickerFragment.OnPaceSelectedListener {

    public static final String PROSTRATIONS_COUNTER_KEY = "prostrations_counter";
    public static final String DATE_OF_LAST_PRACTICE_KEY = "date_of_last_practice";
    public static final String PROSTRATIONS_PACE_KEY = "prostrations_pace";

    private static final int DEFAULT_PACE = 100;

    private int mMainCounter;
    private int mPace = 100;
    private Date mDateOfLastPractice;
    private Date mProjectedFinishDate;
    private DateFormat mDateFormat = SimpleDateFormat.getDateInstance();

    public int getmMainCounter() {
        return mMainCounter;
    }

    public void setmMainCounter(int mMainCounter) {
        if  (mMainCounter > 111111) {
            this.mMainCounter = 111111;
        } else if (mMainCounter < 0) {
            this.mMainCounter = 0;
        }  else {
            this.mMainCounter = mMainCounter;
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        setmMainCounter(prefs.getInt(PROSTRATIONS_COUNTER_KEY, 0));
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
                .putInt(PROSTRATIONS_COUNTER_KEY, getmMainCounter())
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
                dialog.setInitialValue(getmMainCounter());
                dialog.show(getSupportFragmentManager(), "counter_edit");
        }
        return true;
    }


    @Override
    public void onClick(View view) {
        setmMainCounter(getmMainCounter() + mPace);
        mDateOfLastPractice = new Date();
        computeProjectedFinishDate();
        refresh();
    }

    public void onPaceClick(View view) {
        DialogFragment frag = new PacePickerFragment();
        frag.show(getSupportFragmentManager(), "pace_dialog");
    }

    public void onPaceSelected (int value) {
        mPace = value;
        computeProjectedFinishDate();
        refresh();
    }

    public void onDateClick(View view) {
        DialogFragment frag = new DatePickerFragment();
        frag.show(getSupportFragmentManager(), "date_dialog");
    }

    @Override
    public void onEditCounterDialogPositiveClick(int value) {
        setmMainCounter(value);
        computeProjectedFinishDate();
        refresh();
    }



    protected void computeProjectedFinishDate() {
//      remaining repetitions divided by currently selected pace, plus one day for the remainder
        int remainingDays = 0;
        if (mPace > 0) {
             remainingDays = (111111 - getmMainCounter()) / mPace + 1;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, remainingDays);
        mProjectedFinishDate = c.getTime();
    }

    protected void computePace() {
        long now = new Date().getTime();
        long then = mProjectedFinishDate.getTime();
        int days = (int) ((then - now) / (1000 * 60 * 60 * 24))+1;
        mPace = (111111 - getmMainCounter()) / days ;
    }

    protected void refresh() {
        TextView counterView = (TextView) findViewById(R.id.main_counter);
        TextView dateView = (TextView) findViewById(R.id.date_of_last_practice);
        Button plus = (Button) findViewById(R.id.add_repetitions_button);
        Button pace = (Button) findViewById(R.id.pace_button);
        Button date = (Button) findViewById(R.id.date_button);
        counterView.setText(String.format("%,d", (Integer) getmMainCounter()));
        plus.setText("+" + String.valueOf(mPace));
        dateView.setText(String.format("%te %<tB %<tY", mDateOfLastPractice));
        pace.setText(String.valueOf(mPace));
//        computeProjectedFinishDate();
        if (mPace != 0 && getmMainCounter() != 111111) {
            date.setText(mDateFormat.format(mProjectedFinishDate));
        } else if (getmMainCounter() == 111111 ) {
            date.setText(R.string.finished);
        } else if (mPace == 0 ) {
            date.setText(R.string.never);
        }
//        paceToDateView.setText(getResources().getString(R.string.prostrations_pace_to_date, mPace, mProjectedFinishDate));
    }

    public Date getProjectedFinishDate() {
        return mProjectedFinishDate;
    }

    public void setProjectedFinishDate(Date projectedFinishDate) {
        Calendar now = Calendar.getInstance();
        Calendar c = Calendar.getInstance();
        c.setTime(projectedFinishDate);
        if (c.before(now)) {
            this.mProjectedFinishDate = now.getTime();
        } else {
            this.mProjectedFinishDate = c.getTime();
        }
    }


}
