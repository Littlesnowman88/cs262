package ajf27.cs262.calvin.edu.hw1calculator;

import android.content.Context;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    /**
     * Main Activity displays a user interface for a simple calculator and provides basic calculator functionality
     *
     * @author Littlesnowman88
     */

    private Double val1, val2; //user-input values
    private String selected_operator; //for the calculation
    private String[] operators; //array of possible operations
    private EditText input_box_1, input_box_2; //UI
    private ArrayAdapter<CharSequence> operator_adapter; //gives the spinner clickable items
    private TextView results_view; //answer tet box
    private Context context; //useful for accessing strings and string arrays


    /**
     * onCreate is responsible for loading the UI and preparing the UI's functionality
     *
     * @param savedInstanceState the activity's last known state, if not destroyed iin the background
     * @author Littlesnomwan88
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        //load possible operators
        operators = getResources().getStringArray(R.array.operator_array);

        //load appropriate starting values for val1 and val2
        val1 = null;
        val2 = null;
        //load an appropriate default starting value for the operator
        try {
            selected_operator = operators[0];
        } catch (Exception e) {
            e.printStackTrace();
            selected_operator = "";

        }

        //access UI elements
        input_box_1 = (EditText) findViewById(R.id.value_1_text_box);
        input_box_2 = (EditText) findViewById(R.id.value_2_text_box);
        Spinner operator_box = (Spinner) findViewById(R.id.operator_container);
        results_view = (TextView) findViewById(R.id.results_text_view);

        if (operator_adapter == null) {
            //build operator_box's adapter
            operator_adapter = ArrayAdapter.createFromResource(context,
                    R.array.operator_array, R.layout.spinner_item);
            //set the adapter's layout
            operator_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //give operator_box its constructed adapter and an onClickListener
            operator_box.setAdapter(operator_adapter);
            operator_box.setOnItemSelectedListener(this);
        }
    }

    /* I am doing this in place of onPause, because the Android documentation warns against saving data during onPause() */

    /**
     * Saving the current UI state so the user can resume instead of starting the activity over
     * I am doing this in place of onPause, because the Android documentation warns against saving data during onPause()
     *
     * @param outState
     * @author Littlesnwoman88
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save non-null value 1
        if (val1 != null) {
            outState.putDouble("VALUE1", val1);
        }
        //save non-null value 2
        if (val2 != null) {
            outState.putDouble("VALUE2", val2);
        }
        //save the currently selected operator
        outState.putString("OPERATOR", selected_operator);
    }

    /**
     * Restores the UI to its last known state.
     * Specifically, restores the two user values and the last-selected operator
     * Called after onStart()
     *
     * @param savedInstanceState
     * @author Littlesnowman88
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        val1 = savedInstanceState.getDouble("VALUE1");
        val2 = savedInstanceState.getDouble("VALUE2");
        selected_operator = savedInstanceState.getString("OPERATOR");

    }

    /**
     * Called after onRestoreInstanceState, onResume sets listeners to the UI elements
     *
     * @author Littlesnowman88
     */
    @Override
    protected void onResume() {
        super.onResume();

        //Set the EditText listeners
        input_box_1.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView inputView, int i, KeyEvent event) {
                return false; //returns false because not all inputs have necessarily been processed.
                //input numbers processed in retrieveInputVals();
            }
        });

        input_box_2.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView inputView, int i, KeyEvent event) {
                try {
                    //close the keyboard
                    InputMethodManager inputManager = (InputMethodManager)
                            getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                } catch (NullPointerException npe) {
                    npe.printStackTrace();
                    Toast.makeText(context, getString(R.string.keyboard_error),
                            Toast.LENGTH_SHORT).show();
                }
                return true; //as far as the editor is concerned, input 2 is the last editText to be edited, so return true
                //input numbers processed in retrieveInputVals();
            }
        });
    }

    /**
     * onItemSelected implements AdapterView.onItemSelectedListener
     * here, the function saves the user's selected operator into an instance variable
     *
     * @param parent   the root view that holds this adapter
     * @param view     the view (operator) clicked on by the adapter
     * @param position the indexed of the view clicked on by the adapter
     * @param id       the integer id of the view clicked on by the adapter
     * @author: Littlesnowman88
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_operator = parent.getItemAtPosition(position).toString();
    }

    /**
     * onNothingSelected implements AdapterView.onItemSelectedListener
     * here, if nothing is selected, the operator is restored to its default state
     *
     * @param parent the root view that holds this adapter
     * @author Littlesnowman88
     */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //if nothing is selected:
        if (selected_operator.equals("")) {
            //set the adapter to either the first listed operator, or else an empty string if no operators exist
            if (operators.length != 0) {
                selected_operator = getResources().getStringArray(R.array.operator_array)[0];
            } else {
                selected_operator = "";
            }
        }
    }

    /**
     * called by the calculate button, this calculates the user's values and sets the result text
     *
     * @param view the calculate button
     * @author Littlesnowman88
     */
    public void calculateButtonPressed(View view) {
        //private helper function to get the input values
        retrieveInputVals();

        if ((val1 != null) && (val2 != null)) {
            switch (selected_operator) {
                case "+":
                    setResultText(Double.toString(val1 + val2));
                    break;
                case "-":
                    setResultText(Double.toString(val1 - val2));
                    break;
                case "*":
                    setResultText(Double.toString(val1 * val2));
                    break;
                case "/":
                    if (val2 != 0) {
                        setResultText(Double.toString(val1 / val2));
                    } else {
                        setResultText(getString(R.string.zero_division_error));
                    }
                    break;
                case "^":
                    setResultText(Double.toString(Math.pow(val1, val2)));
                    break;
                case "%":
                    if (val2 != 0) {
                        setResultText(Double.toString(val1 % val2));
                    } else {
                        setResultText(getString(R.string.modulo_division_error));
                    }
                    break;
                case "//":
                    if (val2 != 0) {
                        setResultText(Double.toString(Math.floor(val1 / val2)));
                    } else {
                        setResultText(getString(R.string.zero_division_error));
                    }
                    break;
                default:
                    setResultText("");
            }
        } else {
            setResultText(getString(R.string.missing_values_error));
        }
    }

    /**
     * retrieveInputVals gets the numeric values from the user's two number input boxes
     * If the input can't be turned into a number, the val is set to null
     *
     * @author Littlesnowman88
     */
    private void retrieveInputVals() {
        try {
            val1 = Double.parseDouble(input_box_1.getText().toString());
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.Input1Error),
                    Toast.LENGTH_SHORT).show();
            val1 = null;
        }
        try {
            val2 = Double.parseDouble(input_box_2.getText().toString());
        } catch (Exception e) {
            Toast.makeText(context, getString(R.string.Input2Error),
                    Toast.LENGTH_SHORT).show();
            val2 = null;
        }
    }

    /**
     * Sets the result text box with the calculation result, passed in as a parameter
     * This function serves as a helper function to help with code readability
     *
     * @param result_text, a string form of the result
     * @author Littlesnowman88
     */
    private void setResultText(String result_text) {
        results_view.setText(result_text);
    }
}


