package pl.eastsidemandala.nyndrovnik;

import android.app.DatePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

// TODO: encapsulate mPace and round to full hundreds on set
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static final String PROSTRATIONS_COUNTER_KEY = "prostrations_counter";
    public static final String DATE_OF_LAST_PRACTICE_KEY = "date_of_last_practice";
    public static final String PROSTRATIONS_PACE_KEY = "prostrations_pace";

    private static final int DEFAULT_PACE = 100;

    private int mMainCounter;
    private int mPace = 100;
    private Date mDateOfLastPractice;
    private Date mProjectedFinishDate;
    private DateFormat mDateFormat = SimpleDateFormat.getDateInstance();

    // accessors
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

    // Overrided superclass methods
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
                SingleEditDialogFragment counterDialog = new SingleEditDialogFragment();
                counterDialog.setInitialValue(getmMainCounter());
                counterDialog.setTitle(R.string.action_edit_counter);
                counterDialog.setListener(new EditCounterDialogListener());
                counterDialog.show(getSupportFragmentManager(), "counter_edit");
                break;
            case R.id.action_edit_pace:
                SingleEditDialogFragment paceDialog = new SingleEditDialogFragment();
                paceDialog.setInitialValue(mPace);
                paceDialog.setListener(new EditPaceDialogListener());
                paceDialog.setTitle(R.string.action_edit_pace);
                paceDialog.show(getSupportFragmentManager(), "pace_edit");
                break;
            case R.id.action_add_repetitions:
                SingleEditDialogFragment addDialog = new SingleEditDialogFragment();
                addDialog.setTitle(R.string.action_add_repetitions);
                addDialog.setListener(new AddRepetitionsDialogListener());
                addDialog.show(getSupportFragmentManager(), "add_repetitions");
                break;
        }
        return true;
    }

//  Event handlers
    @Override
    public void onClick(View view) {
        setmMainCounter(getmMainCounter() + mPace);
        mDateOfLastPractice = new Date();
        computeProjectedFinishDate();
        refresh();
    }

    public void onPaceClick(View view) {
        PacePickerFragment frag = new PacePickerFragment();
        frag.setListener(new SetPaceDialogListener());
        frag.show(getSupportFragmentManager(), "pace_dialog");
    }

    public void onDateClick(View view) {
        DatePickerFragment frag = new DatePickerFragment();
        frag.setListener(new FinishDateListener());
        frag.show(getSupportFragmentManager(), "date_dialog");
    }

    private class SetPaceDialogListener implements PacePickerFragment.OnPaceSelectedListener {
        public void onPaceSelected (int value) {
            mPace = value;
            computeProjectedFinishDate();
            refresh();
        }
    }

//  Listeners
    private class FinishDateListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            setProjectedFinishDate(c.getTime());
            computePace();
            refresh();
        }
    }

    private class EditCounterDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
        @Override
        public void onSingleEditDialogPositiveClick(int value) {
            setmMainCounter(value);
            computeProjectedFinishDate();
            refresh();
        }
    }

    private class EditPaceDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
        @Override
        public void onSingleEditDialogPositiveClick(int value) {
            mPace = value;
            computeProjectedFinishDate();
            refresh();
        }
    }

    private class AddRepetitionsDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
        @Override
        public void onSingleEditDialogPositiveClick(int value) {
            setmMainCounter(getmMainCounter() + value);
            computeProjectedFinishDate();
            refresh();
        }
    }

//  Internal methods
    private void computeProjectedFinishDate() {
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


}
