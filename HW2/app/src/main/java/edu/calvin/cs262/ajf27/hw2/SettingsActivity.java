package edu.calvin.cs262.ajf27.hw2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * SettingsActivity creates a screen for viewing settings and holds the SettingsFragment screen
 *
 * @author Littlesnowman88
 */
public class SettingsActivity extends AppCompatActivity {

    /**
     * Called when the activity is created; creates SettingsFragment and displays that fragment
     *
     * @param savedInstanceState the last known state of SettingsActivity
     * @author Littlesnowman88
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
}
