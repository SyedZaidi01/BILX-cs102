package hab.bilx.Fragments.Admin;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hab.bilx.R;

import java.util.List;

/**
 * The approve activities adapter for recycler view
 * @author Hanzallah Burney
 */
public class ApproveActivitiesAdapter extends RecyclerView.Adapter<ApproveActivitiesAdapter.MyViewHolder> {
    private List<ApproveActivitiesObject> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView activityName, ge, time,date, location, language, description, clubName;
        /*
         *  @author Hanzallah Burney
         */
        public MyViewHolder(View view) {
            super(view);
            // Initialize variables
            activityName = (TextView) view.findViewById(R.id.approve_actName);
            ge = (TextView) view.findViewById(R.id.approve_ge);
            time = (TextView) view.findViewById(R.id.approve_time);
            date = (TextView) view.findViewById(R.id.approve_date);
            location = (TextView) view.findViewById(R.id.approve_loc);
            language = (TextView) view.findViewById(R.id.approve_lang);
            description = (TextView) view.findViewById(R.id.approve_desc);
            clubName = (TextView) view.findViewById(R.id.approve_clubName);
        }
    }

    /*
     *  @author Hanzallah Burney
     */
    public ApproveActivitiesAdapter(List<ApproveActivitiesObject> list) {
        this.list = list;
    }

    @Override
    public ApproveActivitiesAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout of the data
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.approve_activities_list, parent, false);

        return new ApproveActivitiesAdapter.MyViewHolder(itemView);
    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public void onBindViewHolder(ApproveActivitiesAdapter.MyViewHolder holder, int position) {
        // Set data for a specific position
        ApproveActivitiesObject s = list.get(position);
        holder.activityName.setText(s.getActivityName());
        holder.ge.setText(s.getGe());
        holder.time.setText(s.getTime());
        holder.date.setText(s.getDate());
        holder.location.setText(s.getLocation());
        holder.language.setText(s.getLanguage());
        holder.description.setText(s.getDescription());
        holder.clubName.setText(s.getClubName());
    }
    /*
     *  @author Hanzallah Burney
     */
    // get total items
    @Override
    public int getItemCount() {
        return list.size();
    }
    /*
     *  @author Hanzallah Burney
     */
    // remove item from a specific index
    public void removeAdapter(int i){
        list.remove(i);
    }
}
