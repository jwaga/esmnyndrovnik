package pl.eastsidemandala.nyndrovnik;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import java.util.Calendar;
import java.util.Date;
import java.util.Stack;

public class PracticeData {

    public static final String COUNTER_KEY = "_counter";
    public static final String DATE_OF_LAST_PRACTICE_KEY = "_date_of_last_practice";
    public static final String PACE_KEY = "_pace";
    public static final String ACTIVE_PRACTICE_KEY = "active_practice";
    static final int DEFAULT_PACE = 100;
    public static final String PREVIOUS_COUNT_KEY = "_previous_count";
    private int mRepetitionsMax;

    public int getmRepetitionsMax() {
        return mRepetitionsMax;
    }

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

    class PracticeSession {
        int counter;
        Date date;

        PracticeSession(int counter, Date date) {
            this.counter = counter;
            this.date = date;
        }
    }

    Stack<PracticeSession> mUndo = new Stack<>();

    int mPreviousCount;

    Date mDateOfLastPractice;

    public Date getmDateOfLastPractice() {
        return mDateOfLastPractice;
    }

    public void setDateOfLastPractice(Date mDateOfLastPractice) {
        this.mDateOfLastPractice = mDateOfLastPractice;
    }

    Date mProjectedFinishDate;

    public Date getmProjectedFinishDate() {
        return mProjectedFinishDate;
    }

    public void setmProjectedFinishDate(Date mProjectedFinishDate) {
        this.mProjectedFinishDate = mProjectedFinishDate;
    }

    Practice mPractice;

    public Practice getPractice() {
        return mPractice;
    }

    public void setmPractice(Practice mPractice) {
        this.mPractice = mPractice;
        this.mRepetitionsMax = mPractice.getRepetitionsMax();
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
        if (mMainCounter > this.getmRepetitionsMax()) {
            this.mMainCounter = this.getmRepetitionsMax();
        } else if (mMainCounter < 0) {
            this.mMainCounter = 0;
        } else {
            this.mMainCounter = mMainCounter;
        }
    }

    public void updateMainCounter(int count) {
        mUndo.push(new PracticeSession(getMainCounter(), getmDateOfLastPractice()));
        if (Build.VERSION.SDK_INT >= 11) {
            getActivity().invalidateOptionsMenu();
        }
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
        setDateOfLastPractice(new Date());
        if (date > 0) {
            getmDateOfLastPractice().setTime(date); }
    }
}
