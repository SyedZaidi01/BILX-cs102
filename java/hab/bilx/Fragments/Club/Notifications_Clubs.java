package hab.bilx.Fragments.Club;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.Timer;
import java.util.TimerTask;

/**
 * The club notification fragment for club class
 * @author Hanzallah Burney
 */

public class Notifications_Clubs extends android.support.v4.app.Fragment implements SwipeRefreshLayout.OnRefreshListener {

    public static List<ClubNotificationObject> clubNotifyList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClubNotificationsAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Timer timer;

    /*
     **  @author Hanzallah Burney
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.club_notifications, container, false);
        final RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        clubNotifyList = new ArrayList<>();

        // Initialize and set timer
        timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    // Get club notifications from the database
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                            .child("Notification List").child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
                    Query q = databaseReference.orderByChild("Date");

                    q.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot shot) {
                            // refresh the data list every time data is pulled from the database
                            clubNotifyList = new ArrayList<>();
                            for (DataSnapshot ds : shot.getChildren()) {
                                String s = ds.getValue().toString();
                                s = s.replace("_",".");
                                String val = s.substring(s.indexOf(',') + 1, s.lastIndexOf('=')).trim();

                                // Set the data to the view
                                adapter = new ClubNotificationsAdapter(clubNotifyList);
                                recyclerView = (RecyclerView) view.findViewById(R.id.club_recycler_view);
                                recyclerView.setLayoutManager(mLayoutManager);
                                recyclerView.setItemAnimator(new DefaultItemAnimator());
                                recyclerView.setAdapter(adapter);

                                if (!val.equals("Date")) {
                                    addItem(new ClubNotificationObject("Administrator Notification", val, ""));
                                }
                                else {
                                    val = s.substring(s.indexOf('{')+1,s.indexOf('=')).trim();
                                    addItem(new ClubNotificationObject("Administrator Notification", val, ""));
                                }

                                // enable swipe functionality
                                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(createHelperCallback());
                                itemTouchHelper.attachToRecyclerView(recyclerView);
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                } catch (NullPointerException e) {
                    System.out.println("Null Pointer Exception");
                }

            }
        };
        timer.scheduleAtFixedRate(timerTask, 0, 2 * 1000);

        /*
         **  @author Hanzallah Burney
         */

        // swipe to refresh the fragment functionality
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                new Notifications_Clubs()).commit();
            }
        });

        return view;
    }

    /*
     **  @author Hanzallah Burney
     */

    public void addItem(ClubNotificationObject newItem) {
        clubNotifyList.add(0, newItem);
        adapter.notifyDataSetChanged();
    }

    /*
     **  @author Hanzallah Burney
     */

    // if left swipe action occurs on a data view then delete that data
    private ItemTouchHelper.Callback createHelperCallback(){
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP|ItemTouchHelper.DOWN
                ,ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                try {
                    // get data
                    ClubNotificationObject clubNotificationObject = clubNotifyList.get(viewHolder.getAdapterPosition());
                    String subject = clubNotificationObject.getSubject();
                    subject = subject.replace(".","_");

                    // remove data from firebase
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Notification List")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()).child(subject);
                    reference.removeValue();

                    // refresh fragment
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame,
                            new Notifications_Clubs()).commit();
                    Snackbar.make(getActivity().findViewById(R.id.club_notificationsLayout), "Notification Deleted", Snackbar.LENGTH_LONG).show();
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
