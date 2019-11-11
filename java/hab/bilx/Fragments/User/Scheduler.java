package hab.bilx.Fragments.User;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alamkanak.myweekview.MonthLoader;
import com.alamkanak.myweekview.WeekView;
import com.alamkanak.myweekview.WeekViewEvent;
import com.alamkanak.myweekview.WeekViewLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import hab.bilx.Accounts.User_Account;
import hab.bilx.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 *  The scheduler for the admin class.
 *  @author Hanzallah Burney
 *  contributors: Khasmamad, Abdul Hamid
 */

public class Scheduler extends android.support.v4.app.Fragment {

    //variables
    public static final ArrayList<WeekViewEvent> events = new ArrayList<>();
    WeekView mWeekView;
    View view;

    /*
     **  @author Hanzallah Burney
     * contributors: Khasmamad, Abdul Hamid
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        view = inflater.inflate(R.layout.smart_scheduler, container, false);

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) view.findViewById(R.id.weekView);

        //MonthChangeListener
        mWeekView.setMonthChangeListener( new MonthLoader.MonthChangeListener(){
            @Override
            public List<? extends WeekViewEvent> onMonthChange(int newYear, int newMonth) {
                //events.add( test.getWve());
                return events;
            }
        });

        mWeekView.setWeekViewLoader(new WeekViewLoader() {
            //this variable is used to count the execution time of onLoad() function
            int i=1;
            @Override
            public double toWeekViewPeriodIndex(Calendar instance) {

                //ignore this line
                return instance.getTime().getDate()+instance.getTime().getDate();
            }

            @Override
            public List<WeekViewEvent> onLoad(int periodIndex) {
                //if onLoad function execution count is 3 then load events and set i=0 for next time view Load
                //else  onLoad function execute 1st and 2nd time load only empty event list
                if(i==3)
                {

                    i=1;
                    //this is custom function to load list of WeeViewkEvents
                    return events;
                }
                else
                {
                    i++;
                    //empty List
                    List events=new ArrayList();
                    return events;
                }
            }
        });

        /*
         **  @author Hanzallah Burney
         * contributors: Khasmamad, Abdul Hamid
         */
        if (mWeekView.isReleased){
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new UserActivities()).commit();
            Log.i("tag", "isreleased");
            mWeekView.isReleased = false;
        }


        // Set scheduler activities if use logs in after a while
        if (User_Account.loginTime == 0){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (final DataSnapshot ds: dataSnapshot.getChildren()){
                        String val = ds.toString();
                        final String actName = val.substring(val.indexOf('=')+1, val.indexOf(','));
                        String clubName, time, date;
                        clubName = "";
                        time = "";
                        date = "";
                        for (DataSnapshot ds2 : ds.getChildren()){
                            String str1 = ds2.toString();
                            String name = str1.substring(str1.lastIndexOf('{')+1,str1.lastIndexOf('='));
                            String val3 = str1.substring(str1.lastIndexOf('=')+1,str1.indexOf('}'));

                            if (name.equals("Time")){
                                time = val3;
                            }
                            else if (name.equals("Club Name")){
                                clubName = val3;
                            }
                            else if (name.equals("Date")) {
                                date = val3;
                            }

                        }
                        String hour = time.substring(0,time.indexOf(':'));
                        String min =  time.substring(time.indexOf(':'), time.length());

                        String year = date.substring(date.lastIndexOf("-")+1, date.length()).trim();
                        String day = date.substring(0,date.indexOf('-')).trim();
                        String month = date.substring(date.indexOf('-')+1,date.lastIndexOf('-')).trim();
                        if (day.length()==1){
                            day = "0"+day;
                        }

                        if (month.length()==1){
                            month = "0"+month;
                        }


                        if (min.length()==1){
                            min = "0"+min;
                        }

                        if (hour.length()==1){
                            hour = "0"+hour;
                        }

                        UserActivitiesObject test = new UserActivitiesObject(actName, clubName, "", hour+":"+min, day+"/"+month+"/"+year
                                ,"" , "","");
                        Scheduler.events.add( test.getWve());
                        }
                    }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        // Update login time to disallow duplicates of activity in calendar
        User_Account.loginTime = 1;

        return view;
    }
}
/*
 **  @author Hanzallah Burney
 * contributors: Khasmamad, Abdul Hamid
 */