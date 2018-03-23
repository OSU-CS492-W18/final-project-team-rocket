package com.example.android.firstgenpokedex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Network;
import android.net.Uri;
import android.os.AsyncTask;
import android.service.autofill.FillEventHistory;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.android.firstgenpokedex.utils.NetworkUtils;
import com.example.android.firstgenpokedex.utils.PokeApiUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
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
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class SearchResultDetailActivity extends AppCompatActivity {

    private TextView mTVSearchResultName;

    public JSONObject pokemonInfo, evolutionInfo;

    public String spriteURL;
    public String ability1, ability2;
    public boolean ability1Hidden, ability2Hidden;
    public ArrayList<String> evolutionImageURLs, typeStr;

    private ImageView mTVSearchResultAvi;
    private ImageView mTVEvoTreeOne;
    private ImageView mTVEvoTreeTwo;
    private ImageView mTVEvoTreeThree;
    private TextView mTVSearchResultType1;
    private TextView mTVSearchResultType2;
    private TextView mTVSearchResultAbil;
    private TextView mTVSearchResultHidAbil;
    private ImageView mTVArrowOne;
    private ImageView mTVArrowTwo;

    private PokeApiUtils.SearchResult mSearchResult;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_detail);

        mTVSearchResultName = (TextView) findViewById(R.id.tv_search_result_name);
        mTVSearchResultType1 = (TextView) findViewById(R.id.tv_search_result_type1);
        mTVSearchResultType2 = (TextView) findViewById(R.id.tv_search_result_type2);
        mTVSearchResultAbil = (TextView) findViewById(R.id.tv_search_result_ability);
        mTVSearchResultHidAbil = (TextView) findViewById(R.id.tv_search_result_hidden_ability);
        mTVSearchResultAvi = (ImageView) findViewById(R.id.tv_search_result_avi);
        mTVEvoTreeOne = (ImageView) findViewById(R.id.tv_evo_one);
        mTVEvoTreeTwo = (ImageView) findViewById(R.id.tv_evo_two);
        mTVEvoTreeThree = (ImageView) findViewById(R.id.tv_evo_three);
        mTVArrowOne = (ImageView) findViewById(R.id.tv_arrow_one);
        mTVArrowTwo = (ImageView) findViewById(R.id.tv_arrow_two);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(PokeApiUtils.EXTRA_SEARCH_RESULT)) {
            mSearchResult = (PokeApiUtils.SearchResult) intent.getSerializableExtra(PokeApiUtils.EXTRA_SEARCH_RESULT);
            mTVSearchResultName.setText(mSearchResult.fullName);

        }
        
        int[] pokemonSounds = new int[151];
        String filename;
        for(int i = 0; i<151; i++){
            filename = "p"+String.valueOf(i+1);
            int rawId = getResources().getIdentifier(filename, "raw", getPackageName());
            pokemonSounds[i] = rawId;
        }

        Log.d("INRESULTACTIVITY", "This is the file id we are using: " + pokemonSounds[mSearchResult.entryNum]);
        final MediaPlayer battleCry = MediaPlayer.create(this, pokemonSounds[mSearchResult.entryNum]);
        Log.d("INRESULTACTIVITY", "This is the battle cry media id we are using: " + battleCry);
        ImageButton battleCryBtn = (ImageButton) this.findViewById(R.id.action_battle_cry);

        battleCryBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                battleCry.start();
            }
        });

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
//            //case R.id.action_view_repo:
//                //viewRepoOnWeb();
//                return true;
//            //case R.id.action_share:

//                //shareRepo();
//                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class PokeSearchTask extends AsyncTask<String, Void, String> {
//        ImageView imageView;

//        public PokeSearchTask(ImageView viewById) {
//            this.imageView = imageView;
//        }

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
                    Log.d("DETAIL", resultObj.toString());

                    JSONObject speciesObj = new JSONObject(NetworkUtils.doHTTPGet(resultObj.getJSONObject("species").getString("url")));
                    Log.d("DETAIL_S", speciesObj.toString());
                    JSONObject evolutionObj = new JSONObject(NetworkUtils.doHTTPGet(speciesObj.getJSONObject("evolution_chain").getString("url")));
                    Log.d("DETAIL_E",evolutionObj.toString());
                    evolutionInfo = new JSONObject(evolutionObj.toString());
                    typeStr = new ArrayList<>();
                    String type;

                    if (pokemonInfo != null) {
                        try {
                            //pokemonInfo = new JSONObject(s);
                            JSONArray typesJSONArray = pokemonInfo.getJSONArray("types");
                            for (int i = 0; i < typesJSONArray.length(); i++) {
                                typeStr.add(typesJSONArray.getJSONObject(i).getJSONObject("type").getString("name"));
                                Log.d("DETAIL_T",typeStr.toString());
                            }

                            JSONArray abilitiesJSONArray = pokemonInfo.getJSONArray("abilities");
                            for (int i = 0; i < abilitiesJSONArray.length(); i++) {
                                if (i == 0) {
                                    ability1 = abilitiesJSONArray.getJSONObject(0).getJSONObject("ability").getString("name");
                                    ability1 = ability1.substring(0, 1).toUpperCase() + ability1.substring(1);
                                    ability1Hidden = abilitiesJSONArray.getJSONObject(0).getBoolean("is_hidden");
                                }
                                if (i == 1) {
                                    ability2 = abilitiesJSONArray.getJSONObject(1).getJSONObject("ability").getString("name");
                                    ability2 = ability2.substring(0, 1).toUpperCase() + ability2.substring(1);
                                    ability2Hidden = abilitiesJSONArray.getJSONObject(1).getBoolean("is_hidden");
                                }
                            }

                            spriteURL = pokemonInfo.getJSONObject("sprites").getString("front_default");

                            Log.d("DETAIL_MAIN ABILS", ability1 + ":" + Boolean.toString(ability1Hidden) + " " + ability2 + ":" + Boolean.toString(ability2Hidden));
                            Log.d("DETAIL_MAIN SPRITE", spriteURL);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Log.d("POKEMON_INFO", "IS NULL!");
                    }


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


//                    Bitmap bimage = null;
//                    try {
//                        InputStream in = new java.net.URL(spriteURL).openStream();
//                        bimage = BitmapFactory.decodeStream(in);
//
//                    } catch (Exception e) {
//                        Log.e("Error Message", e.getMessage());
//                        e.printStackTrace();
//                    }
//
//                    return bimage;
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
            //  Log.d("POSTEXECUTE", s);
            Picasso.with(SearchResultDetailActivity.this)
                    .load(spriteURL)
                    .resize(600,600)
                    .into(mTVSearchResultAvi);

            if(typeStr.size() > 1) {
                mTVSearchResultType1.setText(typeStr.get(1));
                mTVSearchResultType2.setText(typeStr.get(0));
            }
            else{
                mTVSearchResultType1.setText(typeStr.get(0));
            }

            if(ability1Hidden == true){
                mTVSearchResultHidAbil.setText(ability1);
            }
            else {
                mTVSearchResultAbil.setText(ability1);
            }

            if(ability2Hidden == true){
                mTVSearchResultHidAbil.setText(ability2);
            }
            else {
                mTVSearchResultAbil.setText(ability2);
            }

            if(evolutionImageURLs.size() == 3 ){
                Picasso.with(SearchResultDetailActivity.this)
                        .load(evolutionImageURLs.get(0))
                        .resize(200,200)
                        .into(mTVEvoTreeOne);
                Picasso.with(SearchResultDetailActivity.this)
                        .load(evolutionImageURLs.get(1))
                        .resize(200,200)
                        .into(mTVEvoTreeTwo);
                Picasso.with(SearchResultDetailActivity.this)
                        .load(evolutionImageURLs.get(2))
                        .resize(200,200)
                        .into(mTVEvoTreeThree);
                mTVArrowOne.setVisibility(View.VISIBLE);
                mTVArrowTwo.setVisibility(View.VISIBLE);
            }
            else if(evolutionImageURLs.size() == 2) {
                Picasso.with(SearchResultDetailActivity.this)
                        .load(evolutionImageURLs.get(0))
                        .resize(200,200)
                        .into(mTVEvoTreeOne);
                Picasso.with(SearchResultDetailActivity.this)
                        .load(evolutionImageURLs.get(1))
                        .resize(200,200)
                        .into(mTVEvoTreeTwo);
                mTVArrowOne.setVisibility(View.VISIBLE);
            }
            else{
                Picasso.with(SearchResultDetailActivity.this)
                        .load(evolutionImageURLs.get(0))
                        .resize(200,200)
                        .into(mTVEvoTreeOne);
            }

        }
    }
}
