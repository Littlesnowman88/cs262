package ajf27.cs262.calvin.edu.lab03;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SecondActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY =
            "ajf27.cs262.calvin.edu.lab03.SecondActivity.extra.REPLY";
    private EditText mReply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        Intent start_intent = getIntent();
        String received_message = start_intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        TextView message_view = (TextView) findViewById(R.id.text_message);
        message_view.setText(received_message);
        mReply = (EditText) findViewById(R.id.editText_second);
    }

    public void returnReply(View view) {
        String reply = mReply.getText().toString();
        Intent second_to_main = new Intent();
        second_to_main.putExtra(EXTRA_REPLY, reply);
        setResult(RESULT_OK, second_to_main);
        finish();
    }


}
