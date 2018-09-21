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

    private Double val1, val2;
    private String selected_operator;
    private String[] operators;
    private EditText input_box_1, input_box_2;
    private ArrayAdapter<CharSequence> operator_adapter;
    private TextView results_view;
    private Context context;


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
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (val1 != null) {outState.putDouble("VALUE1", val1);}
        if (val2 != null) {outState.putDouble("VALUE2", val2);}
        outState.putString("OPERATOR", selected_operator);
    }

    /* called after onStart() */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        val1 = savedInstanceState.getDouble("VALUE1");
        val2 = savedInstanceState.getDouble("VALUE2");
        selected_operator = savedInstanceState.getString("OPERATOR");

    }

    /* onResume, set the textbox listeners */
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

    /* implementing AdapterView.onItemSelectedListener */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        selected_operator = parent.getItemAtPosition(position).toString();
    }

    /* implementing AdapterView.onItemSelectedListener */
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (selected_operator.equals("")) {
            if (operators.length != 0) {
                selected_operator = getResources().getStringArray(R.array.operator_array)[0];
            } else {
                selected_operator = operators[0];
            }
        }
    }

    /* the real work of the calculator */
    public void calculateButtonPressed(View view) {
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

    /* getting the numeric values from the user's two number input boxes
    *  If the input can't be turned into a number, the val is set to null
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

    /* helper function to help with code readability */
    private void setResultText(String result_text) {
        results_view.setText(result_text);
    }
}


