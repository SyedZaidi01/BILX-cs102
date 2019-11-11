package hab.bilx.Accounts;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import hab.bilx.Fragments.Admin.ApproveActivities;
import hab.bilx.Fragments.Admin.CreatePasscodes;
import hab.bilx.Fragments.Admin.SettingsFragment_admin;
import hab.bilx.Fragments.Admin.NotificationsAdmin;
import hab.bilx.Fragments.Information.Admin_Information;
import hab.bilx.Login_Activity;
import hab.bilx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

/**
 *  The admin account.
 *  @author Hanzallah Burney
 */

public class Admin_Account extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static int RESULT_LOAD_IMAGE = 1;

    private TextView navEmail;
    private TextView navUsername;
    private FirebaseAuth firebaseAuth;
    private SettingsFragment_admin settingsFragmentAdmin;
    public static int count;
    private ImageButton imageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin__account);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set the navigation drawer layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Change theme to dark
        settingsFragmentAdmin = new SettingsFragment_admin();

        /*
         *  @author Hanzallah Burney
         */

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Dark Mode")
                .child("admin");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue().toString().contains("true")){
                    // Change card views to dark theme
                    View view =  getLayoutInflater().inflate(R.layout.approve_activities_list, null);
                    CardView cardView = view.findViewById(R.id.card_tracks);
                    cardView.setCardBackgroundColor(Color.DKGRAY);
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);

                    int[][] states = new int[][] {
                            new int[] { android.R.attr.state_enabled}, // enabled
                            new int[] {-android.R.attr.state_enabled}, // disabled
                            new int[] {-android.R.attr.state_checked}, // unchecked
                            new int[] { android.R.attr.state_pressed}  // pressed
                    };

                    int[] colors = new int[] {
                            Color.WHITE,
                            Color.WHITE,
                            Color.GREEN,
                            Color.YELLOW
                    };

                    ColorStateList myList = new ColorStateList(states, colors);

                    navigationView.setItemTextColor(myList);
                    navigationView.setItemIconTintList(myList);
                }
                else{
                    getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        // If user logs in for first time then take him to home page else if he switched modes then recreate activities and take him to settings page
        if (count == 0){
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ApproveActivities()).commit();
            this.getSupportActionBar().setTitle(R.string.ApproveActivities);
            navigationView.setCheckedItem(R.id.nav_approveActivities);
        }
        else{
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragmentAdmin).commit();
            this.getSupportActionBar().setTitle(R.string.title_activity_settings);
            navigationView.setCheckedItem(R.id.nav_settings);
        }


    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.admin__account, menu);


        // Set email in navigation drawer to user's email
        firebaseAuth = FirebaseAuth.getInstance();
        navEmail = (TextView) findViewById(R.id.navBar_email);
        navEmail.setText(firebaseAuth.getCurrentUser().getEmail());

        // Set username in navigation drawer to user's username
        navUsername = (TextView) findViewById(R.id.navBar_username);
        navUsername.setText(R.string.adminName);

        // Image button in navigation drawer
        imageButton = (ImageButton) findViewById(R.id.admin_imageButton);

        // Get the profile image of user and set it if it exists
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        storageRef.child(firebaseAuth.getCurrentUser().getDisplayName() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(imageButton);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        // If image button clicked then take user to gallery and allow them to set a profile picture
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.putExtra("outputX", 400); // output size horizontal
                intent.putExtra("outputY", 400); // output size vertical
                intent.putExtra("aspectX", 1); // horizontal aspect ration set to 1
                intent.putExtra("aspectY", 1); // vertical aspect ratio set to 1
                intent.putExtra("noFaceDetection", false); // apply facial recognition
                intent.putExtra("crop", true); // allow the user to crop the image
                intent.putExtra("return-data", true); //  return the image data

                // start the change profile picture intent
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
            }
        });
        return true;
    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.nav_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    /*
     *  @author Hanzallah Burney
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Take user to respective activities from navigation drawer
        if (id == R.id.nav_approveActivities) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new ApproveActivities()).commit();
            this.getSupportActionBar().setTitle(R.string.ApproveActivities);
        } else if (id == R.id.nav_approveClubs) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new CreatePasscodes()).commit();
            this.getSupportActionBar().setTitle(R.string.CreatePasscodes);

        } else if (id == R.id.nav_adminnotifications) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new NotificationsAdmin()).commit();
            this.getSupportActionBar().setTitle(R.string.Notifications);

        } else if (id == R.id.nav_settings) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, settingsFragmentAdmin).commit();
            this.getSupportActionBar().setTitle(R.string.title_activity_settings);
        } else if (id == R.id.nav_information) {
            getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new Admin_Information()).commit();
            this.getSupportActionBar().setTitle(R.string.Information);
        } else if (id == R.id.nav_logout) {
            count = 0;
            FirebaseAuth.getInstance().signOut();
            Intent logout = new Intent(Admin_Account.this, Login_Activity.class);
            startActivity(logout);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
     *  @author Hanzallah Burney
     */
    /**
     * Uplaods profile picture to firebase storage
     * @param requestCode - the code to request change picture intent
     * @param resultCode - the return code
     * @param data - the image data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Bundle bundle = data.getExtras();
            Bitmap image = bundle.getParcelable("data");
            imageButton.setImageBitmap(image);

            FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
            StorageReference storageRef = firebaseStorage.getReferenceFromUrl("gs://cs-bilx.appspot.com");
            StorageReference profileRef = storageRef.child(firebaseAuth.getCurrentUser().getDisplayName() + ".jpg");
            StorageReference profileImagesRef = storageRef.child("images/" + firebaseAuth.getCurrentUser().getDisplayName() + ".jpg");

            profileRef.getName().equals(profileImagesRef.getName());
            profileRef.getPath().equals(profileImagesRef.getPath());

            imageButton.setDrawingCacheEnabled(true);
            imageButton.buildDrawingCache();
            Bitmap bitmap = imageButton.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[]  bytes = baos.toByteArray();

            UploadTask uploadTask = profileRef.putBytes(bytes);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                }
            });
        }
    }
}
/*
 *  @author Hanzallah Burney
 */