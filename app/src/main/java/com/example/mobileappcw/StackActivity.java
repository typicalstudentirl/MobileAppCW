package com.example.mobileappcw;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StackActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    ArrayList<stackModel> stackItemsArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stack);
        recyclerView = findViewById(R.id.viewRecycler);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(100);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new Adapter(this, stackItemsArrayList);
        String url = "https://api.stackexchange.com/2.2/questions?order=desc&sort=activity&site=android";
        new GetStackTask().execute(url);
    }


    private class GetStackTask extends AsyncTask<String, Void, ArrayList> {

        public  String TAG = "StackActivity";

        @Override
        protected ArrayList doInBackground(String... strings) {

            try {
                URL url = new URL(strings[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
                StringBuilder builder = new StringBuilder();

                String inputString;
                while ((inputString = bufferedReader.readLine()) != null) {
                    builder.append(inputString);
                }
                JSONObject root = new JSONObject(builder.toString());
                JSONArray itemsArray = root.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {

                    // Create StackModel class to add to ArrayList
                    stackModel stackItems = new stackModel();

                    // Get values from JSON Array items
                    String title = itemsArray.getJSONObject(i).getString("title");
                    stackItems.setTitle(String.valueOf(title));

                    String link = itemsArray.getJSONObject(i).getString("link");
                    stackItems.setLink(String.valueOf(link));


                    JSONObject jsonImage = itemsArray.getJSONObject(i);
                    JSONObject img = jsonImage.getJSONObject("owner");

                    String image = String.valueOf(img.getString("profile_image"));
                    stackItems.setImage(String.valueOf(image));

                    // Add each StackModel to the list
                    stackItemsArrayList.add(stackItems);
                }

                urlConnection.disconnect();
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return stackItemsArrayList;

        }

        @Override
        protected void onPostExecute(ArrayList temp) {
            Log.d(TAG, "onPostExecute: " + temp);
            recyclerView.setAdapter(adapter);
        }

    }
}


































//public class StackActivity extends AppCompatActivity {
//
//    TextView txtView_title1;
//    TextView txtView_title2;
//    TextView txtView_title3;
//    TextView txtView_title4;
//    TextView txtView_title5;
//    TextView txtView_title6;
//    TextView txtView_title7;
//    TextView txtView_title8;
//    TextView txtView_title9;
//    TextView txtView_title10;
//    List<String> list = new ArrayList<>();
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_stack);
//
//        String url = "https://api.stackexchange.com/2.1/questions?/order=desc&sort=creation&site=stackoverflow&tagged=android";
//        new getStackQuestions().execute(url);
//
//
//        txtView_title1 = (TextView) findViewById(R.id.txtView_title1);
//        txtView_title2 = (TextView) findViewById(R.id.txtView_title2);
//        txtView_title3 = (TextView) findViewById(R.id.txtView_title3);
//        txtView_title4 = (TextView) findViewById(R.id.txtView_title4);
//        txtView_title5 = (TextView) findViewById(R.id.txtView_title5);
//        txtView_title6 = (TextView) findViewById(R.id.txtView_title6);
//        txtView_title7 = (TextView) findViewById(R.id.txtView_title7);
//        txtView_title8 = (TextView) findViewById(R.id.txtView_title8);
//        txtView_title9 = (TextView) findViewById(R.id.txtView_title9);
//        txtView_title10 = (TextView) findViewById(R.id.txtView_title10);
//    }
//
//    public class getStackQuestions extends AsyncTask<String, Void, List<String>> {
//        String info = "";
//
//        @Override
//        protected List<String> doInBackground(String... strings) {
//            try {
//                URL url = new URL(strings[0]);
//                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod("GET");
//
//                InputStream stream = new BufferedInputStream(urlConnection.getInputStream());
//                // convert input stream to string
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
//                StringBuilder builder = new StringBuilder();
//
//                String inputString;
//                while ((inputString = bufferedReader.readLine()) != null) {
//                    builder.append(inputString);
//                }
//
//
//                JSONObject items = new JSONObject();
//                JSONArray itemsArray = items.getJSONArray("items");
//
//
//                for (int i = 0; i < itemsArray.length(); i++) {
//                   String title = itemsArray.getJSONObject(i).getString("title");
//                    list.add(title);
//                }
//                urlConnection.disconnect();
//            } catch (JSONException | IOException e) {
//                e.printStackTrace();
//            }
//            return list;
//        }
//
//        // onPostExecute displays the results of the AsyncTask
//        @Override
//        protected void onPostExecute(List<String> list) {
//            txtView_title1.setText(list.get(0));
//            txtView_title2.setText(list.get(1));
//            txtView_title3.setText(list.get(2));
//        }
//    }
//}