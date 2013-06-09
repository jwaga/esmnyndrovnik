package pl.eastsidemandala.nyndrovnik;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
* Created by konrad on 09.06.2013.
*/
public class PacePickerFragment extends DialogFragment implements AlertDialog.OnClickListener{

    OnPaceSelectedListener mListener;

    public interface OnPaceSelectedListener {
        public void onPaceSelected(int pace);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnPaceSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement OnPaceSelectedListener");
        }
    }

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
        String value = getResources().getStringArray(R.array.pace_values)[which];
        int count = Integer.valueOf(value);
        mListener.onPaceSelected(count);
    }

}
