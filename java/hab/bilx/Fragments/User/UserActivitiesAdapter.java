package hab.bilx.Fragments.User;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hab.bilx.R;

import java.util.List;

/**
 * The user activities adapter for recycler view
 * @author Hanzallah Burney
 */

public class UserActivitiesAdapter extends RecyclerView.Adapter<UserActivitiesAdapter.MyViewHolder> {
    private List<UserActivitiesObject> list;
    /*
     **  @author Hanzallah Burney
     */

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName, clubName, ge, time,date, location, language, description;

        public MyViewHolder(View view) {
            super(view);
            // Initialize variables
            activityName = (TextView) view.findViewById(R.id.activity_name_user);
            clubName = (TextView)view.findViewById(R.id.club_name_user);
            ge = (TextView) view.findViewById(R.id.ge_point_user);
            time = (TextView) view.findViewById(R.id.time_user);
            date = (TextView) view.findViewById(R.id.date_user);
            location = (TextView) view.findViewById(R.id.location_user);
            language = (TextView) view.findViewById(R.id.language_user);
            description = (TextView) view.findViewById(R.id.description_user);
        }
    }
    /*
     **  @author Hanzallah Burney
     */


    public UserActivitiesAdapter(List<UserActivitiesObject> list) {
        this.list = list;
    }

    @Override
    public UserActivitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout of the data
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_activities_list, parent, false);

        return new UserActivitiesAdapter.MyViewHolder(itemView);
    }
    /*
     **  @author Hanzallah Burney
     */

    @Override
    public void onBindViewHolder(UserActivitiesAdapter.MyViewHolder holder, int position) {
        // Set data for a specific position
        UserActivitiesObject s = list.get(position);
        holder.activityName.setText(s.getActivityName());
        holder.clubName.setText(s.getClubName());
        holder.ge.setText(s.getGe());
        holder.time.setText(s.getTime());
        holder.date.setText(s.getDate());
        holder.location.setText(s.getLocation());
        holder.language.setText(s.getLanguage());
        holder.description.setText(s.getDescription());
    }
    /*
     **  @author Hanzallah Burney
     */

    // get total items
    @Override
    public int getItemCount() {
        return list.size();
    }
    /*
     **  @author Hanzallah Burney
     */

    // remove item from a specific index
    public void removeAdapter(int i){
        list.remove(i);
    }
}
/*
 **  @author Hanzallah Burney
 */
