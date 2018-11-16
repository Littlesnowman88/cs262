package edu.calvin.cs262.ajf27.hw2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

/**
 * PlayerLoader is the AsyncTaskLoader responsible for fetching player data from NetworkUtils
 *
 * @author Littlesnowman88
 */
public class PlayerLoader extends AsyncTaskLoader<String> {

    //filter string provided by the user in MainActivity
    private String queryString;

    /**
     * Constructor: passes context to AsyncTaskLoader and saves the user's filter string
     *
     * @param context
     * @param query
     */
    public PlayerLoader(@NonNull Context context, String query) {
        super(context);
        this.queryString = query;
    }

    /**
     * Forces the AsyncTaskLoader to load
     *
     * @author Littlesnowman88
     */
    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    /**
     * Fetches data from NetworkUtils in the background so the UI thread isn't held up by data fetching
     *
     * @return player information corresponding to the user's filter string
     * @author Littlesnowman88
     */
    @Override
    public String loadInBackground() {
        return NetworkUtils.getPlayerInfo(queryString);
    }


}
