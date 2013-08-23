package pl.eastsidemandala.nyndrovnik;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


// TODO: encapsulate mPace and round to full hundreds on set
public class NyndroFragment extends Fragment implements View.OnClickListener {

    public static final String COUNTER_KEY = "_counter";
    public static final String DATE_OF_LAST_PRACTICE_KEY = "_date_of_last_practice";
    public static final String PACE_KEY = "_pace";


    public static enum Practice {
        PROSTRATIONS(R.string.prostrations, R.drawable.rtree),
        DIAMOND_MIND(R.string.diamond_mind, R.drawable.dm),
        MANDALA_OFFERING(R.string.mandala_offering, R.drawable.rtree),
        GURU_YOGA(R.string.guru_yoga, R.drawable.dm);
        int name_res;
        int image_res;
        Practice(int name_res, int image_res) {
            this.name_res = name_res;
            this.image_res = image_res;
        }
        public int getNameRes() {
            return this.name_res;
        }
        public int getImageRes() {
            return this.image_res;
        }
    }

    private static final int DEFAULT_PACE = 100;

    private int mMainCounter;
    private int mPace = 100;
    private Date mDateOfLastPractice;
    private Date mProjectedFinishDate;
    private DateFormat mDateFormat = SimpleDateFormat.getDateInstance();
    private Practice mPractice;

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
        MainActivity activity = (MainActivity) getActivity();
        switch (this.mPractice) {
            case PROSTRATIONS:
                if (this.mMainCounter >= 30000) {
                    activity.dmUnlocked = true;
                } else {
                    activity.dmUnlocked = false;
                }
                break;
            case DIAMOND_MIND:
                if (this.mMainCounter == 111111) {
                    activity.mandalaUnlocked = true;
                } else {
                    activity.mandalaUnlocked = false;
                }
                break;
            case MANDALA_OFFERING:
                if (this.mMainCounter == 111111) {
                    activity.guruYogaUnlocked = true;
                } else {
                    activity.guruYogaUnlocked = false;
                }
        }
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(
                new Intent().setAction("pl.eastsidemandala.nyndrovnik.PRACTICE_UNLOCKED")
                        );


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

    // Overriden superclass methods


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPractice = Practice.valueOf(getArguments().getString("practice"));
        Log.d(getTag(), "Fragment created:" + mPractice.toString());
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.nyndro_fragment, container, false);
        ImageView image = (ImageView)layout.findViewById(R.id.imageView);
        image.setImageResource(mPractice.getImageRes());
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(getTag(), "fragment.onActivityCreated:" + mPractice);
        TextView name =  (TextView) getView().findViewById(R.id.practice_name);
        name.setText(mPractice.getNameRes());
        getView().findViewById(R.id.add_repetitions_button).setOnClickListener(this);
        getView().findViewById(R.id.pace_button).setOnClickListener(this);
        getView().findViewById(R.id.date_button).setOnClickListener(this);
        SharedPreferences prefs = getActivity().getPreferences(Context.MODE_PRIVATE);
        setmMainCounter(prefs.getInt(mPractice + COUNTER_KEY, 0));
        mPace = prefs.getInt(mPractice + PACE_KEY, DEFAULT_PACE);
        long date = prefs.getLong(mPractice + DATE_OF_LAST_PRACTICE_KEY, 0);
        mDateOfLastPractice = new Date();
        if (date > 0) { mDateOfLastPractice.setTime(date); }
        else { }
//        setUpSpinner();

        IntentFilter filter = new IntentFilter();
        filter.addAction("pl.eastsidemandala.nyndrovnik.PRACTICE_UNLOCKED");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new PracticeUnlockReceiver(),
                filter);
        updatePracticeLock();
        computeProjectedFinishDate();
        refresh();
    }

    public void updatePracticeLock() {
        TextView lock = (TextView) getView().findViewById(R.id.lock_text);
        MainActivity activity = (MainActivity) getActivity();
        switch (mPractice) {
            case PROSTRATIONS:
                lock.setVisibility(View.GONE);
                break;
            case DIAMOND_MIND:
                if  (! activity.dmUnlocked) {
                    lock.setVisibility(View.VISIBLE);
                } else {
                    lock.setVisibility(View.GONE);
                }
                break;
            case MANDALA_OFFERING:
                if  (! activity.mandalaUnlocked) {
                    lock.setVisibility(View.VISIBLE);
                    lock.setText(R.string.mandala_lock);
                } else {
                    lock.setVisibility(View.GONE);
                }
                break;
            case GURU_YOGA:
                if  (! activity.guruYogaUnlocked) {
                    lock.setVisibility(View.VISIBLE);
                    lock.setText(R.string.guru_yoga_lock);
                } else {
                    lock.setVisibility(View.GONE);
                }
                break;


            }
    }

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onStop() {
        super.onStop();
       getActivity().getPreferences(Context.MODE_PRIVATE).edit()
                .putInt(mPractice.toString() + COUNTER_KEY, getmMainCounter())
                .putLong(mPractice.toString() + DATE_OF_LAST_PRACTICE_KEY, mDateOfLastPractice.getTime())
                .putInt(mPractice.toString() + PACE_KEY, mPace)
                .commit();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(mPractice.toString() + COUNTER_KEY, mMainCounter);
        outState.putInt(mPractice.toString() + PACE_KEY, mPace);
        outState.putLong(mPractice.toString() + DATE_OF_LAST_PRACTICE_KEY, mDateOfLastPractice.getTime());
    }





    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_counter:
                SingleEditDialogFragment counterDialog = new SingleEditDialogFragment();
                counterDialog.setInitialValue(getmMainCounter());
                counterDialog.setTitle(R.string.action_edit_counter);
                counterDialog.setListener(new EditCounterDialogListener());
                counterDialog.show(getActivity().getSupportFragmentManager(), "counter_edit");
                break;
            case R.id.action_edit_pace:
                SingleEditDialogFragment paceDialog = new SingleEditDialogFragment();
                paceDialog.setInitialValue(mPace);
                paceDialog.setListener(new EditPaceDialogListener());
                paceDialog.setTitle(R.string.action_edit_pace);
                paceDialog.show(getActivity().getSupportFragmentManager(), "pace_edit");
                break;
            case R.id.action_add_repetitions:
                SingleEditDialogFragment addDialog = new SingleEditDialogFragment();
                addDialog.setTitle(R.string.action_add_repetitions);
                addDialog.setListener(new AddRepetitionsDialogListener());
                addDialog.show(getActivity().getSupportFragmentManager(), "add_repetitions");
                break;
        }
        return true;
    }

//  Event handlers
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_repetitions_button:
                setmMainCounter(getmMainCounter() + mPace);
                mDateOfLastPractice = new Date();
                computeProjectedFinishDate();
                refresh();
                break;
            case R.id.pace_button:
                onPaceClick(view);
                break;
            case R.id.date_button:
                onDateClick(view);
                break;
        }
    }

    public void onPaceClick(View view) {
        PacePickerFragment frag = new PacePickerFragment();
        frag.setListener(new SetPaceDialogListener());
        frag.show(getActivity().getSupportFragmentManager(), "pace_dialog");
    }

    public void onDateClick(View view) {
        DatePickerFragment frag = new DatePickerFragment();
        frag.setListener(new FinishDateListener());
        Bundle args = new Bundle();
        args.putLong("date", getProjectedFinishDate().getTime());
        frag.setArguments(args);
        frag.show(getActivity().getSupportFragmentManager(), "date_dialog");
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
            if (NyndroFragment.this.getView() != null) {
                refresh();
            }
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

    private class PracticeUnlockReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (getView() != null) {
                updatePracticeLock();
            }
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
        int pace = (111111 - getmMainCounter()) / days ;
//        round up to the nearest 100: add 100 - remainder if remainder > 0
        mPace = pace + (pace % 100 > 0 ? 100 - (pace % 100) : 0);
    }

    protected void refresh() {
        TextView counterView = (TextView) getView().findViewById(R.id.main_counter);
        TextView dateView = (TextView) getView().findViewById(R.id.date_of_last_practice);
        Button plus = (Button) getView().findViewById(R.id.add_repetitions_button);
        Button pace = (Button) getView().findViewById(R.id.pace_button);
        Button date = (Button) getView().findViewById(R.id.date_button);
        counterView.setText(String.format("%,d", (Integer) getmMainCounter()));
        NyndroProgressView progressView = (NyndroProgressView) getView().findViewById(R.id.progress);
        progressView.setCount(mMainCounter);
        pace.setText("+" + String.valueOf(mPace));
        dateView.setText(String.format("ostatnio %te %<tB %<tY", mDateOfLastPractice));
//        pace.setText(String.valueOf(mPace));
//        computeProjectedFinishDate();
        if (mPace != 0 && getmMainCounter() != 111111) {
            date.setText(String.format("do %te %<tB %<tY", mProjectedFinishDate));
        } else if (getmMainCounter() == 111111 ) {
            date.setText(R.string.finished);
        } else if (mPace == 0 ) {
            date.setText(R.string.never);
        }
//        paceToDateView.setText(getResources().getString(R.string.prostrations_pace_to_date, mPace, mProjectedFinishDate));
    }


}
