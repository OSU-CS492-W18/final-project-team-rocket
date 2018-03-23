package com.example.android.firstgenpokedex;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.example.android.firstgenpokedex.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

/**
 * Created by hessro on 2/20/18.
 */

public class GitHubSearchLoader extends AsyncTaskLoader<String> {
    private final static String NEW_BASE_URL = "https://pokeapi.co/api/v2/pokemon/";

    private String mSearchResultsJSON;
    private String mGitHubSearchURL;

    GitHubSearchLoader(Context context, String url) {
        super(context);
        mGitHubSearchURL = url;
    }

    @Override
    protected void onStartLoading() {
        if (mGitHubSearchURL != null) {
            if (mSearchResultsJSON != null) {
                Log.d("SOMETAG", "loader returning cached results");
                deliverResult(mSearchResultsJSON);
            } else {
                forceLoad();
            }
        }
    }

    @Override
    public String loadInBackground() {
        if (mGitHubSearchURL != null) {
            Log.d("SOMETAG", "loading results from Pokeapi with URL: " + mGitHubSearchURL);
            String searchResults = null;
            try {
                searchResults = NetworkUtils.doHTTPGet(mGitHubSearchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResults;
        } else {
            return null;
        }
    }

    @Override
    public void deliverResult(String data) {
        mSearchResultsJSON = data;
        super.deliverResult(data);
    }
}
