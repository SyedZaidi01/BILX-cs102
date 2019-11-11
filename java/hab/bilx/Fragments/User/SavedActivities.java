package hab.bilx.Fragments.User;


import android.content.Intent;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import hab.bilx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *  The settings fragment for the admin class.
 *  @author Hanzallah Burney
 */
public class SavedActivities extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener  {
    public static List<SavedActivitiesObject> savedActivityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private SavedActivitiesAdapter savedAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    /*
     **  @author Hanzallah Burney
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View view =  inflater.inflate(R.layout.saved_activities, container, false);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        savedActivityList = new ArrayList<>();
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.myActivities_user);

        /*
         **  @author Hanzallah Burney
         */

        // swipe to refresh the fragment functionality
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshUserSavedAct);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new SavedActivities()).commit();
            }
        });

        /*
         **  @author Hanzallah Burney
         */

        // Add activities saved by the user
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // refresh the data list every time data is pulled from the database
                savedActivityList = new ArrayList<>();
                for (final DataSnapshot ds1: dataSnapshot.getChildren()){
                    // get activity name
                    String str = ds1.toString();
                    final String val2 = str.substring(str.indexOf('=')+1,str.indexOf(',')).trim();

                    // get the rest of the saved activity data
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(val2);
                    Query query = databaseReference2.orderByPriority();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ge, clubName, time, lang, loc, desc, date, status;
                            ge = "";
                            clubName = "";
                            time = "";
                            lang ="";
                            loc ="";
                            desc= "";
                            date = "";
                            status = "";
                            for (DataSnapshot ds2: ds1.getChildren()){
                                String str1 = ds2.toString();
                                String name = str1.substring(str1.lastIndexOf('{')+1,str1.lastIndexOf('='));
                                String val3 = str1.substring(str1.lastIndexOf('=')+1,str1.indexOf('}'));
                                if (name.equals("Time")){
                                    time = val3;
                                }
                                else if (name.equals("Club Name")){
                                    clubName = val3;
                                }
                                else if (name.equals("Location")){
                                    loc = val3;
                                }
                                else if (name.equals("GE")){
                                    ge = val3;
                                }
                                else if (name.equals("Language")){
                                    lang = val3;
                                }
                                else if (name.equals("Date")){
                                    date = val3;
                                }
                                else if (name.equals("Description")){
                                    desc = val3;
                                }
                            }

                            // set data to the view
                            recyclerView = (RecyclerView) view.findViewById(R.id.saved_activities_recycler_view);
                            savedAdapter = new SavedActivitiesAdapter(savedActivityList);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(savedAdapter);

                            addItem(new SavedActivitiesObject("Activity Name: " + val2,"Club: "+ clubName,
                                    "GE points: " + ge, "Time: " + time, "Date: " + date, "Location: " + loc, "Language: " + lang,
                                    "Activity Description: " + desc, "STATUS: " + status));

                            // enable swipe functionality
                            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
                            itemTouchHelper.attachToRecyclerView(recyclerView);
                        }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                            }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setHasOptionsMenu(true);
        return view;
    }

    /*
     **  @author Hanzallah Burney
     */

    public void addItem(SavedActivitiesObject newItem) {
        savedActivityList.add(0,newItem);
        savedAdapter.notifyDataSetChanged();
    }

    // if left swipe action occurs on a data view then add activity to phone calendar
    // id right swipe then add activity alarm on the phone
    /*
     **  @author Hanzallah Burney
     */

    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN
                ,ItemTouchHelper.RIGHT|ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    if (direction == ItemTouchHelper.RIGHT){
                        // Send to Alarm
                        final SavedActivitiesObject savedActivitiesObject = savedActivityList.get(viewHolder.getAdapterPosition());
                        String time = savedActivitiesObject.getTime().substring(
                                savedActivitiesObject.getTime().indexOf(':')+1,savedActivitiesObject.getTime().length()).trim();
                        String hour = time.substring(0,time.indexOf(':'));
                        String min = time.substring(time.indexOf(':')+1,time.length());

                        // open alarm
                        Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
                        i.putExtra(AlarmClock.EXTRA_HOUR, Integer.parseInt(hour));
                        i.putExtra(AlarmClock.EXTRA_MINUTES, Integer.parseInt(min));
                        startActivity(i);

                        Snackbar.make(getActivity().findViewById(R.id.saved_activitiesLayout), "Alarm Created", Snackbar.LENGTH_LONG).show();

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                new SavedActivities()).commit();
                    }
                    // Send to Calendar
                    if (direction == ItemTouchHelper.LEFT){
                        final SavedActivitiesObject savedActivitiesObject = savedActivityList.get(viewHolder.getAdapterPosition());
                        String time = savedActivitiesObject.getTime().substring(
                                savedActivitiesObject.getTime().indexOf(':')+1,savedActivitiesObject.getTime().length()).trim();
                        String hour = time.substring(0,time.indexOf(':'));
                        String min = time.substring(time.indexOf(':')+1,time.length());

                        String actName = savedActivitiesObject.getActivityName().substring(
                                savedActivitiesObject.getActivityName().indexOf(':')+1,savedActivitiesObject.getActivityName().length()).trim();
                        String desc = savedActivitiesObject.getDescription().substring(
                                savedActivitiesObject.getDescription().indexOf(':')+1,savedActivitiesObject.getDescription().length()).trim();
                        String loc = savedActivitiesObject.getLocation().substring(
                                savedActivitiesObject.getLocation().indexOf(':')+1,savedActivitiesObject.getLocation().length()).trim();

                        String lang = savedActivitiesObject.getLanguage().substring(
                                savedActivitiesObject.getLanguage().indexOf(':')+1,savedActivitiesObject.getLanguage().length()).trim();

                        String date = savedActivitiesObject.getDate().substring(
                                savedActivitiesObject.getDate().indexOf(':')+1,savedActivitiesObject.getDate().length()).trim();

                        String year = date.substring(date.lastIndexOf("-")+1, date.length()).trim();
                        String day = date.substring(0,date.indexOf('-')).trim();
                        String month = date.substring(date.indexOf('-')+1,date.lastIndexOf('-')).trim();

                        if (month.length() == 1){
                            month = "0"+month;
                        }



                        Calendar cal = Calendar.getInstance();
                        cal.set(Integer.parseInt(year), Integer.parseInt(month)-1, Integer.parseInt(day), Integer.parseInt(hour), Integer.parseInt(min));
                        long  startMillis = cal.getTimeInMillis();

                        // open calendar
                        Intent intent = new Intent(Intent.ACTION_EDIT);
                        intent.setType("vnd.android.cursor.item/event");
                        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, startMillis);
                        intent.putExtra("allDay", true);
                        intent.putExtra("eventLocation", loc);
                        intent.putExtra("description", "Language: " + lang + "\n"+ desc);
                        intent.putExtra("title", actName);
                        startActivity(intent);

                        Snackbar.make(getActivity().findViewById(R.id.saved_activitiesLayout), "Activity added to mobile calendar", Snackbar.LENGTH_LONG).show();

                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                new SavedActivities()).commit();
                    }

                }catch (Exception e){

                }

            }
        };
        return simpleCallback;
    }

    /*
     **  @author Hanzallah Burney
     */

    @Override
    public void onRefresh(){
        // Empty
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.user__account,menu );
        super.onCreateOptionsMenu(menu, inflater);
    }

    /*
     **  @author Hanzallah Burney
     */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        // sort by ge points
        if (id == R.id.action_gepoints) {
            if (savedActivityList.size() != 0){
                Collections.sort(savedActivityList, new Comparator<SavedActivitiesObject>() {
                    @Override
                    public int compare(SavedActivitiesObject activity1, SavedActivitiesObject activity2) {
                        String ge1 = activity1.getGe().substring(activity1.getGe().indexOf(':') + 1,
                                activity1.getGe().length()).trim();
                        String ge2 = activity2.getGe().substring(activity2.getGe().indexOf(':') + 1,
                                activity2.getGe().length()).trim();
                        return   Integer.parseInt(ge2) - Integer.parseInt(ge1);
                    }
                });
                savedAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        this).commit();

            }
            return true;
        }
        // sort by date
        else if (id == R.id.action_date){
            if (savedActivityList.size() != 0){
                Collections.sort(savedActivityList, new Comparator<SavedActivitiesObject>() {
                    @Override
                    public int compare(SavedActivitiesObject activity1, SavedActivitiesObject activity2) {
                        String date1 = activity1.getDate().substring(activity1.getDate().indexOf(':') + 1,
                                activity1.getDate().length()).trim();
                        String date2 = activity2.getDate().substring(activity2.getDate().indexOf(':') + 1,
                                activity2.getDate().length()).trim();

                        String year1 = date1.substring(date1.lastIndexOf("-") + 1, date1.length()).trim();
                        String day1 = date1.substring(0, date1.indexOf('-')).trim();
                        String month1 = date1.substring(date1.indexOf('-') + 1, date1.lastIndexOf('-')).trim();

                        String year2 = date2.substring(date2.lastIndexOf("-") + 1, date2.length()).trim();
                        String day2 = date2.substring(0, date2.indexOf('-')).trim();
                        String month2 = date2.substring(date2.indexOf('-') + 1, date2.lastIndexOf('-')).trim();

                        if (Integer.parseInt(year1) == Integer.parseInt(year2) && Integer.parseInt(month1) == Integer.parseInt(month2)) {
                            return Integer.parseInt(day1) - Integer.parseInt(day2);
                        } else if (Integer.parseInt(year1) == Integer.parseInt(year2) && Integer.parseInt(month1) != Integer.parseInt(month2)) {
                            return Integer.parseInt(month1) - Integer.parseInt(month2);
                        } else {
                            return Integer.parseInt(year2) - Integer.parseInt(year1);
                        }


                    }
                });
                savedAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        this).commit();
            }
            return true;
        }
        // sort by language
        else if (id == R.id.action_language){
            if (savedActivityList.size() != 0){
                Collections.sort(savedActivityList, new Comparator<SavedActivitiesObject>() {
                    @Override
                    public int compare(SavedActivitiesObject activity1, SavedActivitiesObject activity2) {
                        String lang1 = activity1.getLanguage().substring(activity1.getLanguage().indexOf(':') + 1,
                                activity1.getLanguage().length()).trim();
                        String lang2 = activity2.getLanguage().substring(activity2.getLanguage().indexOf(':') + 1,
                                activity2.getLanguage().length()).trim();

                        int l1;
                        int l2;

                        if (lang1.equals("English")){
                            l1 = 1;
                            l2 = 0;
                        }
                        else {
                            l1 = 0;
                            l2 = 1;
                        }
                        return l2-l1;
                    }
                });
                savedAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        this).commit();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
/*
 **  @author Hanzallah Burney
 */
