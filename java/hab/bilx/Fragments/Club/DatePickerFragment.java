package hab.bilx.Fragments.Club;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Class creates a calendar to allow club to choose a date
 * @author Hanzallah Burney
 */

public class DatePickerFragment extends DialogFragment {
    DatePickerDialog.OnDateSetListener ondateSet;

    public DatePickerFragment() {
    }

    /*
     **  @author Hanzallah Burney
     */

    public void setCallBack(DatePickerDialog.OnDateSetListener ondate) {
        ondateSet = ondate;
    }

    private int year, month, day;
    /*
     **  @author Hanzallah Burney
     */

    @SuppressLint("NewApi")
    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        year = args.getInt("year");
        month = args.getInt("month");
        day = args.getInt("day");
    }
    /*
     **  @author Hanzallah Burney
     */

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new DatePickerDialog(getActivity(), ondateSet, year, month, day);
    }
}
/*
 **  @author Hanzallah Burney
 */
