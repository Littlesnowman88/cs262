package edu.calvin.cs262.ajf27.hw2;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.preference.PreferenceScreen;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.preference.RingtonePreference;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * MainActivity provides a User interface for a user to look up player data from NetworkUtils
 * Implements LoaderManager.LoaderCallbacks<String> to handle data fetching
 *
 * @author Littlesnowman88
 */
public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    //UI elements
    private LinearLayout rootLayout;
    private EditText inputTextField;
    private Button fetchButton;
    private RecyclerView player_container;
    //application context for accessing strings.xml and displaying toasts
    private Context context;
    //list of players
    private ArrayList<String> player_data;
    //data provider for Recycler View container
    private PlayersAdapter player_adapter;
    //flag for shared preference
    private Boolean dark_mode;

    //integers used in darkMode and normal mode
    public static final int WHITE = Color.parseColor("#ffffff");
    public static final int BLACK = Color.parseColor("#333333");
    public static final int DARKGRAY = Color.parseColor("#666666");
    public static final int LIGHTGRAY = Color.parseColor("#bbbbbb");
    public static final String KEY_PREF_NOTIF_SETTING = "notification_preference";

    /**
     * called when MainActivity is started.
     * Accesses UI, initializes important instance variables, and applies dark/non-dark mode
     *
     * @param savedInstanceState
     * @author Littlesnowman88
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //establish UI framework
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //access UI
        rootLayout = findViewById(R.id.root_layout);
        inputTextField = findViewById(R.id.fetch_text);
        fetchButton = findViewById(R.id.fetch_button);

        //establish app context
        context = getApplicationContext();
        //build the Loader responsible for fetching player data
        if (getSupportLoaderManager().getLoader(0) != null) {
            getSupportLoaderManager().initLoader(0, null, this);
        }

        //access the UI container for player data and build+set its adapter
        player_container = (RecyclerView) findViewById(R.id.player_text_container);
        player_container.setLayoutManager(new LinearLayoutManager(this));
        if (null == player_data) player_data = new ArrayList<String>();
        player_adapter = new PlayersAdapter(context);
        player_adapter.setPlayers(player_data);
        player_container.setAdapter(player_adapter);

        //access shared preferences for dark mode
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        dark_mode = sharedPrefs.getBoolean("color_preference", false);
        //apply dark mode
        if (!dark_mode) {
            rootLayout.setBackgroundColor(WHITE);
            inputTextField.setBackgroundColor(WHITE);
            fetchButton.setBackgroundColor(LIGHTGRAY);
            player_container.setBackgroundColor(WHITE);
            inputTextField.setHintTextColor(LIGHTGRAY);
            inputTextField.setTextColor(BLACK);
            fetchButton.setTextColor(BLACK);
        } else {
            rootLayout.setBackgroundColor(BLACK);
            inputTextField.setBackgroundColor(BLACK);
            fetchButton.setBackgroundColor(DARKGRAY);
            player_container.setBackgroundColor(BLACK);
            inputTextField.setHintTextColor(DARKGRAY);
            inputTextField.setTextColor(WHITE);
            fetchButton.setTextColor(WHITE);
        }

    }

    /**
     * Creates a menu based on menu_main.xml, which has a settings option
     *
     * @param menu the menu to be inflated (rendered, essentially)
     * @return true, signifying that the  menu has been created
     * @author Littlesnowman88
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    /**
     * handles menu item clicks--most notably the settings icon
     *
     * @param item the user-selected item
     * @return true, if settings was clicked
     * whatever AppCompatActivity returns, if something unknown in the menu is clicked.
     * @author Littlesnowman88
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //if settings is clicked, start the settings activity
            case R.id.action_settings:
                Intent main_to_settings = new Intent(this, SettingsActivity.class);
                startActivity(main_to_settings);
                return true;
            //if something unknown is clicked, say so in a Toast
            default:
                Toast.makeText(context, "ERROR: Unknown menu item clicked.", Toast.LENGTH_SHORT).show();
        }
        //returns only if settings isn't clicked
        return super.onOptionsItemSelected(item);
    }

    /**
     * fetchPlayer grabs the user's filter text and tells the loader to fetch data
     *
     * @param view, the fetch button clicked by the user
     * @author Littlesnowman88
     */
    public void fetchPlayer(View view) {
        //hides keyboard upon clicking fetch button?
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        //get the user's filter string, format it, and place it in a bundle that the Loader receives
        String queryString = inputTextField.getText().toString();
        if (queryString.equals("")) queryString = "players";
        else queryString = "player/" + queryString + "/";
        Bundle queryBundle = new Bundle();
        queryBundle.putString("queryString", queryString);
        getSupportLoaderManager().restartLoader(0, queryBundle, this);
    }

    /**
     * Part of interface LoaderManager.LoaderCallbacks<String>
     * onCreateLoader is called when a Loader is restarted (see: fetchPlayer)
     * creates a new PlayerLoader that fetches data based on the query string
     *
     * @param i,      the loader id?
     * @param bundle, the bundle containing the user's filter string (see: fetchPlayer)
     * @return the newly constructed PlayerLoader
     * @author Littlesnowman88
     */
    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new PlayerLoader(this, bundle.getString("queryString"));
    }

    /**
     * Part of interface LoaderManager.LoaderCallbacks<String>
     * onLoadFinished is called when PlayerLoader has finished fetching data from NetworkUtils
     *
     * @param loader, the PlayerLoader built by onCreateLoader and fetchPlayer
     * @param s,      the JSON or error returned by the PlayerLoader
     * @author Littlesnowman88
     */
    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        //reset the list of players because a new list has come in
        player_data.clear();
        //if an error was thrown from my new runtime exceptions, print those in a toast
        if (s.startsWith(context.getString(R.string.error))) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            return;
        }
        //try parsing a JSON from the passed in string
        try {
            JSONObject jsonObject = new JSONObject(s);
            String playerInfo;
            try {
                //try to create an array of player entries
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int num_players = itemsArray.length();
                for (int i = 0; i < num_players; i++) {
                    //fill the player entries array with players
                    JSONObject player = itemsArray.getJSONObject(i); //get the current player
                    playerInfo = parseJSONPlayer(player); //parse the data
                    player_data.add(playerInfo); //add the data
                }
            }
            //error: the player array couldn't be found or created, or a player object couldn't be made
            catch (Exception e) {
                //here, the jsonObject is assumed to be a list of player items (in an array), so we'll just show the whole thing.
                playerInfo = parseJSONPlayer(jsonObject);
                player_data.add(playerInfo);
            } finally {
                //update the recyclerview with player info
                player_adapter.setPlayers(player_data);
            }

        }
        //error: a proper JSON object could not be parsed from s
        catch (Exception e) {
            Toast.makeText(context, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * helper function built to create a string representation of a JSON player object
     *
     * @param player a JSONObject representation of a player
     * @return the string representation of a player, in format of id + name + email
     */
    private String parseJSONPlayer(JSONObject player) {
        //declare string aggregate parts and set default values when appropriate
        int id;
        String name;
        String email = null;
        String id_text = null;
        String name_text = null;
        //get any existing player id
        try {
            id = player.getInt("id");
            id_text = Integer.toString(id) + ", ";
        } catch (Exception e) {
        }
        //get any existing player email address
        try {
            email = player.getString("emailAddress");
        } catch (Exception e) {
        }
        //get an existing player name
        try {
            name = player.getString("name");
            name_text = name + ", ";
        }
        //if failure, log the failure and set the name to the empty string
        catch (org.json.JSONException jse) {
            Log.d("JSON PARSING: ", "Player with id " + id_text + " and email" + email + "has no name.");
            name_text = "";
        }

        //text to be shown for a player
        return (id_text + name_text + email);
    }

    /**
     * Part of interface LoaderManager.LoaderCallbacks<String>
     * ...I think this is required by the above interface, but I'm surprised I don't do anything... sorry.
     *
     * @param loader the loader to be reset
     */
    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
