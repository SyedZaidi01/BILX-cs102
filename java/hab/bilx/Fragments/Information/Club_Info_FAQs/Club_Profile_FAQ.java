package hab.bilx.Fragments.Information.Club_Info_FAQs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hab.bilx.R;
/*
 *  @author Hanzallah Burney
 */
public class Club_Profile_FAQ extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Profile FAQs");
        return inflater.inflate(R.layout.club_profile_faq, container, false);
    }
}
