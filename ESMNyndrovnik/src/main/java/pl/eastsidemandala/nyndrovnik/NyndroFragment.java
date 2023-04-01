package pl.eastsidemandala.nyndrovnik;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

// TODO: encapsulate mPace and round to full hundreds on set
public class NyndroFragment extends Fragment implements View.OnClickListener {

    final PracticeData mData = new PracticeData();

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
        name.setText(mData.getPractice().getTextRes());
        getView().findViewById(R.id.add_repetitions_button).setOnClickListener(this);
        getView().findViewById(R.id.pace_button).setOnClickListener(this);
        getView().findViewById(R.id.date_button).setOnClickListener(this);
        mData.loadData(this);
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
                break;
            case R.id.action_about:
                actionAbout();
                break;
        }
        return true;
    }

    private void actionAbout() {
        Intent intent = new Intent(getActivity(), AboutActivity.class);
        startActivity(intent);
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
        Animation feedbackAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.add_feedback);
        feedback.startAnimation(feedbackAnimation);
    }

    public void onPaceClick(View view) {
        PacePickerFragment frag = new PacePickerFragment();
        frag.setListener(new SetPaceDialogListener());
        frag.setmParentFragment(this);
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
                PracticeData.PracticeSession previous = mData.mUndo.pop();
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

     public class EditPaceDialogListener implements SingleEditDialogFragment.SingleEditDialogListener {
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

    //  Internal methods
    protected void computeProjectedFinishDate() {
//      remaining repetitions divided by currently selected pace, plus one day for the remainder
        int remainingDays = 0;
        if (mData.getmPace() > 0) {
             remainingDays = (mData.getPractice().getRepetitionsMax() - mData.getMainCounter()) / mData.getmPace() + 1;
        }
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_YEAR, remainingDays);
        mData.setmProjectedFinishDate(c.getTime());
    }

    protected void computePace() {
        long now = new Date().getTime();
        long then = mData.getmProjectedFinishDate().getTime();
        int days = (int) ((then - now) / (1000 * 60 * 60 * 24))+1;
        int pace = (mData.getmRepetitionsMax() - mData.getMainCounter()) / days ;
//        round up to the nearest 100: add 100 - remainder if remainder > 0
        mData.setPace(pace + (pace % 100 > 0 ? 100 - (pace % 100) : 0));
    }

    protected void refresh() {
        TextView counterView = (TextView) getView().findViewById(R.id.main_counter);
        TextView dateView = (TextView) getView().findViewById(R.id.date_of_last_practice);
        Button pace = (Button) getView().findViewById(R.id.pace_button);
        Button date = (Button) getView().findViewById(R.id.date_button);
        counterView.setText(String.format("%,d", (Integer) mData.getMainCounter()));
        NyndroProgressView progressView = (NyndroProgressView) getView().findViewById(R.id.progress);
        progressView.setPractice(mData.getPractice());
        progressView.setCurrentPracticeCount(mData.getMainCounter());
        pace.setText("+" + String.valueOf(mData.getmPace()));
        dateView.setText(String.format(getResources().getString(R.string.last_practice_date), mData.getmDateOfLastPractice()));
//        pace.setText(String.valueOf(mPace));
//        computeProjectedFinishDate();
        if (mData.getmPace() != 0 && mData.getMainCounter() != mData.getmRepetitionsMax()) {
            date.setText(String.format(getResources().getString(R.string.until), mData.getmProjectedFinishDate()));
        } else if (mData.getMainCounter() == mData.getmRepetitionsMax()) {
            date.setText(R.string.finished);
        } else if (mData.getmPace() == 0 ) {
            date.setText(R.string.never);
        }
//        paceToDateView.setText(getResources().getString(R.string.prostrations_pace_to_date, mPace, mProjectedFinishDate));
    }


}
