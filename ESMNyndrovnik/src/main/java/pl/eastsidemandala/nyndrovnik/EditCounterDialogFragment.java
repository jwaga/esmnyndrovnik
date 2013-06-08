package pl.eastsidemandala.nyndrovnik;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by konrad on 06.06.2013.
 */
public class EditCounterDialogFragment extends DialogFragment {

    public interface EditCounterDialogListener {
        public void onEditCounterDialogPositiveClick(int value);
    }
    int mCount = 0;
    EditCounterDialogListener mListener;

    public EditCounterDialogFragment (int count) {
        mCount = count;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mListener = (EditCounterDialogListener) activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final EditText edit = new EditText(getActivity());
        edit.setText(String.valueOf(mCount));
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        return new AlertDialog.Builder(getActivity())
            .setTitle(R.string.action_edit_counter)
            .setView(edit)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    mListener.onEditCounterDialogPositiveClick(Integer.valueOf(edit.getText().toString()));
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancelled
                    }
                }).create();
    }
}
