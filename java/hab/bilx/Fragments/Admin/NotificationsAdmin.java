package hab.bilx.Fragments.Admin;

import android.app.Notification;
import android.app.NotificationManager;
import android.os.Bundle;
import android.service.autofill.Dataset;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import hab.bilx.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 *  The notifications fragment for the admin class.
 *  @author Hanzallah Burney
 */

public class NotificationsAdmin extends android.support.v4.app.Fragment {
    private Spinner admin_spinner;
    private EditText notify_text;
    private FloatingActionButton fab;
    private Map notify;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        // set view
        View view = inflater.inflate(R.layout.notifcations_admin, container, false);

        // initialize variables
        admin_spinner = (Spinner) view.findViewById(R.id.sendTo_spinner);
        notify_text = (EditText) view.findViewById(R.id.Notification_text);
        fab = (FloatingActionButton) view.findViewById(R.id.floatingActionButton);

        /*
         *  @author Hanzallah Burney
         */
        // Get the selected item for the list = users, clubs, both to whom notifications are to be sent
        admin_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Object item = parent.getItemAtPosition(pos);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        /*
         *  @author Hanzallah Burney
         */
        // Actions when send button is clicked
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the notification text
                String str = notify_text.getText().toString();

                // Create notification for admin itself for preview purposes
                final String s = str.replace(".","_");
                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getActivity())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Bilkent Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(s))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
                Notification notification = mBuilder.build();

                //-------------------- Username database --------------------------------
                // Structure of database
                // Create hashmap and put user email as child node of username
                // sets check notify for respective audiences to ture to ensure they receive notification and adds the notification to their list
                // All in firebase database
                /*
                 *  @author Hanzallah Burney
                 */
                notify = new HashMap();
                // Send notification to user
                if (admin_spinner.getSelectedItem().toString().equals("Users")){

                    DatabaseReference current_user = FirebaseDatabase.getInstance().getReference()
                            .child("Notifications").child("Users").child("Message");
                    notify.put("Value",s );
                    current_user.setValue(notify);

                    DatabaseReference users_data = FirebaseDatabase.getInstance().getReference()
                            .child("Check Notify");
                    users_data.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                final String name = ds.toString().substring(ds.toString().indexOf('=') + 1
                                        , ds.toString().indexOf(',')).trim();

                                DatabaseReference check = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(name);
                                check.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!dataSnapshot.toString().contains("club")) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notification List")
                                                    .child(name.trim()).child(s).child(s);
                                            ref.setValue(s);

                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Notification List")
                                                    .child(name.trim()).child(s).child("Date");

                                            ref2.setValue((new Date()).getTime()); // to order notifications for user by time

                                            DatabaseReference dat = FirebaseDatabase.getInstance().getReference()
                                                    .child("Check Notify").child(name).child("Users");

                                            Map check = new HashMap();
                                            check.put("Bool", "true");
                                            dat.setValue(check);

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
                    Snackbar.make(getActivity().findViewById(R.id.admin_notifications), "Notification Sent to Users", Snackbar.LENGTH_LONG).show();
                }
                // Send notification to clubs
                else if (admin_spinner.getSelectedItem().toString().equals("Clubs")){
                    DatabaseReference current_user = FirebaseDatabase.getInstance().getReference()
                            .child("Notifications").child("Clubs").child("Message");
                    notify.put("Value",s );
                    current_user.setValue(notify);


                    DatabaseReference users_data = FirebaseDatabase.getInstance().getReference()
                            .child("Check Notify");
                    users_data.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            for (final DataSnapshot ds : dataSnapshot.getChildren()) {
                                final String name = ds.toString().substring(ds.toString().indexOf('=') + 1
                                        , ds.toString().indexOf(',')).trim();



                                DatabaseReference check = FirebaseDatabase.getInstance().getReference()
                                        .child("Users").child(name);
                                check.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.toString().contains("club")){
                                            String name = ds.toString().substring(ds.toString().indexOf('=')+1,
                                                    ds.toString().indexOf(','));
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notification List")
                                                    .child(name.trim()).child(s).child(s);
                                            ref.setValue(s);

                                            DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Notification List")
                                                    .child(name.trim()).child(s).child("Date");

                                            ref2.setValue((new Date()).getTime());

                                            DatabaseReference dat = FirebaseDatabase.getInstance().getReference()
                                                    .child("Check Notify").child(name.trim()).child("Clubs");

                                            Map check = new HashMap();
                                            check.put("Bool", "true");
                                            dat.setValue(check);
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

                    Snackbar.make(getActivity().findViewById(R.id.admin_notifications), "Notification Sent to Clubs", Snackbar.LENGTH_LONG).show();

                }
                // Send notification to all
                else{
                    DatabaseReference current_user = FirebaseDatabase.getInstance().getReference()
                            .child("Notifications").child("Both").child("Message");
                    notify.put("Value",s );
                    current_user.setValue(notify);

                        DatabaseReference users_data = FirebaseDatabase.getInstance().getReference()
                                .child("Check Notify");
                        users_data.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    String name = ds.toString().substring(ds.toString().indexOf('=') + 1
                                            , ds.toString().indexOf(',')).trim();

                                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Notification List")
                                            .child(name.trim()).child(s).child(s);
                                    ref.setValue(s);

                                    DatabaseReference ref2 = FirebaseDatabase.getInstance().getReference().child("Notification List")
                                            .child(name.trim()).child(s).child("Date");

                                    ref2.setValue((new Date()).getTime());
                                    DatabaseReference dat = FirebaseDatabase.getInstance().getReference()
                                            .child("Check Notify").child(name.trim()).child("Both");

                                    Map check = new HashMap();
                                    check.put("Bool", "true");
                                    dat.setValue(check);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    Snackbar.make(getActivity().findViewById(R.id.admin_notifications), "Notification Sent to All", Snackbar.LENGTH_LONG).show();

                }
                NotificationManagerCompat.from(getActivity()).notify(0,notification);
            }
        });

        return view;
    }
}
/*
 *  @author Hanzallah Burney
 */