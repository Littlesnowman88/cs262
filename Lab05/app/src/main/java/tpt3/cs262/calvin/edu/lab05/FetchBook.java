package tpt3.cs262.calvin.edu.lab05;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

public class FetchBook extends AsyncTask<String,Void,String> {
    private TextView titleText;
    private TextView authorText;
    private Context context;

    public FetchBook(TextView titleText, TextView authorText, Context context) {
        this.titleText = titleText;
        this.authorText = authorText;
        this.context = new WeakReference<Context>(context).get();
    }

    @Override
    protected String doInBackground(String... params) {
        return NetworkUtils.getBookInfo(params[0]);
    }

    //Method for handling the results on the UI thread
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {
            JSONObject jsonObject = new JSONObject(s);
            JSONArray itemsArray = jsonObject.getJSONArray("items");
            for(int i = 0; i<itemsArray.length(); i++){
                JSONObject book = itemsArray.getJSONObject(i);
                String title=null;
                String authors=null;
                JSONObject volumeInfo = book.getJSONObject("volumeInfo");


                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors");
                } catch (Exception e){
                    e.printStackTrace();
                }


                if (title != null && authors != null){
                    titleText.setText(title);
                    authorText.setText(authors);
                    return;
                }
            }


            titleText.setText("No Results Found");
            authorText.setText("");


        } catch (Exception e){
            titleText.setText("No Results Found");
            authorText.setText("");
            e.printStackTrace();
        }
    }
}
