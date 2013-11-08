package pl.eastsidemandala.nyndrovnik;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class PracticeData {

    public static final String COUNTER_KEY = "_counter";
    public static final String DATE_OF_LAST_PRACTICE_KEY = "_date_of_last_practice";
    public static final String PACE_KEY = "_pace";
    public static final String ACTIVE_PRACTICE_KEY = "active_practice";
    static final int DEFAULT_PACE = 100;
    public static final String PREVIOUS_COUNT_KEY = "_previous_count";


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    Activity activity;

    int mMainCounter;
    int mPace = 100;

    public int getmPace() {
        return mPace;
    }

    public void setPace(int mPace) {
        this.mPace = mPace;
    }

    public int getPreviousCount() {
        return mPreviousCount;
    }

    public void setPreviousCount(int mPreviousCount) {
        this.mPreviousCount = mPreviousCount;
    }

    int mPreviousCount;

    Date mDateOfLastPractice;

    public Date getmDateOfLastPractice() {
        return mDateOfLastPractice;
    }

    public void setmDateOfLastPractice(Date mDateOfLastPractice) {
        this.mDateOfLastPractice = mDateOfLastPractice;
    }

    Date mProjectedFinishDate;

    public Date getmProjectedFinishDate() {
        return mProjectedFinishDate;
    }

    public void setmProjectedFinishDate(Date mProjectedFinishDate) {
        this.mProjectedFinishDate = mProjectedFinishDate;
    }

    DateFormat mDateFormat = SimpleDateFormat.getDateInstance();
    NyndroFragment.Practice mPractice;

    public NyndroFragment.Practice getPractice() {
        return mPractice;
    }

    public void setmPractice(NyndroFragment.Practice mPractice) {
        this.mPractice = mPractice;
    }

    boolean mUpdated = false;

    public boolean isUpdated() {
        return mUpdated;
    }

    public PracticeData() {
    }// accessors

    public int getMainCounter() {
        return mMainCounter;
    }

    public void setMainCounter(int mMainCounter) {
        if (mMainCounter > 111111) {
            this.mMainCounter = 111111;
        } else if (mMainCounter < 0) {
            this.mMainCounter = 0;
        } else {
            this.mMainCounter = mMainCounter;
        }
        MainActivity activity = (MainActivity) this.activity;
        switch (this.mPractice) {
            case PROSTRATIONS:
                activity.dmUnlocked = (this.mMainCounter >= 30000);
                break;
            case DIAMOND_MIND:
                activity.mandalaUnlocked = (this.mMainCounter == 111111);
                break;
            case MANDALA_OFFERING:
                activity.guruYogaUnlocked = (this.mMainCounter == 111111);
                break;
        }
        LocalBroadcastManager.getInstance(this.activity).sendBroadcast(
                new Intent().setAction("pl.eastsidemandala.nyndrovnik.PRACTICE_UNLOCKED")
        );

    }

    public void updateMainCounter(int count) {
        setPreviousCount(getMainCounter());
        setMainCounter(count);
        this.mUpdated = true;
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

    void saveData(NyndroFragment nyndroFragment) {
        SharedPreferences.Editor prefs = nyndroFragment.getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        prefs.putInt(getPractice().toString() + COUNTER_KEY, getMainCounter())
        .putLong(getPractice().toString() + DATE_OF_LAST_PRACTICE_KEY, getmDateOfLastPractice().getTime())
        .putInt(getPractice().toString() + PACE_KEY, getmPace())
        .putInt(getPractice().toString() + PREVIOUS_COUNT_KEY, getPreviousCount());
        if (isUpdated()) {
            prefs.putString(ACTIVE_PRACTICE_KEY, getPractice().toString());
        }
        prefs.commit();
    }

    public void saveDataToInstanceState(Bundle outState) {
        outState.putInt(getPractice().toString() + COUNTER_KEY, getMainCounter());
        outState.putInt(getPractice().toString() + PACE_KEY, getmPace());
        outState.putLong(getPractice().toString() + DATE_OF_LAST_PRACTICE_KEY, getmDateOfLastPractice().getTime());
        outState.putInt(getPractice().toString() + PREVIOUS_COUNT_KEY, getPreviousCount());
        if (isUpdated()) {
            outState.putString(ACTIVE_PRACTICE_KEY, getPractice().toString());
        }
    }

    void loadData(NyndroFragment nyndroFragment) {
        SharedPreferences prefs = nyndroFragment.getActivity().getPreferences(Context.MODE_PRIVATE);
        setMainCounter(prefs.getInt(getPractice() + COUNTER_KEY, 0));
        setPreviousCount(prefs.getInt(getPractice() + PREVIOUS_COUNT_KEY, 0));
        setPace(prefs.getInt(getPractice() + PACE_KEY, DEFAULT_PACE));
        long date = prefs.getLong(getPractice() + DATE_OF_LAST_PRACTICE_KEY, 0);
        setmDateOfLastPractice(new Date());
        if (date > 0) {
            getmDateOfLastPractice().setTime(date); }
    }
}