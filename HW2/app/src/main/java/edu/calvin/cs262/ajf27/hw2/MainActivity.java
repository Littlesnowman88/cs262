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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String>{

    private LinearLayout rootLayout;
    private EditText inputTextField;
    private Button fetchButton;
    private RecyclerView player_container;
    private Context context;
    private ArrayList<String> player_data;
    private PlayersAdapter player_adapter;
    private Boolean dark_mode;

    public static final int WHITE = Color.parseColor("#ffffff");
    public static final int BLACK = Color.parseColor("#333333");
    public static final int DARKGRAY = Color.parseColor("#666666");
    public static final int LIGHTGRAY = Color.parseColor("#bbbbbb");
    public static final String KEY_PREF_NOTIF_SETTING = "notification_preference";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootLayout = findViewById(R.id.root_layout);
        inputTextField = findViewById(R.id.fetch_text);
        fetchButton = findViewById(R.id.fetch_button);

        context = getApplicationContext();
        if (getSupportLoaderManager().getLoader(0)!=null){
            getSupportLoaderManager().initLoader(0, null, this);
        }

        player_container = (RecyclerView) findViewById(R.id.player_text_container);
        player_container.setLayoutManager(new LinearLayoutManager(this));
        if (null == player_data) player_data = new ArrayList<String>();
        player_adapter = new PlayersAdapter(context);
        player_adapter.setPlayers(player_data);
        player_container.setAdapter(player_adapter);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        dark_mode = sharedPrefs.getBoolean("color_preference", false);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_settings:
                Intent main_to_settings = new Intent(this, SettingsActivity.class);
                startActivity(main_to_settings);
                return true;
            default:
                Toast.makeText(context, "ERROR: Unknown menu item clicked.", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchPlayer(View view) {
        InputMethodManager inputManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

        String queryString = inputTextField.getText().toString();
        if (queryString.equals("")) queryString="players";
        else queryString = "player/" + queryString + "/";
        //new FetchPlayer(playerText, context).execute(queryString);
        Bundle queryBundle = new Bundle();
        queryBundle.putString("queryString", queryString);
        getSupportLoaderManager().restartLoader(0, queryBundle, this);
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new PlayerLoader(this, bundle.getString("queryString"));
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String s) {
        player_data.clear();
        //if an error was thrown from my new runtime exceptions, print those in a toast
        if (s.startsWith(context.getString(R.string.error))) {
            Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            JSONObject jsonObject = new JSONObject(s);
            String playerInfo;
            try{
                JSONArray itemsArray = jsonObject.getJSONArray("items");
                int num_players = itemsArray.length();
                for(int i=0; i < num_players; i++) {
                    JSONObject player = itemsArray.getJSONObject(i); //get the current player
                    playerInfo = parseJSONPlayer(player); //parse the data
                    player_data.add(playerInfo);
                }
            } catch (Exception e) {
                playerInfo = parseJSONPlayer(jsonObject);
                player_data.add(playerInfo);
            } finally {
                player_adapter.setPlayers(player_data);
            }

        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
        }
    }

    private String parseJSONPlayer(JSONObject player) {
        int id;
        String name;
        String email = null;
        String id_text = null;
        String name_text = null;
        try {
            id = player.getInt("id");
            id_text = Integer.toString(id) + ", ";}
        catch(Exception e) {}
        try { email = player.getString("emailAddress"); }
        catch(Exception e) {}
        try {
            name = player.getString("name");
            name_text = name + ", ";
        }
        catch(org.json.JSONException jse) {
            Log.d("JSON PARSING: ", "Player with id " + id_text + " and email" + email + "has no name.");
            name_text = "";
        }

        //text to be shown for a player
        return (id_text + name_text + email);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
