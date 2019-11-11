package hab.bilx.Fragments.Club;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hab.bilx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The club notifications adapter for recycler view
 * @author Hanzallah Burney
 */

public class ClubNotificationsAdapter extends RecyclerView.Adapter<ClubNotificationsAdapter.MyViewHolder> {
    private List<ClubNotificationObject> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        /*
         **  @author Hanzallah Burney
         */

        public MyViewHolder(View view) {
            super(view);
            // Initialize variables
            title = (TextView) view.findViewById(R.id.club_title);
            genre = (TextView) view.findViewById(R.id.club_genre);
            year = (TextView) view.findViewById(R.id.club_year);
        }
    }

    /*
     **  @author Hanzallah Burney
     */

    public ClubNotificationsAdapter(List<ClubNotificationObject> list) {
        this.list = list;
    }

    @Override
    public ClubNotificationsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout of the data
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_club_list, parent, false);

        return new ClubNotificationsAdapter.MyViewHolder(itemView);
    }
    /*
     **  @author Hanzallah Burney
     */

    @Override
    public void onBindViewHolder(ClubNotificationsAdapter.MyViewHolder holder, int position) {
        // Set data for a specific position
        ClubNotificationObject s = list.get(position);
        holder.title.setText(s.getSentBy());
        holder.genre.setText(s.getSubject());
        holder.year.setText(s.getContent());
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
    public void removeAdapter(int i) {
        list.remove(i);
    }
}