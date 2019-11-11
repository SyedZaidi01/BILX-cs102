package hab.bilx.Fragments.Information;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;


import hab.bilx.Fragments.Information.Club_Info_FAQs.Club_Activities_FAQ;
import hab.bilx.Fragments.Information.Club_Info_FAQs.Club_Profile_FAQ;
import hab.bilx.R;


/**
 *  The settings fragment for the admin class.
 *  @author Hanzallah Burney
 */

public class Club_Information extends android.support.v4.app.Fragment {
    /*
     *  @author Hanzallah Burney
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.club_information, container, false);
        String[] infoItems = {"Activities", "Profile", "Notifications", "Settings"};
        ListView listView = (ListView) view.findViewById(R.id.club_list_info);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(R.string.Information);


        ArrayAdapter<String> listViewAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, infoItems);
        listView.setAdapter(listViewAdapter);

        /*
         *  @author Hanzallah Burney
         */
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                if (index == 0) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new Club_Activities_FAQ())
                            .addToBackStack(getClass().getName()).commit();
                } else if (index == 1) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new Club_Profile_FAQ())
                            .addToBackStack(getClass().getName()).commit();
                } else if (index == 2) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new Notifications_FAQ())
                            .addToBackStack(getClass().getName()).commit();
                } else if (index == 3) {
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.content_frame, new Settings_FAQ())
                            .addToBackStack(getClass().getName()).commit();
                }
            }
        });

        return view;
    }
}
/*
 *  @author Hanzallah Burney
 */