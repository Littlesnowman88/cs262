package edu.calvin.cs262.ajf27.hw2;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * PlayersAdapter loads the player RecyclerView with Player data
 *
 * @author Littlesnowman88
 */
public class PlayersAdapter extends RecyclerView.Adapter<PlayersAdapter.PlayersAdapterViewHolder> {

    //list of players and a flag for the dummy dark mode setting
    private ArrayList<String> players;
    private Boolean dark_mode;

    /**
     * Constructor to initialize instance variables and access shared preferences
     *
     * @param app_context the application context used for getting strings from strings.xml and getting default shared preferences
     * @author Littlesnowman88
     **/
    public PlayersAdapter(Context app_context) {

        players = new ArrayList<String>();
        //accessed through WeakReference to avoid memory leaks
        Context context = new WeakReference<Context>(app_context).get();
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        dark_mode = sharedPrefs.getBoolean("color_preference", false);
    }

    /**
     * Inner view holder that holds instances of player_item.xml
     * Although the view holder technically implements View.OnClickListener, nothing happens when the user clicks on a player text view
     *
     * @author Littlesnowman88
     */
    public class PlayersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView player_view;

        /**
         * constructor to access UI items from player_item.xml and to apply a homebrew darkmode preference
         * gives this viewholder access to a player's internal xml elements
         **/
        public PlayersAdapterViewHolder(View view) {
            super(view);
            //save access to the player text view
            player_view = (TextView) view.findViewById(R.id.player_text);
            //apply dark mode or non-dark mode
            if (!dark_mode) {
                player_view.setBackgroundColor(MainActivity.WHITE);
                player_view.setTextColor(MainActivity.BLACK);
            } else {
                player_view.setBackgroundColor(MainActivity.BLACK);
                player_view.setTextColor(MainActivity.WHITE);
            }
        }

        /**
         * implemented from View.onClickListener(), but actually does nothing.
         *
         * @param view the player textview clicked on by a user
         * @author Littlesnowman88
         */
        @Override
        public void onClick(View view) {
            //don't do anything on a click.
        }
    }


    /**
     * called when each ViewHolder in the RecycleView is created.
     *
     * @param parent:   the single view group that contains all the players
     * @param viewType: an integer indicating different types of items
     * @return a PlayersAdapterViewHolder that contains the Views for each player.
     * @author Littlesnomwan88
     */
    @NonNull
    @Override
    public PlayersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int ListItemId = R.layout.player_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        final boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(ListItemId, parent, shouldAttachToParentImmediately);
        return new PlayersAdapterViewHolder(view);
    }

    /**
     * onBindViewHolder takes data from players and places the data onto the player_UI.
     * onBindViewHolder also sets viewHolder darkmode
     * Postcondition: the player's UI textview is set with its information.
     *
     * @param player_holder the viewholder defined in this adapter class
     * @param position      the player info at which the adapter is currently looking
     *
     * @author Littlesnowman88
     */
    @Override
    public void onBindViewHolder(@NonNull PlayersAdapterViewHolder player_holder, int position) {
        String current_player_info = players.get(position);
        player_holder.player_view.setText(current_player_info);
        if (!dark_mode) {
            player_holder.player_view.setBackgroundColor(MainActivity.WHITE);
            player_holder.player_view.setTextColor(MainActivity.BLACK);
        } else {
            player_holder.player_view.setBackgroundColor(MainActivity.BLACK);
            player_holder.player_view.setTextColor(MainActivity.WHITE);
        }
    }

    /**
     * get the number of cards in the adapter's data structure
     *
     * @author littlesnowman88
     */
    @Override
    public int getItemCount() {
        if (null == players) return 0;
        return players.size();
    }

    /**
     * set the player info in the adapter's data structure
     *
     * @author Littlesnowman88
     */
    public void setPlayers(ArrayList<String> new_players) {
        players = new_players;
        notifyDataSetChanged(); //a method inside of Recycler View.
    }
}
