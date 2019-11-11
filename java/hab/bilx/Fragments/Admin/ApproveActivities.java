package hab.bilx.Fragments.Admin;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hab.bilx.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  The approve activities fragment for the admin class.
 *  @author Hanzallah Burney
 */

public class ApproveActivities extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {
    public static List<ApproveActivitiesObject> approveActivityList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ApproveActivitiesAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;

    /*
     *  @author Hanzallah Burney
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View view =  inflater.inflate(R.layout.approve_activities, container, false);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        approveActivityList = new ArrayList<>();

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(R.string.ApproveActivities);

        // Get the activities to approve from the database
        /*
         *  @author Hanzallah Burney
         */
        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Approve Activities");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                approveActivityList = new ArrayList<>();
                for (final DataSnapshot ds: dataSnapshot.getChildren() ){
                    String s = ds.toString();
                    final String val = s.substring(s.indexOf('=')+1,s.indexOf(','));

                    DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("Approve Activities").child(val);
                    databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot ds1: ds.getChildren()){
                                String str = ds1.toString();
                                final String val2 = str.substring(str.indexOf('=')+1,str.indexOf(',')).trim();

                                DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference().child("Approve Activities")
                                        .child(val);
                                Query query = databaseReference2.orderByPriority();
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        String ge,time, lang, loc,desc, date;
                                        ge = "";
                                        time = "";
                                        lang ="";
                                        loc ="";
                                        desc= "";
                                        date = "";
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
                                        }
                                        // Set t he data into the recycler view
                                        adapter = new ApproveActivitiesAdapter(approveActivityList);
                                        recyclerView = (RecyclerView) view.findViewById(R.id.approveAct_recycler_view);

                                        recyclerView.setLayoutManager(mLayoutManager);
                                        recyclerView.setItemAnimator(new DefaultItemAnimator());
                                        recyclerView.setAdapter(adapter);

                                        // Add the data
                                        addItem(new ApproveActivitiesObject("Activity Name: "+ val2,
                                                "Club Name: "+ val,"GE Points: "+ ge,
                                                "Time: "+ time,"Date: "+ date,"Location: "+ loc,
                                                "Language: "+ lang,"Activity Description: "+ desc));

                                        // Enable swipe functionality
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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        /*
         *  @author Hanzallah Burney
         */
        // Swipe the fragment to refresh
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshApprove);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                        new ApproveActivities()).commit();
            }
        });

        return view;
    }
    /*
     *  @author Hanzallah Burney
     */
    public void addItem(ApproveActivitiesObject newItem) {
        approveActivityList.add(0,newItem);
        adapter.notifyDataSetChanged();
    }
    /*
     *  @author Hanzallah Burney
     */
    // If swipe left then prompt a dialog box to allow admin to approve or reject an activity
    private ItemTouchHelper.Callback createHelperCallback() {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN
                , ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                    final AlertDialog.Builder builder;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
                    } else {
                        builder = new AlertDialog.Builder(getActivity());
                    }
                    builder.setTitle("Approve or Reject Activity")
                            .setMessage("Do you want to approve the activity or reject it?");
                    builder.setNegativeButton("Approve", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Get the data from the specific position
                            ApproveActivitiesObject approveActivitiesObject = approveActivityList.get(viewHolder.getAdapterPosition());
                            final String clubName = approveActivitiesObject.getClubName().substring(
                                    approveActivitiesObject.getClubName().indexOf(':')+1,approveActivitiesObject.getClubName().length()).trim();
                            final String activityName = approveActivitiesObject.getActivityName().substring(
                                    approveActivitiesObject.getActivityName().indexOf(':')+1,approveActivitiesObject.getActivityName().length()).trim();

                            final String date = approveActivitiesObject.getDate().substring(
                                    approveActivitiesObject.getDate().indexOf(':')+1,approveActivitiesObject.getDate().length()).trim();

                            final String time = approveActivitiesObject.getTime().substring(
                                    approveActivitiesObject.getTime().indexOf(':')+1,approveActivitiesObject.getTime().length()).trim();

                            final String ge = approveActivitiesObject.getGe().substring(
                                    approveActivitiesObject.getGe().indexOf(':')+1,approveActivitiesObject.getGe().length()).trim();

                            final String language = approveActivitiesObject.getLanguage().substring(
                                    approveActivitiesObject.getLanguage().indexOf(':')+1,approveActivitiesObject.getLanguage().length()).trim();

                            final String location = approveActivitiesObject.getLocation().substring(
                                    approveActivitiesObject.getLocation().indexOf(':')+1,approveActivitiesObject.getLocation().length()).trim();

                            final String description = approveActivitiesObject.getDescription().substring(
                                    approveActivitiesObject.getDescription().indexOf(':')+1,approveActivitiesObject.getDescription().length()).trim();

                            // Approval for club
                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Club Activities")
                                    .child(clubName).child(activityName).child("Status");
                            Map status = new HashMap();
                            status.put("Status","True");
                            ref.setValue(status);

                            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Approve Activities").child(clubName).child(activityName);
                            reference.removeValue();
                            Snackbar.make(getActivity().findViewById(R.id.approveAct), "Approval sent to club", Snackbar.LENGTH_LONG).show();

                            // Add to Users List ===============================================================================================
                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Users");
                            ref2.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot ds : dataSnapshot.getChildren()){
                                        if (!ds.toString().contains("club")){
                                            String userName = ds.toString();
                                            final String name = userName.substring(userName.indexOf('=')+1,userName.indexOf(',')).trim();
                                            DatabaseReference databaseReference1;
                                            Map activityValues;
                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("GE");
                                            activityValues.put("GE",ge.trim());
                                            databaseReference1.setValue(activityValues);

                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("Club Name");
                                            activityValues.put("Club Name",clubName.trim());
                                            databaseReference1.setValue(activityValues);

                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("Time");
                                            activityValues.put("Time",time.trim());
                                            databaseReference1.setValue(activityValues);



                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("Date");
                                            activityValues.put("Date",date.trim());
                                            databaseReference1.setValue(activityValues);


                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("Location");
                                            activityValues.put("Location",location.trim());
                                            databaseReference1.setValue(activityValues);

                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("Language");
                                            activityValues.put("Language",language.trim());
                                            databaseReference1.setValue(activityValues);


                                            activityValues = new HashMap();
                                            databaseReference1 = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName).child("Description");
                                            activityValues.put("Description",description.trim());
                                            databaseReference1.setValue(activityValues);

                                            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("User Activities").child(name)
                                                    .child(activityName);
                                            databaseReference.setPriority((new Date()).getTime());

                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                            /*
                             *  @author Hanzallah Burney
                             */
                            //======================================================================================================================================================
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                    new ApproveActivities()).commit();
                        }
                    });
                    builder.setPositiveButton("Reject", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ApproveActivitiesObject approveActivitiesObject = approveActivityList.get(viewHolder.getAdapterPosition());
                                String clubName = approveActivitiesObject.getClubName().substring(
                                        approveActivitiesObject.getClubName().indexOf(':')+1,approveActivitiesObject.getClubName().length()).trim();
                                String activityName = approveActivitiesObject.getActivityName().substring(
                                        approveActivitiesObject.getActivityName().indexOf(':')+1,approveActivitiesObject.getActivityName().length()).trim();

                                // Rejection to club
                                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Club Activities")
                                        .child(clubName).child(activityName).child("Status");
                                Map status = new HashMap();
                                status.put("Status","False");
                                ref.setValue(status);

                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Approve Activities").child(clubName).child(activityName);
                                reference.removeValue();
                                Snackbar.make(getActivity().findViewById(R.id.approveAct), "Rejection sent to club", Snackbar.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                                        new ApproveActivities()).commit();
                            } catch (Exception e){
                                System.out.println("Exception generated");
                            }
                        }
                    }).show();
            }

        };
        return simpleCallback;
    }
    @Override
    public void onRefresh(){
        // Empty
    }
}/*
 *  @author Hanzallah Burney
 */