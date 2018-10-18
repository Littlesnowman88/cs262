package edu.calvin.cs262.ajf27.hw2;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();
    private static final String PLAYER_BASE_URL = "https://calvincs262-monopoly.appspot.com/monopoly/v1/";

    static String getPlayerInfo(String queryString) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String playerJSONString = null;
        try {
            //Build the URL
            Uri builtURI = Uri.parse(PLAYER_BASE_URL).buildUpon()
                    .appendEncodedPath(queryString)
                    .build();
            URL requestURL = new URL(builtURI.toString());

            //establish url connection and make a GET request
            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //read the response and turn it into a string
            InputStream iStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (iStream == null) {
                //no input stream came back, how sad
                throw new RuntimeException("ERROR: connection failed; no input stream could be created.");
            }

            //build the stream reader/parser, putting newlines into the text for debugging aid
            reader = new BufferedReader(new InputStreamReader(iStream));
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n"); //Only and REALLY helpful for debugging
            }
            if (buffer.length() == 0) {
                // input stream was empty, so return null and don't bother parsing.
                throw new RuntimeException("ERROR: No player found for that ID.");
            }

            playerJSONString = buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return e.getLocalizedMessage();

        } finally {
            if (urlConnection != null) {
                //we don't want connections lingering around. NO LOITERING.
                urlConnection.disconnect();
            }
            //now, if the input stream was opened
            if (reader != null) {
                try {
                    //clean up the reader, too.
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(LOG_TAG, (playerJSONString!=null)?playerJSONString:"JSON STRING IS NULL, BADNESS");
        return playerJSONString;
    }
}
