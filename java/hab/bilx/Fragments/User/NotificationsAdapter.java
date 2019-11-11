package hab.bilx.Fragments.User;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hab.bilx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * The user notifications adapter for recycler view
 * @author Hanzallah Burney
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.MyViewHolder> {
    private List<UserNotificationObject> list;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        /*
         *  @author Hanzallah Burney
         */
        public MyViewHolder(View view) {
            super(view);
            // Initialize variables
            title = (TextView) view.findViewById(R.id.title2);
            genre = (TextView) view.findViewById(R.id.genre);
            year = (TextView) view.findViewById(R.id.year);
        }
    }


    public NotificationsAdapter(List<UserNotificationObject> list) {
        this.list = list;
    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout of the data
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notification_user_list, parent, false);

        return new MyViewHolder(itemView);
    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Set data for a specific position
        UserNotificationObject s = list.get(position);
        holder.title.setText(s.getSentBy());
        holder.genre.setText(s.getSubject());
        holder.year.setText(s.getContent());
    }
    /*
     *  @author Hanzallah Burney
     */
    // get total items
    @Override
    public int getItemCount() {
        return list.size();
    }

    // remove item from a specific index
    public void removeAdapter(int i) {
        list.remove(i);
    }

}
/*
 *  @author Hanzallah Burney
 */
