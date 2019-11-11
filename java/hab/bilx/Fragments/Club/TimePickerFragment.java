package hab.bilx.Fragments.Club;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

/**
 * Class creates a time selector clock to allow club to choose a time
 * @author Hanzallah Burney
 */

public class TimePickerFragment extends DialogFragment {
    TimePickerDialog.OnTimeSetListener onTimeSet;
    /*
     *  @author Hanzallah Burney
     */
    public TimePickerFragment() {

    }
    /*
     *  @author Hanzallah Burney
     */
    public void setCallBack(TimePickerDialog.OnTimeSetListener ontime) {
        onTimeSet = ontime;
    }
    @SuppressLint("NewApi")
    private int hour, minute;
    public void setArguments(Bundle args) {
        super.setArguments(args);
        hour = args.getInt("hour");
        minute = args.getInt("minute");

    }
    /*
     *  @author Hanzallah Burney
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new TimePickerDialog(getActivity(), onTimeSet, hour, minute, false);
    }
}