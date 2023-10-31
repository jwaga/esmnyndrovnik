package pl.eastsidemandala.nyndrovnik;

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

    public NyndroFragment getmParentFragment() {
        return mParentFragment;
    }

    public void setmParentFragment(NyndroFragment mParentFragment) {
        this.mParentFragment = mParentFragment;
    }

    NyndroFragment mParentFragment;

    public interface OnPaceSelectedListener {
        public void onPaceSelected(int pace);

    }

    public void setListener (OnPaceSelectedListener listener) {
        mListener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
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
        String[] values = getResources().getStringArray(R.array.pace_values);
        String value = values[which];
        if (which == values.length-1) {
            SingleEditDialogFragment paceDialog = new SingleEditDialogFragment();
            paceDialog.setInitialValue(mParentFragment.mData.getmPace());
            paceDialog.setListener(mParentFragment.newEditPaceDialogListener());
            paceDialog.setTitle(R.string.action_edit_pace);
            paceDialog.show(getActivity().getSupportFragmentManager(), "pace_edit");
        } else {
            int count = Integer.valueOf(value);
            mListener.onPaceSelected(count);
        }
    }

}
