package hab.bilx;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.*;

/**
 * Implements the signup activity allowing a user to create their account in the app.
 * @author Hanzallah Burney
 * @version 1.00 23-03-2018
 */

public class SignUp extends AppCompatActivity {
    // Set variables for fields and firebase
    private EditText email_login, password, password_retype, username;
    private CheckBox club;
    private FirebaseAuth firebaseAuth;
    private String user_name;
    private Map newUser;
    private Map check;
    private Query usernameQuery;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        // Initialize variables
        email_login = findViewById(R.id.email_signup);
        password = findViewById(R.id.password_signup);
        password_retype = findViewById(R.id.password_signup2);
        username = findViewById(R.id.username);
        club = findViewById(R.id.club_checkbox);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    // Overrides transition animation when back button is pressed
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slidein_left,R.anim.slideout_right);
    }

    /**
     * Performs signup functions when signup button is clicked
     * @param view - the button view
     */
    public void signup_click(View view){
        // Creates a progress dialog box
        final ProgressDialog progressDialog = ProgressDialog.show(SignUp.this,
                "Please Wait", "Processing",true);
        /*
         *  @author Hanzallah Burney
         */
        // Get the username and set database reference
        user_name = username.getText().toString().trim();
        usernameQuery = FirebaseDatabase.getInstance().getReference().child("Users").child(user_name);
        usernameQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressDialog.dismiss();
                // Check if username entered or not
                if (user_name.equals("")){
                    Toast.makeText(SignUp.this,"Some Field Is Empty", Toast.LENGTH_LONG)
                            .show();
                }
                // Check that username does not contain the @ symbol
                else if (user_name.contains("@")){
                    Toast.makeText(SignUp.this,"Username cannot contain @", Toast.LENGTH_LONG)
                            .show();
                }
                // If the username is already present then tell the user to enter a different username
                else if (dataSnapshot.getChildrenCount() > 0){
                    Toast.makeText(SignUp.this,"Choose a different username", Toast.LENGTH_LONG)
                            .show();
                }
                else{
                    // Create and initialize password check patterns to ensure password contains one of each
                    Pattern letter = Pattern.compile("[a-zA-z]");
                    Pattern digit = Pattern.compile("[0-9]");
                    Pattern special = Pattern.compile ("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

                    // Set text to be matched against the password patterns
                    Matcher hasLetter = letter.matcher(password.getText().toString());
                    Matcher hasDigit = digit.matcher(password.getText().toString());
                    Matcher hasSpecial = special.matcher(password.getText().toString());

                    // If any field in empty show toast
                    if  (email_login.getText().toString().equals("") || password.getText().toString().equals("") || password_retype.getText().toString().equals("")){
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this,"Some Field Is Empty",Toast.LENGTH_LONG).show();
                    }
                    // If password and password retype activities don't match show toast
                    else if (!password.getText().toString().equals(password_retype.getText().toString())){
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this,"Passwords don't match",Toast.LENGTH_LONG).show();
                    }
                    // If password does not contain a small letter, a capital letter, a number and a special character show toast
                    else if (!hasLetter.find() || !hasDigit.find() || !hasSpecial.find()){
                        progressDialog.dismiss();
                        Toast.makeText(SignUp.this,"Password must contain a small alphabet, a capital alphabet, a number and a special character",Toast.LENGTH_LONG).show();
                    }
                    // Create account and return to login page
                    else{
                        (firebaseAuth.createUserWithEmailAndPassword(email_login.getText().toString().trim(), password.getText().toString().trim()))
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        progressDialog.dismiss();
                                        if (task.isSuccessful()){
                                            //-------------------- Username database -------------------------------
                                            // Structure of database
                                            DatabaseReference current_user = FirebaseDatabase.getInstance().getReference()
                                                    .child("Users").child(user_name);
                                            // Create hashmap and put user email as child node of username
                                            newUser = new HashMap();
                                            check = new HashMap();
                                            // If club checkbox is ticked
                                            if (club.isChecked()){
                                                // Create alert dialog box for club to enter passcode
                                                final AlertDialog.Builder builder;
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                                    builder = new AlertDialog.Builder(SignUp.this, android.R.style.Theme_Material_Dialog_Alert);
                                                } else {
                                                    builder = new AlertDialog.Builder(SignUp.this);
                                                }
                                                builder.setTitle("Club Account Confirmation")
                                                        .setMessage("Enter The Passcode Assigned");
                                                        final EditText input = new EditText(SignUp.this);
                                                        input.setInputType(InputType.TYPE_CLASS_NUMBER);
                                                        input.setTextColor(Color.WHITE);
                                                        builder.setView(input);
                                                        // Do not create club or any other account if cancel clicked in the dialog box
                                                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog, int which) {
                                                                        builder.setCancelable(true);
                                                                        FirebaseAuth.getInstance().getCurrentUser().delete();
                                                                    }
                                                                });
                                                        // If passcode entered and ok pressed then verify passcode and create club account else prompt club again
                                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            public void onClick(DialogInterface dialog, int which) {
                                                                final String s = input.getText().toString();

                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Passcodes");
                                                                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        int index = 0;
                                                                        for (DataSnapshot ds: dataSnapshot.getChildren()){
                                                                            index++;
                                                                            // If passcode is valid then create club account
                                                                            if (ds.getValue().toString().contains(s)){
                                                                                // Set the club account under club node
                                                                                DatabaseReference current_user = FirebaseDatabase.getInstance().getReference()
                                                                                        .child("Users").child(user_name).child("club");
                                                                                newUser.put("email",email_login.getText().toString().trim() );

                                                                                current_user.setValue(newUser);

                                                                                // Set notifications to true for club in his database for both personal and all notifications
                                                                                DatabaseReference check_notify = FirebaseDatabase.getInstance().getReference()
                                                                                        .child("Check Notify").child(user_name).child("Clubs");
                                                                                check.put("bool", "true");
                                                                                check_notify.setValue(check);

                                                                                check_notify = FirebaseDatabase.getInstance().getReference()
                                                                                        .child("Check Notify").child(user_name).child("Both");
                                                                                check.put("bool", "true");
                                                                                check_notify.setValue(check);

                                                                                // Create dark mode node for user in database and set it to false initially
                                                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Dark Mode").
                                                                                        child(user_name);
                                                                                Map mode = new HashMap();
                                                                                mode.put("Bool","false");
                                                                                databaseReference.setValue(mode);

                                                                                // Confirmation that account ahs been created and take user to login activity
                                                                                Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_LONG).show();
                                                                                Intent login = new Intent(SignUp.this, Login_Activity.class);
                                                                                startActivity(login);
                                                                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Passcodes")
                                                                                        .child(s);
                                                                                reference.removeValue();
                                                                                break;
                                                                            }
                                                                            // If passcode is invalid give user error message
                                                                            else if (index == dataSnapshot.getChildrenCount() && !ds.getValue().toString().contains(s) ) {
                                                                                FirebaseAuth.getInstance().getCurrentUser().delete();
                                                                                Toast.makeText(getApplicationContext(),"Invalid Passcode", Toast.LENGTH_LONG).show();
                                                                            }
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                            }
                                                        })
                                                        .show();
                                            }
                                            // If club checkbox is not ticked, create a user account
                                            else{
                                                // Put email into user's database
                                                newUser.put("email",email_login.getText().toString().trim() );
                                                current_user.setValue(newUser);

                                                // Set notifications to true for user in his database for both personal and all notifications
                                                DatabaseReference check_notify = FirebaseDatabase.getInstance().getReference()
                                                        .child("Check Notify").child(user_name).child("Users");
                                                check.put("bool", "true");
                                                check_notify.setValue(check);

                                                check_notify = FirebaseDatabase.getInstance().getReference()
                                                        .child("Check Notify").child(user_name).child("Both");
                                                check.put("bool", "true");
                                                check_notify.setValue(check);


                                                // Create dark mode node for user in database and set it to false initially
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Dark Mode").
                                                        child(user_name);
                                                Map mode = new HashMap();
                                                mode.put("Bool","false");
                                                databaseReference.setValue(mode);

                                                // Confirmation that account ahs been created and take user to login activity
                                                Toast.makeText(SignUp.this, "Account Created", Toast.LENGTH_LONG).show();
                                                Intent login = new Intent(SignUp.this, Login_Activity.class);
                                                startActivity(login);
                                            }

                                            // Set username as display name in firebase
                                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(user_name).build();
                                            user.updateProfile(profileUpdates);
                                        }
                                        // If account not created then send error message
                                        else {
                                            Log.e("Error",task.getException().toString());
                                            Toast.makeText(SignUp.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
/*
 *  @author Hanzallah Burney
 */