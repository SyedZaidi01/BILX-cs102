package hab.bilx;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import hab.bilx.Accounts.Admin_Account;
import hab.bilx.Accounts.Club_Account;
import hab.bilx.Accounts.User_Account;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * This class is the implements login functionality using firebase.
 * @author Hanzallah Burney
 * @version 1.00 21-03-2018
 */

public class Login_Activity extends AppCompatActivity{
    // Shared preferences final values
    private static final String PREFERENCES = "preferences";
    private static final String KEY_REMEMBER = "remember";

    // Set variables
    private String email;
    private DatabaseReference reference;
    private String user_name;
    private CheckBox remember;
    private boolean saveLogin;
    private EditText email_Login;
    private EditText password;
    private FirebaseAuth firebaseauth;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize shared preference variables
        sharedPreferences = getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();


        // Initialize variables
        remember = findViewById(R.id.checkBox);
        email_Login = findViewById(R.id.email);
        password = findViewById(R.id.password);
        firebaseauth = FirebaseAuth.getInstance();

        // If shared preferences exist then set checkbox to true else to false
        if (sharedPreferences.getBoolean(KEY_REMEMBER,false)){
            remember.setChecked(true);
        }
        else {
            remember.setChecked(false);
        }
        /*
         *  @author Hanzallah Burney
         */
        // If shared preferences exist, set the fields to the preferences and if they don't then set them empty and set checkbox to true
        saveLogin = sharedPreferences.getBoolean("saveLogin", false);
        if (saveLogin) {
            email_Login.setText(sharedPreferences.getString("Email", ""));
            password.setText(sharedPreferences.getString("password", ""));
            remember.setChecked(true);
        }
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Implements functionality of login button
     * @param view - the button view
     */
    public void login_button_click(View view){
        // Creates Progress dialog box
        final ProgressDialog progressDialog = ProgressDialog.show(Login_Activity.this,
                "Please Wait", "Processing",true);


        // If any field is empty show toast
        if  (email_Login.getText().toString().equals("") || password.getText().toString().equals("")){
            progressDialog.dismiss();
            Toast.makeText(Login_Activity.this,"Either Field Is Empty",Toast.LENGTH_LONG).show();
        }
        // If username entered
        else if (!email_Login.getText().toString().contains("@")){
            user_name = email_Login.getText().toString().trim();
            // Get the users node from database
            reference = FirebaseDatabase.getInstance().getReference().child("Users");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // If username does not exist then print error message
                    if (!dataSnapshot.hasChild(user_name)){
                        progressDialog.dismiss(); // removes the progress dialog
                        Toast.makeText(Login_Activity.this,"The username is incorrect or does not exist",Toast.LENGTH_LONG).show();
                    }
                    // Log the user into the app
                    else{
                        progressDialog.dismiss();
                        // Extract email and login with the email and password
                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_name);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                // Extract email corresponding to username
                                email = dataSnapshot.getValue().toString().substring(7, dataSnapshot.getValue().toString().length() - 1);
                                // Login the admin
                                if (user_name.equals("admin")){
                                    Adminlogin(email,password);
                                }
                                // Login the club and get its email
                                else if (dataSnapshot.hasChild("club")){
                                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_name).child("club");
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            email = dataSnapshot.getValue().toString().substring(7, dataSnapshot.getValue().toString().length() - 1);
                                            Clublogin(email, password);
                                            }
                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {
                                            }
                                    });
                                }
                                // Login the suer
                                else {
                                    Userlogin(email, password);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                       }
                   }
                   @Override
                   public void onCancelled(DatabaseError databaseError) {}
               });
        }
        // If account email entered login with it directly
        else{
            email = email_Login.getText().toString().trim();
            reference = FirebaseDatabase.getInstance().getReference().child("Users");
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    long count = 0;

                    // Login admin
                    if (email.trim().equals("azim.burney@ug.bilkent.edu.tr")){
                        Adminlogin(email, password);
                    }
                    // If email is not of admin
                    else if (dataSnapshot.exists()){
                        progressDialog.dismiss();
                        // Check email under every username ad club child to see if it exists else print error
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String s = snapshot.child("email").toString().substring(snapshot.child("email").toString().lastIndexOf("=")+1,snapshot.child("email").toString().lastIndexOf("}"));
                            if (s.trim().equals(email)){
                                Userlogin(email,password);
                                break;
                            }
                            else if (snapshot.hasChild("club")){
                                s = snapshot.child("club").child("email").toString().substring(snapshot.child("club").child("email").toString().lastIndexOf("=")+1,snapshot.child("club").child("email").toString().lastIndexOf("}"));

                                if (s.trim().equals(email)) {
                                    Clublogin(email, password);
                                    break;
                                }
                                else{
                                    count++;
                                    if (count == dataSnapshot.getChildrenCount()-1){
                                        progressDialog.dismiss();
                                        Toast.makeText(Login_Activity.this,"The email is incorrect or does not exist",Toast.LENGTH_LONG).show();
                                        break;
                                    }
                                }
                            }
                            else{
                                count++;
                                if (count == dataSnapshot.getChildrenCount()-1){
                                    progressDialog.dismiss();
                                    Toast.makeText(Login_Activity.this,"The email is incorrect or does not exist",Toast.LENGTH_LONG).show();
                                    break;
                                }
                            }
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
    /**
     * Add fields if remember me is checked to shared preferences else clears shared preferences
     */
    private void managePreferences(){
        if (remember.isChecked()) {
            editor.putBoolean("saveLogin", true);
            editor.putString("Email", email_Login.getText().toString());
            editor.putString("password", password.getText().toString());
            editor.commit();
        } else {
            editor.clear();
            editor.commit();
        }
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Implements the functionality of the signup text
     * @param view - the button view
     */
    public void signup_click(View view){
        // Create a signup intent and start signup activity
        Intent signup = new Intent(Login_Activity.this,SignUp.class);
        startActivity(signup);

        // Override default animation with this one
        overridePendingTransition(R.anim.slidein_right,R.anim.slideout_left);
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Implements the functionality of the forgot password text
     * @param view - the button view
     */
    public void forgotPassword_click(View view) {
        firebaseauth = FirebaseAuth.getInstance();

        // User email address
        user_name = email_Login.getText().toString().trim();

        // If email address field is empty show toast
        if (user_name.equals("")) {
            Toast.makeText(Login_Activity.this, "Enter Email", Toast.LENGTH_SHORT).show();
        }
        // If username entered
        else if (!email_Login.getText().toString().contains("@")) {
            // Get the users node from database
            reference = FirebaseDatabase.getInstance().getReference().child("Users");

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // If username does not exist then send error message to user
                    if (!dataSnapshot.hasChild(user_name)) {
                        Toast.makeText(Login_Activity.this, "The username is incorrect or does not exist", Toast.LENGTH_LONG).show();
                    }
                    // get the email and send the password reset link to that email
                    else {
                        reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_name);
                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.hasChild("club")){
                                    reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user_name).child("club");
                                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            email = dataSnapshot.getValue().toString().substring(7, dataSnapshot.getValue().toString().length() - 1);
                                            setPassword(email);
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {
                                        }
                                    });
                                }
                                else{
                                    // Extract email corresponding to username
                                    email = dataSnapshot.getValue().toString().substring(7, dataSnapshot.getValue().toString().length() - 1).trim();
                                    // Send Reset Email
                                    setPassword(email);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError databaseError) {}
                        });
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) { }
            });
        }
        // Send email to user regarding password reset if user account exists on database else show toast
        else {
            setPassword(user_name);
        }
    }

    /*
     *  @author Hanzallah Burney
     */
    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        super.onBackPressed();
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Login method for the user
     * @param email - the user email
     * @param password - the user password
     */
    public void Userlogin(String email,EditText password){
        // Creates Progress dialog box
        final ProgressDialog progressDialog = ProgressDialog.show(Login_Activity.this,
                "Please Wait", "Processing",true);
        (firebaseauth.signInWithEmailAndPassword(email,password.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(Login_Activity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            managePreferences();
                            Intent user_account = new Intent(Login_Activity.this, User_Account.class);
                            startActivity(user_account);
                        }
                        else {
                            Log.e("Error",task.getException().toString());
                            Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Login method for the admin
     * @param email - the admin email
     * @param password - the admin password
     */
    public void Adminlogin(String email,EditText password){
        // Creates Progress dialog box
        final ProgressDialog progressDialog = ProgressDialog.show(Login_Activity.this,
                "Please Wait", "Processing",true);
        (firebaseauth.signInWithEmailAndPassword(email,password.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(Login_Activity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            managePreferences();
                            Intent admin_account = new Intent(Login_Activity.this, Admin_Account.class);
                            startActivity(admin_account);
                        }
                        else {
                            Log.e("Error",task.getException().toString());
                            Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Login method for the club
     * @param email - the club email
     * @param password - the club password
     */
    public void Clublogin(String email,EditText password){
        // Creates Progress dialog box
        final ProgressDialog progressDialog = ProgressDialog.show(Login_Activity.this,
                "Please Wait", "Processing",true);
        (firebaseauth.signInWithEmailAndPassword(email,password.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(Login_Activity.this, "Login Successful", Toast.LENGTH_LONG).show();
                            managePreferences();
                            Intent club_account = new Intent(Login_Activity.this, Club_Account.class);
                            startActivity(club_account);
                        }
                        else {
                            Log.e("Error",task.getException().toString());
                            Toast.makeText(Login_Activity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    /*
     *  @author Hanzallah Burney
     */
    /**
     * Allows user to reset password
     * @param email - the email where password reset link is to be sent
     */
    public void setPassword(String email){
        firebaseauth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Snackbar.make(getCurrentFocus(), "Email sent", Snackbar.LENGTH_LONG).show();
                        }
                        else{
                            Snackbar.make(getCurrentFocus(), "Enter valid email", Snackbar.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
/*
 *  @author Hanzallah Burney
 */