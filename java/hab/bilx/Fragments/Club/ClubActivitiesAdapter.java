package hab.bilx.Fragments.Club;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hab.bilx.R;

import java.util.List;

/**
 * The club activities adapter for recycler view
 * @author Hanzallah Burney
 */

public class ClubActivitiesAdapter extends RecyclerView.Adapter<ClubActivitiesAdapter.MyViewHolder> {
    private List<ClubActivitiesObject> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName, ge, time,date, location, language, description, status;
        /*
         **  @author Hanzallah Burney
         */

        public MyViewHolder(View view) {
            super(view);
            // Initialize variables
            activityName = (TextView) view.findViewById(R.id.activity_name_text);
            ge = (TextView) view.findViewById(R.id.ge_point_text);
            time = (TextView) view.findViewById(R.id.time_text);
            date = (TextView) view.findViewById(R.id.date_text);
            location = (TextView) view.findViewById(R.id.location_text);
            language = (TextView) view.findViewById(R.id.language_text);
            description = (TextView) view.findViewById(R.id.description_text);
            status = (TextView) view.findViewById(R.id.status_text);
        }
    }

    /*
     **  @author Hanzallah Burney
     */

    public ClubActivitiesAdapter(List<ClubActivitiesObject> list) {
        this.list = list;
    }

    @Override
    public ClubActivitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout of the data
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.club_activities_list, parent, false);

        return new ClubActivitiesAdapter.MyViewHolder(itemView);
    }
    /*
     **  @author Hanzallah Burney
     */

    @Override
    public void onBindViewHolder(ClubActivitiesAdapter.MyViewHolder holder, int position) {
        // Set data for a specific position
        ClubActivitiesObject s = list.get(position);
        holder.activityName.setText(s.getActivityName());
        holder.ge.setText(s.getGe());
        holder.time.setText(s.getTime());
        holder.date.setText(s.getDate());
        holder.location.setText(s.getLocation());
        holder.language.setText(s.getLanguage());
        holder.description.setText(s.getDescription());
        holder.status.setText(s.getStatus());
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
