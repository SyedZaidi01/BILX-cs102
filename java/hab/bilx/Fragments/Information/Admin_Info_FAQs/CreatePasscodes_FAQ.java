package hab.bilx.Fragments.Information.Admin_Info_FAQs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hab.bilx.R;
/**
 * Create passcode class faqs
 *  @author Hanzallah Burney
 */
public class CreatePasscodes_FAQ extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle("Create Passcodes FAQs");
        return inflater.inflate(R.layout.create_passcodes_faq, container, false);
    }
}
/*
 *  @author Hanzallah Burney
 */