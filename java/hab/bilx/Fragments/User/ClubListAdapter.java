package hab.bilx.Fragments.User;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hab.bilx.R;
import com.squareup.picasso.Picasso;
import java.util.List;

/**
 * The club list adapter for user for recycler view
 * @author Hanzallah Burney
 */

public class ClubListAdapter extends RecyclerView.Adapter<ClubListAdapter.MyViewHolder> {

    private List<ClubListObject> list;


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView icon;
        public TextView content;
        /*
         *  @author Hanzallah Burney
         */
        public MyViewHolder(View view) {
            super(view);
            // Initialize variables
            title = (TextView) view.findViewById(R.id.clubTitle);
            icon = (ImageView) view.findViewById(R.id.clubIcon);
            content = (TextView) view.findViewById(R.id.content);
        }
    }

    /*
     *  @author Hanzallah Burney
     */
    public ClubListAdapter(List<ClubListObject> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Inflate the layout of the data
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_club_list, parent, false);

        return new MyViewHolder(itemView);
    }
    /*
     *  @author Hanzallah Burney
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // Set data for a specific position
        ClubListObject s = list.get(position);
        holder.title.setText(s.getSentBy());
        holder.content.setText(s.getSubject());
        Picasso.get().load(s.getClubIcon()).into(holder.icon);



    }
    /*
     *  @author Hanzallah Burney
     */
    // get total items
    @Override
    public int getItemCount() {
        return list.size();
    }
}
/*
 *  @author Hanzallah Burney
 */