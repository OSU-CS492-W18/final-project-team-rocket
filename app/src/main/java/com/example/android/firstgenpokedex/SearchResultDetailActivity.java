package com.example.android.firstgenpokedex;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.firstgenpokedex.utils.NetworkUtils;
import com.example.android.firstgenpokedex.utils.PokeApiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class SearchResultDetailActivity extends AppCompatActivity  {

    private TextView mTVSearchResultName;
    private TextView mTVSearchResultStars;
    private TextView mTVSearchResultDescription;

    public JSONObject pokemonInfo;

    public String typeStr;

    private PokeApiUtils.SearchResult mSearchResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mTVSearchResultName = (TextView)findViewById(R.id.tv_search_result_name);
        mTVSearchResultStars = (TextView)findViewById(R.id.tv_search_result_stars);
        mTVSearchResultDescription = (TextView)findViewById(R.id.tv_search_result_description);


        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PokeApiUtils.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (PokeApiUtils.SearchResult) intent.getSerializableExtra(PokeApiUtils.EXTRA_SEARCH_RESULT);
            mTVSearchResultName.setText(mSearchResult.fullName);
            //mTVSearchResultStars.setText(String.valueOf(mSearchResult.stars));
            mTVSearchResultDescription.setText(mSearchResult.pokemonURL);
        }

        // new get stuff
        String newBaseURL = "https://pokeapi.co/api/v2/pokemon/" + mSearchResult.fullName;
        Log.d("DETAIL", "querying search URL: " + newBaseURL);

        new PokeSearchTask().execute(newBaseURL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_view_repo:
                //viewRepoOnWeb();
                return true;
            case R.id.action_share:
                //shareRepo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PokeSearchTask extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                try {
                    JSONObject resultObj = new JSONObject(NetworkUtils.doHTTPGet(urls[0]));
                    Log.d("DETAIL",resultObj.toString());
                    pokemonInfo = new JSONObject(resultObj.toString());
                    return resultObj.toString();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            Log.d("POSTEXECUTE", s);
            typeStr = "";
            if(s != null) {
                try {
                    pokemonInfo = new JSONObject(s);
                    JSONArray typesJSONArray = pokemonInfo.getJSONArray("types");
                    for (int i = 0; i < typesJSONArray.length(); i++) {
                        typeStr = typeStr + typesJSONArray.getJSONObject(i).getJSONObject("type").getString("name") + ",";
                    }
                    //typeStr = pokemonInfo.getJSONArray("types").getJSONObject(0).getJSONObject("type").getString("name");
                    Log.d("DETAIL_MAIN", typeStr);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("POKEMON_INFO","IS NULL!");
            }
        }


//    public void viewRepoOnWeb() {
//        if (mSearchResult != null) {
//            Uri githubRepoURL = Uri.parse(mSearchResult.htmlURL);

//            Intent webIntent = new Intent(Intent.ACTION_VIEW, githubRepoURL);
//            if (webIntent.resolveActivity(getPackageManager()) != null) {
//                startActivity(webIntent);
//            }
//        }
//    }
//
//    public void shareRepo() {
//        if (mSearchResult != null) {
//            String shareText = getString(R.string.share_text_prefix) + ": " +
//                  mSearchResult.fullName + ", " + mSearchResult.htmlURL;
//
//            ShareCompat.IntentBuilder.from(this)
//                    .setChooserTitle(R.string.share_chooser_title)
//                    .setType("text/plain")
//                    .setText(shareText)
//                    .startChooser();
//        }
//    }
    }
}
