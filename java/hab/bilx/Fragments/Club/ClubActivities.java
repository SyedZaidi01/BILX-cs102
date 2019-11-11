package hab.bilx.Fragments.Club;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
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
import java.util.List;

/**
 *  The settings fragment for the admin class.
 *  @author Hanzallah Burney
 */

public class ClubActivities extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static List<ClubActivitiesObject> clubActivityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClubActivitiesAdapter adapter;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View view =  inflater.inflate(R.layout.club_activities, container, false);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        clubActivityList = new ArrayList<>();

        // create activity button initialize
        fab = (FloatingActionButton) view.findViewById(R.id.club_activities_fab);

       ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.clubActivities);

       // Swipe the fragment to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshClubAct);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new ClubActivities()).commit();
            }
        });


       // Add Items to the fragment from firebase database ========================================================
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Club Activities")
                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                clubActivityList = new ArrayList<>();
                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Club Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot ds1: dataSnapshot.getChildren()){
                                String str = ds1.toString();
                                final String val2 = str.substring(str.indexOf('=')+1,str.indexOf(',')).trim();
                                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Approve Activities")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                                Query query = databaseReference2.orderByPriority();
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String ge, time, lang, loc, desc, date, status;
                                        ge = "";
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
                                            else if (name.equals("Status")){
                                                if (val3.equals("True")){
                                                    status = "APPROVED";
                                                }
                                                else if (val3.equals("False")){
                                                    status = "REJECTED";
                                                }
                                                else {
                                                    status = val3;
                                                }
                                            }
                                        }
                                        recyclerView = (RecyclerView) view.findViewById(R.id.club_activities_recycler_view);
                                        adapter = new ClubActivitiesAdapter(clubActivityList);

                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(adapter);

                                        addItem(new ClubActivitiesObject("Activity Name: " + val2,
                                                "GE points: " + ge, "Time: " + time, "Date: " + date, "Location: " + loc, "Language: " + lang,
                                                "Activity Description: " + desc, "STATUS: " + status));

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

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //====================================================================


        // Go to create activity page when fab is clicked
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, new CreateNewActivity());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    public void addItem(ClubActivitiesObject newItem) {
        clubActivityList.add(0,newItem);
        adapter.notifyDataSetChanged();
    }

    // Swipe left to delete the item. If status pending then also delete from the admin account
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
                    ClubActivitiesObject clubActivitiesObject;
                    String actName;
                    String status;

                    clubActivitiesObject = clubActivityList.get(viewHolder.getAdapterPosition());
                    actName = clubActivitiesObject.getActivityName().substring(clubActivitiesObject.getActivityName().indexOf(':') + 1,
                            clubActivitiesObject.getActivityName().length()).trim();
                    status = clubActivitiesObject.getStatus();

                    // remove from admin if status is pending
                    if (status.contains("PENDING")) {
                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Approve Activities")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(actName);
                        reference1.removeValue();
                    }

                    // remove from club database
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Club Activities")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(actName);
                    reference.removeValue();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                            new ClubActivities()).commit();
                    Snackbar.make(getActivity().findViewById(R.id.club_activitiesLayout), "Activity Deleted", Snackbar.LENGTH_LONG).show();

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