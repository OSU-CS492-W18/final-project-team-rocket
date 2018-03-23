package com.example.android.firstgenpokedex.utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by hessro on 1/30/18.
 */

public class PokeApiUtils {
    public static final String EXTRA_SEARCH_RESULT = "PokeApiUtils.SearchResult";

    final static String GITHUB_SEARCH_BASE_URL = "https://pokeapi.co/api/v2/";
    final static String FIRST_GEN_KANTO = "pokedex/kanto/";
    final static String GITHUB_SEARCH_QUERY_PARAM = "q";
    final static String GITHUB_SEARCH_SORT_PARAM = "sort";

    public static class SearchResult implements Serializable , Comparable<SearchResult>{
        public String fullName;
        public String pokemonURL;
        public int entryNum;

        @Override
        public int compareTo(@NonNull SearchResult entry) {
            return this.fullName.charAt(0);
        }
    }

    public static String buildGitHubSearchURL() {

        Uri.Builder builder = Uri.parse(GITHUB_SEARCH_BASE_URL + "pokedex/kanto/").buildUpon();

        return builder.build().toString();
    }

    public static ArrayList<SearchResult> parseSearchResultsJSON(String searchResultsJSON) {
        try {
            JSONObject searchResultsObj = new JSONObject(searchResultsJSON);
            JSONArray searchResultsItems = searchResultsObj.getJSONArray("pokemon_entries");

            ArrayList<SearchResult> searchResultsList = new ArrayList<SearchResult>();
            for (int i = 0; i < searchResultsItems.length(); i++) {
                SearchResult result = new SearchResult();
                JSONObject resultItem = searchResultsItems.getJSONObject(i);
                result.entryNum = resultItem.getInt("entry_number");
                result.fullName = resultItem.getJSONObject("pokemon_species").getString("name");
                result.pokemonURL = GITHUB_SEARCH_BASE_URL + "pokemon/" + result.fullName + "/";
                searchResultsList.add(result);
            }
            return searchResultsList;
        } catch (JSONException e) {
            return null;
        }
    }
}
