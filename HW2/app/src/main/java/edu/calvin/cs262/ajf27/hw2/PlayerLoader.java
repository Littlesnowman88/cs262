package edu.calvin.cs262.ajf27.hw2;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

public class PlayerLoader extends AsyncTaskLoader<String> {

    private String queryString;

    public PlayerLoader(@NonNull Context context, String query) {
        super(context);
        this.queryString = query;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();

    }

    @Override
    public String loadInBackground() {
        return NetworkUtils.getPlayerInfo(queryString);
    }


}
