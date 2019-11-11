package hab.bilx.Fragments.Club;

import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import hab.bilx.Accounts.Club_Account;
import hab.bilx.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 *  The club profile fragment for the club class.
 *  @author Hanzallah Burney
 *  Contributors: Abdul Hamid
 */

public class ClubProfile extends android.support.v4.app.Fragment {

    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private FloatingActionButton fab;
    private FirebaseAuth firebaseAuth;

    /*
     **  @author Hanzallah Burney
     */

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view =  inflater.inflate(R.layout.club_profile, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        final EditText clubDescriptionEditText = (EditText) view.findViewById(R.id.clubDescription_text);

        imageView = (ImageView) view.findViewById(R.id.club_imageView);

        // Retrieve profile image from firebase storage
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        storageRef.child(firebaseAuth.getCurrentUser().getDisplayName() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri.toString()).into(imageView);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });

        /*
         **  @author Hanzallah Burney
         */

        // Set the entered club profile description to the dataabse under the club's name when the send button is pressed
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Club profiles")
                        .child(firebaseAuth.getCurrentUser().getDisplayName()+"");
                Map profile = new HashMap();
                profile.put("Profile", clubDescriptionEditText.getText()+"");
                reference.setValue(profile);

                reference.setPriority((new Date()).getTime());

                Snackbar.make(getActivity().findViewById(R.id.club_profile), "Profile Updated", Snackbar.LENGTH_LONG).show();
            }
        });

        return view;
    }
}
/*
 **  @author Hanzallah Burney
 */
