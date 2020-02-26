package com.uowm.ekasdym.activities;



import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.widget.ListView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.uowm.ekasdym.R;
import com.uowm.ekasdym.adapters.AllMatchesListAdapter;
import com.uowm.ekasdym.model.Match;
import com.uowm.ekasdym.utilities.JSONParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class AllMatches extends AppCompatActivity {

    private String url = "",name = "";
    private int id;
    private static final String Match_Details = "Match_Details";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        Intent i = getIntent();
        id = i.getIntExtra("id", 0);
        name = i.getStringExtra("title");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_item);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPrimary)));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_left);
        getSupportActionBar().setTitle(Html.fromHtml("<font color='#FFFFFF'>"+name+"</font>"));
        new JSONParse().execute();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    public class JSONParse extends AsyncTask < String, String, String > {
        private ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(AllMatches.this);
            pDialog.setMessage(getString(R.string.waiting_screen));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

        }

        @Override
        protected String doInBackground(String...args) {


            url = getString(R.string.server) + "getAllMatch.php?id=" + id;

            JSONParser jParser = new JSONParser();

            String st = jParser.getJSONFromUrl(url);

            return st;
        }


        @Override
        protected void onPostExecute(String json) {

            ArrayList <Match> matches = new ArrayList < > ();

            final Handler handler = new Handler();
            handler.postDelayed(() -> pDialog.dismiss(), 500);

            int error_code = 0;
            JSONObject jobj = null;

            try {
                jobj = new JSONObject(json);
                JSONObject errorObject = jobj.getJSONObject("ERROR");
                error_code = errorObject.getInt("error_code");

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (error_code == 200) {

                try {
                    JSONArray jsonArray = jobj.getJSONArray(Match_Details);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);


                        String team1 = obj.getString("team_id_1");
                        String team2 = obj.getString("team_id_2");
                        int team_1_score = obj.getInt("team_score_1");
                        int team_2_score = obj.getInt("team_score_2");
                        int id = obj.getInt("id");
                        String date_time = obj.getString("date_time");
                        String latitude = obj.getString("latitude");
                        String longitude = obj.getString("longitude");
                        int state = obj.getInt("state");


                        Match match = new Match(team1, team2, team_1_score, team_2_score, date_time, latitude, longitude, id,state);
                        matches.add(match);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                final ListView listView = (ListView) findViewById(android.R.id.list);
                listView.setAdapter(new AllMatchesListAdapter(AllMatches.this, matches));

            } else if (error_code == 403) {
                Toast toast = Toast.makeText(AllMatches.this, getString(R.string.error_code_403), Toast.LENGTH_LONG);
                toast.show();
                AllMatches.this.finishAffinity();
            } else if (error_code == 204) {
                Toast toast = Toast.makeText(AllMatches.this, getString(R.string.error_code_204), Toast.LENGTH_LONG);
                toast.show();
            } else {
                Toast toast = Toast.makeText(AllMatches.this, getString(R.string.error_code_0), Toast.LENGTH_LONG);
                toast.show();
                AllMatches.this.finishAffinity();
            }

            final ListView listView = (ListView) findViewById(android.R.id.list);
            listView.setAdapter(new AllMatchesListAdapter(AllMatches.this, matches));

        }
    }
}