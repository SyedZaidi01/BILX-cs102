package hab.bilx.Fragments.Admin;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.SwitchPreferenceCompat;
import android.widget.Toast;

import hab.bilx.Accounts.Admin_Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import hab.bilx.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


/**
 *  The settings fragment for the admin class.
 *  @author Hanzallah Burney
 */

public class SettingsFragment_admin extends PreferenceFragmentCompat {
    private SwitchPreferenceCompat darkMode;
    private Preference resetPassword;
    private Preference reportBugs;


    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // Load the Preferences from the XML file
        addPreferencesFromResource(R.xml.app_preferences);
        darkMode = (SwitchPreferenceCompat) findPreference("theme_mode");
        resetPassword = (Preference) findPreference("password_reset");
        reportBugs = (Preference) findPreference("admin_report_bug");

        /*
         *  @author Hanzallah Burney
         */
        // Implementation of dark mode
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Dark Mode")
                .child("admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().contains("true")){
                    darkMode.setChecked(true);
                }
                else{
                    darkMode.setChecked(false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*
         *  @author Hanzallah Burney
         */
        // Action for when dark mode is clicked by updating database and recreating activity in the new mode
        darkMode.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference pref, Object object) {
                boolean isChecked = (Boolean) object;
                Map mode = new HashMap();
                if (isChecked) {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Dark Mode")
                            .child("admin");
                    mode.put("Mode","true");
                    databaseReference.setValue(mode);
                    Admin_Account.count++;
                    getActivity().recreate();
                } else {
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Dark Mode")
                            .child("admin");
                    mode.put("Mode","false");
                    databaseReference.setValue(mode);
                    Admin_Account.count++;
                    getActivity().recreate();
                }
                return true;
            }
        });

        /*
         *  @author Hanzallah Burney
         */
        // Report Bugs
        reportBugs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent(Intent.ACTION_SENDTO); // it's not ACTION_SEND
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "BILX Bug Report");
                intent.setData(Uri.fromParts("mailto",
                        "azim.burney@ug.bilkent.edu.tr", null)); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
                return true;
            }
        });


        /*
         *  @author Hanzallah Burney
         */
        // Reset Password
        resetPassword.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            public boolean onPreferenceClick(Preference preference) {
                String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                FirebaseAuth firebaseauth = FirebaseAuth.getInstance();
                firebaseauth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Snackbar.make(getView(), "Email sent", Snackbar.LENGTH_LONG).show();
                                    } else {
                                    Snackbar.make(getView(), "Enter valid email", Snackbar.LENGTH_LONG).show();
                                }
                            }
                        });
                return true;
            }
        });
    }
}
/*
 *  @author Hanzallah Burney
 */