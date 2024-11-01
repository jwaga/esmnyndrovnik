package pl.eastsidemandala.nyndrovnik;

import android.app.AlertDialog;
import android.app.Dialog;
import androidx.fragment.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

/**
 * Created by konrad on 06.06.2013.
 */
public class SingleEditDialogFragment extends DialogFragment {

    private int mTitleid;

    public interface SingleEditDialogListener {
        public void onSingleEditDialogPositiveClick(int value);
    }
    int mValue;
    SingleEditDialogListener mListener;

    public void setInitialValue (int value) {
        mValue = value;
    }

    public void setListener (SingleEditDialogListener listener) {
        mListener = listener;
    }

    public void setTitle (int titleId) {
        mTitleid = titleId;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt("count", mValue);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (mListener == null && context instanceof SingleEditDialogListener) {
            mListener = (SingleEditDialogListener) context;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            mValue = savedInstanceState.getInt("count", 0);
        }
        final EditText edit = new EditText(getActivity());
        String textValue = "";
        if (mValue != 0) {
            textValue = String.valueOf(mValue);
        }
        edit.setText(textValue);
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        return new AlertDialog.Builder(getActivity())
            .setTitle(mTitleid)
            .setView(edit)
            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    String text = edit.getText().toString();
                    if (!text.isEmpty()) {
                        mListener.onSingleEditDialogPositiveClick(Integer.parseInt(text));
                    }
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // cancelled
                    }
                }).create();
    }


}
