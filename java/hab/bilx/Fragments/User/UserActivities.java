package hab.bilx.Fragments.User;

import android.os.Bundle;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The user activities fragment.
 *  @author Hanzallah Burney
 */

public class UserActivities extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static List<UserActivitiesObject> userActivityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private UserActivitiesAdapter userAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    /*
     **  @author Hanzallah Burney
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View view =  inflater.inflate(R.layout.user_activities, container, false);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.allActivities_user);

        /*
         **  @author Hanzallah Burney
         */

        // swipe to refresh the fragment functionality
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshUserAct);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new UserActivities()).commit();
            }
        });

        /*
         **  @author Hanzallah Burney
         */

        // Add activities to all activities fragment for user
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Activities")
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // refresh the data list every time data is pulled from the database
                userActivityList = new ArrayList<>();
                for (final DataSnapshot ds1: dataSnapshot.getChildren()){
                    // get activity name
                    String str = ds1.toString();
                    final String val2 = str.substring(str.indexOf('=')+1,str.indexOf(',')).trim();

                    // get the rest of the activity data
                    DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("User Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(val2);
                    Query query = databaseReference2.orderByPriority();
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            String ge, clubName, time, lang, loc, desc, date;
                            ge = "";
                            clubName = "";
                            time = "";
                            lang = "";
                            loc = "";
                            desc = "";
                            date = "";
                            for (DataSnapshot ds2 : ds1.getChildren()) {
                                String str1 = ds2.toString();
                                String name = str1.substring(str1.lastIndexOf('{') + 1, str1.lastIndexOf('='));
                                String val3 = str1.substring(str1.lastIndexOf('=') + 1, str1.indexOf('}'));
                                if (name.equals("Time")) {
                                    time = val3;
                                } else if (name.equals("Club Name")) {
                                    clubName = val3;
                                } else if (name.equals("Location")) {
                                    loc = val3;
                                } else if (name.equals("GE")) {
                                    ge = val3;
                                } else if (name.equals("Language")) {
                                    lang = val3;
                                } else if (name.equals("Date")) {
                                    date = val3;
                                } else if (name.equals("Description")) {
                                    desc = val3;
                                }
                            }

                            // set data to the view
                            recyclerView = (RecyclerView) view.findViewById(R.id.user_activities_recycler_view);
                            userAdapter = new UserActivitiesAdapter(userActivityList);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(userAdapter);

                            // add data into the view
                            addItem(new UserActivitiesObject("Activity Name: " + val2, "Club: " + clubName,
                                    "GE points: " + ge, "Time: " + time, "Date: " + date, "Location: " + loc, "Language: " + lang,
                                    "Activity Description: " + desc));

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
            if (userActivityList.size() != 0) {
                Collections.sort(userActivityList, new Comparator<UserActivitiesObject>() {
                    @Override
                    public int compare(UserActivitiesObject activity1, UserActivitiesObject activity2) {
                        String ge1 = activity1.getGe().substring(activity1.getGe().indexOf(':') + 1,
                                activity1.getGe().length()).trim();
                        String ge2 = activity2.getGe().substring(activity2.getGe().indexOf(':') + 1,
                                activity2.getGe().length()).trim();
                        return   Integer.parseInt(ge2) - Integer.parseInt(ge1);
                    }
                });
                userAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        this).commit();
            }
            return true;
        }
        // sort by date
        else if (id == R.id.action_date){
            if (userActivityList.size() != 0) {
                Collections.sort(userActivityList, new Comparator<UserActivitiesObject>() {
                    @Override
                    public int compare(UserActivitiesObject activity1, UserActivitiesObject activity2) {
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
                userAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        this).commit();
            }
            return true;
        }
        // sort by language
        else if (id == R.id.action_language){
            if (userActivityList.size() != 0){
                Collections.sort(userActivityList, new Comparator<UserActivitiesObject>() {
                    @Override
                    public int compare(UserActivitiesObject activity1, UserActivitiesObject activity2) {
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
                userAdapter.notifyDataSetChanged();
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        this).commit();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
     **  @author Hanzallah Burney
     */

    public void addItem(UserActivitiesObject newItem) {
        userActivityList.add(0,newItem);
        userAdapter.notifyDataSetChanged();
    }

    /*
     **  @author Hanzallah Burney
     */

    // if swipe is left then add data to saved activities for the user and add it to the calendar
    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN
                ,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    // get all the data present at the swiped position
                    UserActivitiesObject userActivitiesObject = userActivityList.get(viewHolder.getAdapterPosition());

                    String actName = userActivitiesObject.getActivityName().substring(userActivitiesObject.getActivityName().indexOf(':') + 1,
                            userActivitiesObject.getActivityName().length()).trim();
                    String clubName = userActivitiesObject.getClubName().substring(userActivitiesObject.getClubName().indexOf(':') + 1,
                            userActivitiesObject.getClubName().length()).trim();
                    String ge = userActivitiesObject.getGe().substring(userActivitiesObject.getGe().indexOf(':') + 1,
                            userActivitiesObject.getGe().length()).trim();
                    String time = userActivitiesObject.getTime().substring(userActivitiesObject.getTime().indexOf(':') + 1,
                            userActivitiesObject.getTime().length()).trim();
                    String date = userActivitiesObject.getDate().substring(userActivitiesObject.getDate().indexOf(':') + 1,
                            userActivitiesObject.getDate().length()).trim();
                    String location = userActivitiesObject.getLocation().substring(userActivitiesObject.getLocation().indexOf(':') + 1,
                            userActivitiesObject.getLocation().length()).trim();
                    String language = userActivitiesObject.getLanguage().substring(userActivitiesObject.getLanguage().indexOf(':') + 1,
                            userActivitiesObject.getLanguage().length()).trim();
                    String description = userActivitiesObject.getDescription().substring(userActivitiesObject.getDescription().indexOf(':') + 1,
                            userActivitiesObject.getDescription().length()).trim();

                    // put all that data into the database under saved activities
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    Map savedAct = new HashMap();
                    databaseReference = databaseReference.child(actName).child("GE");
                    savedAct.put("GE",ge.trim());
                    databaseReference.setValue(savedAct);


                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    savedAct = new HashMap();
                    databaseReference = databaseReference.child(actName).child("Date");
                    savedAct.put("Date",date.trim());
                    databaseReference.setValue(savedAct);

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    savedAct = new HashMap();
                    databaseReference = databaseReference.child(actName).child("Club Name");
                    savedAct.put("Club Name",clubName.trim());
                    databaseReference.setValue(savedAct);

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    savedAct = new HashMap();
                        databaseReference = databaseReference.child(actName).child("Time");
                    savedAct.put("Time",time.trim());
                    databaseReference.setValue(savedAct);

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    savedAct = new HashMap();
                    databaseReference = databaseReference.child(actName).child("Language");
                    savedAct.put("Language",language.trim());
                    databaseReference.setValue(savedAct);

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    savedAct = new HashMap();
                    databaseReference = databaseReference.child(actName).child("Location");
                    savedAct.put("Location",location.trim());
                    databaseReference.setValue(savedAct);

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("Saved Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    savedAct = new HashMap();
                    databaseReference = databaseReference.child(actName).child("Description");
                    savedAct.put("Description",description.trim());
                    databaseReference.setValue(savedAct);





                    // Get it to calendar here
                    String year = date.substring(date.lastIndexOf("-")+1, date.length()).trim();
                    String day = date.substring(0,date.indexOf('-')).trim();
                    String month = date.substring(date.indexOf('-')+1,date.lastIndexOf('-')).trim();

                    String min = time.substring(time.indexOf(":")+1, time.length()).trim();
                    String hour = time.substring(0,time.indexOf(':')).trim();

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

                    // Add to calendar
                    final UserActivitiesObject test = new UserActivitiesObject(actName, clubName, ge, hour+":"+min, day+"/"+month+"/"+year,location , language,"");
                    Scheduler.events.add( test.getWve());

                    Snackbar.make(getActivity().findViewById(R.id.use_activitiesLayout), "Activity saved and added to scheduler", Snackbar.LENGTH_LONG).show();

                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                            new UserActivities()).commit();
                }catch (Exception e){
                    System.out.println("Exception generated");
                }

            }
        };
        return simpleCallback;
    }

    @Override
    public void onRefresh(){
        // Empty
    }
}
/*
 **  @author Hanzallah Burney
 */
