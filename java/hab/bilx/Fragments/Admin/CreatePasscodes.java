package hab.bilx.Fragments.Admin;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import hab.bilx.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 *  The create passcodes fragment for the admin class.
 *  @author Hanzallah Burney
 */

public class CreatePasscodes extends android.support.v4.app.Fragment {
    private Button generate_code;
    private EditText club_email;
    private TextView show_passcode;
    private FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.create_passcodes, container, false);
        Random rand = new Random();
        /*
         *  @author Hanzallah Burney
         */
        club_email = (EditText) view.findViewById(R.id.club_email_enter);
        generate_code = (Button) view.findViewById(R.id.generate_passcode);
        show_passcode = (TextView) view.findViewById(R.id.show_passcode);
        final String s = rand.nextInt(1000)+"";
        fab = (FloatingActionButton) view.findViewById(R.id.passcode_fab);

        // set fab to false until a passcode is generated
        fab.setEnabled(false);

        /*
         *  @author Hanzallah Burney
         */
        // generate passcode and store in database
        generate_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fab.setEnabled(true);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Passcodes").child(s);
                databaseReference.setValue(s);
                show_passcode.setText(s);
            }
        });

        // Send email to club informing them of their passcode
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = club_email.getText().toString().trim();

                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_SUBJECT, "BILX Passcode"); // email subject
                intent.putExtra(Intent.EXTRA_TEXT, "Your Passcode is: " + s); // email body
                intent.setData(Uri.fromParts("mailto",
                        email+"", null)); // or just "mailto:" for blank
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // this will make such that when user returns to your app, your app is displayed, instead of the email app.
                startActivity(intent);
            }
        });

        return view;
    }
}
/*
 *  @author Hanzallah Burney
 */
