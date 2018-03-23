package com.example.android.firstgenpokedex;

import android.content.Intent;
import android.net.Network;
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

import android.net.Uri;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.firstgenpokedex.utils.PokeApiUtils;

import org.w3c.dom.Text;

public class SearchResultDetailActivity extends AppCompatActivity {

    private TextView mTVSearchResultName;
    private TextView mTVSearchResultStars;

    private TextView mTVSearchResultDescription;
    public JSONObject pokemonInfo, evolutionInfo;

    public String typeStr, spriteURL;
    public String ability1, ability2;
    public boolean ability1Hidden, ability2Hidden;
    public ArrayList<String> evolutionImageURLs;

    private ImageView mTVSearchResultAvi;
    private TextView mTVSearchResultType1;
    private TextView mTVSearchResultType2;
    private TextView mTVSearchResultAbil;
    private TextView mTVSearchResultHidAbil;

    private PokeApiUtils.SearchResult mSearchResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mTVSearchResultName = (TextView)findViewById(R.id.tv_search_result_name);

//        mTVSearchResultStars = (TextView)findViewById(R.id.tv_search_result_stars);
//        mTVSearchResultAvi = (ImageView) findViewById(R.id.tv_search_result_avi);
        mTVSearchResultType1 = (TextView) findViewById(R.id.tv_search_result_type1);
        mTVSearchResultType2 = (TextView) findViewById(R.id.tv_search_result_type2);
        mTVSearchResultAbil = (TextView) findViewById(R.id.tv_search_result_ability);
        mTVSearchResultHidAbil = (TextView) findViewById(R.id.tv_search_result_hidden_ability);

        mTVSearchResultAvi = (ImageView) findViewById(R.id.tv_search_result_avi);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PokeApiUtils.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (PokeApiUtils.SearchResult) intent.getSerializableExtra(PokeApiUtils.EXTRA_SEARCH_RESULT);
            mTVSearchResultName.setText(mSearchResult.fullName);

            mTVSearchResultType1.setText("Type 1");
            mTVSearchResultType2.setText("Type 2");
            mTVSearchResultAbil.setText("Ability");
            mTVSearchResultHidAbil.setText("Hidden Ability");
            //mTVSearchResultStars.setText(String.valueOf(mSearchResult.stars));
//            mTVSearchResultDescription.setText(mSearchResult.pokemonURL);

        }

        // new get stuff
        String newBaseURL = "https://pokeapi.co/api/v2/pokemon/" + mSearchResult.fullName;
        Log.d("DETAIL", "querying search URL: " + newBaseURL);

        new PokeSearchTask().execute(newBaseURL);
            //mTVSearchResultDescription.setText(mSearchResult.pokemonURL);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_result_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
//            case R.id.action_view_repo:
//                //viewRepoOnWeb();
//                return true;
//            case R.id.action_share:
//                //shareRepo();
//                return true;
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
                    pokemonInfo = new JSONObject(resultObj.toString());
                    Log.d("DETAIL",resultObj.toString());

                    JSONObject speciesObj = new JSONObject(NetworkUtils.doHTTPGet(resultObj.getJSONObject("species").getString("url")));
                    Log.d("DETAIL_S",speciesObj.toString());
                    JSONObject evolutionObj = new JSONObject(NetworkUtils.doHTTPGet(speciesObj.getJSONObject("evolution_chain").getString("url")));
                    Log.d("DETAIL_E",evolutionObj.toString());
                    evolutionInfo = new JSONObject(evolutionObj.toString());

                    if(evolutionInfo != null) {
                        evolutionImageURLs = new ArrayList<String>();
                        JSONObject evolutionChain = evolutionInfo.getJSONObject("chain");
                        try {
                            JSONObject imgObj = new JSONObject(NetworkUtils.doHTTPGet("https://pokeapi.co/api/v2/pokemon/" + evolutionChain.getJSONObject("species").getString("name")));
                            String imgURL = imgObj.getJSONObject("sprites").getString("front_default");
                            evolutionImageURLs.add(imgURL);

                            JSONArray imgJSONArray = evolutionChain.getJSONArray("evolves_to");
                            if(!imgJSONArray.isNull(0)) {
                                imgObj = new JSONObject(NetworkUtils.doHTTPGet("https://pokeapi.co/api/v2/pokemon/" + imgJSONArray.getJSONObject(0).getJSONObject("species").getString("name")));
                                imgURL = imgObj.getJSONObject("sprites").getString("front_default");
                                evolutionImageURLs.add(imgURL);

                                JSONArray imgJSONArray_F = imgJSONArray.getJSONObject(0).getJSONArray("evolves_to");
                                if(!imgJSONArray_F.isNull(0)) {
                                    imgObj = new JSONObject(NetworkUtils.doHTTPGet("https://pokeapi.co/api/v2/pokemon/" + imgJSONArray_F.getJSONObject(0).getJSONObject("species").getString("name")));
                                    imgURL = imgObj.getJSONObject("sprites").getString("front_default");
                                    evolutionImageURLs.add(imgURL);
                                }
                            }

                            for (String iUrl : evolutionImageURLs) {
                                Log.d("IMAGE URL ", "0 " + iUrl);
                            }

                        } catch (IOException ie) {
                            ie.printStackTrace();
                        }

                    }

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
            if(pokemonInfo != null) {
                try {
                    //pokemonInfo = new JSONObject(s);
                    JSONArray typesJSONArray = pokemonInfo.getJSONArray("types");
                    for (int i = 0; i < typesJSONArray.length(); i++) {
                        typeStr = typeStr + typesJSONArray.getJSONObject(i).getJSONObject("type").getString("name") + ",";
                    }

                    JSONArray abilitiesJSONArray = pokemonInfo.getJSONArray("abilities");
                    for (int i = 0; i < abilitiesJSONArray.length(); i++) {
                        if(i == 0) {
                            ability1 = abilitiesJSONArray.getJSONObject(0).getJSONObject("ability").getString("name");
                            ability1 = ability1.substring(0, 1).toUpperCase() + ability1.substring(1);
                            ability1Hidden = abilitiesJSONArray.getJSONObject(0).getBoolean("is_hidden");
                        }
                        if(i == 1) {
                            ability2 = abilitiesJSONArray.getJSONObject(1).getJSONObject("ability").getString("name");
                            ability2 = ability2.substring(0, 1).toUpperCase() + ability2.substring(1);
                            ability2Hidden = abilitiesJSONArray.getJSONObject(1).getBoolean("is_hidden");
                        }
                    }

                    spriteURL = pokemonInfo.getJSONObject("sprites").getString("front_default");

                    Log.d("DETAIL_MAIN TYPES", typeStr);
                    Log.d("DETAIL_MAIN ABILS", ability1 + ":" + Boolean.toString(ability1Hidden) + " " + ability2 + ":" + Boolean.toString(ability2Hidden));
                    Log.d("DETAIL_MAIN SPRITE", spriteURL);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d("POKEMON_INFO","IS NULL!");
            }
        }
    }
}
