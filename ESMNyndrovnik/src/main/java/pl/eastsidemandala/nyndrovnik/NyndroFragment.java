package pl.eastsidemandala.nyndrovnik;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;


// TODO: encapsulate mPace and round to full hundreds on set
public class NyndroFragment extends Fragment implements View.OnClickListener {

    private final PracticeData mData = new PracticeData();


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


    // Overriden superclass methods



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mData.setmPractice(Practice.valueOf(getArguments().getString("practice")));
        Log.d(getTag(), "Fragment created:" + mData.getPractice().toString());
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.nyndro_fragment, container, false);
        ImageView image = (ImageView)layout.findViewById(R.id.imageView);
        image.setImageResource(mData.getPractice().getImageRes());
        return layout;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mData.setActivity(getActivity());
        Log.d(getTag(), "fragment.onActivityCreated:" + mData.getPractice());
        TextView name =  (TextView) getView().findViewById(R.id.practice_name);
        name.setText(mData.getPractice().getNameRes());
        getView().findViewById(R.id.add_repetitions_button).setOnClickListener(this);
        getView().findViewById(R.id.pace_button).setOnClickListener(this);
        getView().findViewById(R.id.date_button).setOnClickListener(this);
        mData.loadData(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("pl.eastsidemandala.nyndrovnik.PRACTICE_UNLOCKED");
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(new PracticeUnlockReceiver(),
                filter);
        if (getView() != null) {
            updatePracticeLock();
        }
        computeProjectedFinishDate();
        refresh();
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mData.mUndo.empty()) {
            menu.findItem(R.id.action_undo).setEnabled(false).setIcon(R.drawable.undo_disabled);
        } else {
            menu.findItem(R.id.action_undo).setEnabled(true).setIcon(R.drawable.undo);
        }
    }

    public void updatePracticeLock() {
        TextView lock = (TextView) getView().findViewById(R.id.lock_text);
        MainActivity activity = (MainActivity) getActivity();
        switch (mData.getPractice()) {
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
        mData.saveData(this);
        mData.mUndo.clear();
        Activity a = getActivity();
        if (a != null && Build.VERSION.SDK_INT >= 11) {
            a.invalidateOptionsMenu();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mData.saveDataToInstanceState(outState);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit_counter:
                SingleEditDialogFragment counterDialog = new SingleEditDialogFragment();
                counterDialog.setInitialValue(mData.getMainCounter());
                counterDialog.setTitle(R.string.action_edit_counter);
                counterDialog.setListener(new EditCounterDialogListener());
                counterDialog.show(getActivity().getSupportFragmentManager(), "counter_edit");
                break;
            case R.id.action_edit_pace:
                SingleEditDialogFragment paceDialog = new SingleEditDialogFragment();
                paceDialog.setInitialValue(mData.getmPace());
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
            case R.id.action_undo:
                onUndoClick();
        }
        return true;
    }

//  Event handlers
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_repetitions_button:
                onAddClick();
                break;
            case R.id.pace_button:
                onPaceClick(view);
                break;
            case R.id.date_button:
                onDateClick(view);
                break;
        }
    }

    private void onAddClick() {
        mData.updateMainCounter(mData.getMainCounter() + mData.getmPace());
        mData.setDateOfLastPractice(new Date());
        computeProjectedFinishDate();
        refresh();
        TextView feedback = (TextView) getView().findViewById(R.id.feedback);
        feedback.setText(String.format("+%s", String.valueOf(mData.getmPace())));
//        feedback.setVisibility(View.VISIBLE);
        Animation feedbackAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_feedback);
        feedback.startAnimation(feedbackAnimation);
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
        args.putLong("date", mData.getProjectedFinishDate().getTime());
        frag.setArguments(args);
        frag.show(getActivity().getSupportFragmentManager(), "date_dialog");
    }

    public void onUndoClick() {
        if (! mData.mUndo.empty()) {
                PracticeData.PracticeSession previous = (PracticeData.PracticeSession) mData.mUndo.pop();
                mData.setMainCounter(previous.counter);
                mData.setDateOfLastPractice(previous.date);
            if (Build.VERSION.SDK_INT >= 11) {
                getActivity().invalidateOptionsMenu();
            }
            refresh();
        }
    }

    private class SetPaceDialogListener implements PacePickerFragment.OnPaceSelectedListener {
        public void onPaceSelected (int value) {
            mData.setPace(value);
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
            mData.setProjectedFinishDate(c.getTime());
            computePace();
            if (NyndroFragment.this.getView() != null) {
                refresh();
            }
        }
    }

    private class EditCounterDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
        @Override
        public void onSingleEditDialogPositiveClick(int value) {
            mData.updateMainCounter(value);
            computeProjectedFinishDate();
            refresh();
        }
    }

    private class EditPaceDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
        @Override
        public void onSingleEditDialogPositiveClick(int value) {
            mData.setPace(value);
            computeProjectedFinishDate();
            refresh();
        }
    }

    private class AddRepetitionsDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
        @Override
        public void onSingleEditDialogPositiveClick(int value) {
            mData.updateMainCounter(mData.getMainCounter() + value);
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
        if (mData.getmPace() > 0) {
             remainingDays = (111111 - mData.getMainCounter()) / mData.getmPace() + 1;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, remainingDays);
        mData.setmProjectedFinishDate(c.getTime());
    }

    protected void computePace() {
        long now = new Date().getTime();
        long then = mData.getmProjectedFinishDate().getTime();
        int days = (int) ((then - now) / (1000 * 60 * 60 * 24))+1;
        int pace = (111111 - mData.getMainCounter()) / days ;
//        round up to the nearest 100: add 100 - remainder if remainder > 0
        mData.setPace(pace + (pace % 100 > 0 ? 100 - (pace % 100) : 0));
    }

    protected void refresh() {
        TextView counterView = (TextView) getView().findViewById(R.id.main_counter);
        TextView dateView = (TextView) getView().findViewById(R.id.date_of_last_practice);
        Button plus = (Button) getView().findViewById(R.id.add_repetitions_button);
        Button pace = (Button) getView().findViewById(R.id.pace_button);
        Button date = (Button) getView().findViewById(R.id.date_button);
        counterView.setText(String.format("%,d", (Integer) mData.getMainCounter()));
        NyndroProgressView progressView = (NyndroProgressView) getView().findViewById(R.id.progress);
        progressView.setCount(mData.getMainCounter());
        pace.setText("+" + String.valueOf(mData.getmPace()));
        dateView.setText(String.format("ostatnio %te %<tB %<tY o %<tH:%<tM", mData.getmDateOfLastPractice()));
//        pace.setText(String.valueOf(mPace));
//        computeProjectedFinishDate();
        if (mData.getmPace() != 0 && mData.getMainCounter() != 111111) {
            date.setText(String.format("do %te %<tB %<tY", mData.getmProjectedFinishDate()));
        } else if (mData.getMainCounter() == 111111 ) {
            date.setText(R.string.finished);
        } else if (mData.getmPace() == 0 ) {
            date.setText(R.string.never);
        }
//        paceToDateView.setText(getResources().getString(R.string.prostrations_pace_to_date, mPace, mProjectedFinishDate));
    }


}
