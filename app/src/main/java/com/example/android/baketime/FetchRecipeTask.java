package com.example.android.baketime;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jebus on 5/29/2017.
 */

public class FetchRecipeTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchRecipeTask.class.getSimpleName();

    public OnTaskCompleted listener = null;

    @Override
    protected String[] doInBackground(String... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        StringBuffer buffer;
        // Will contain the raw JSON response as a string.
        String recipeJsonStr = null;
        try {
            // Construct the URL for the query
            final String recipe_URL = "https://go.udacity.com/android-baking-app-json";


            URL url = new URL(recipe_URL);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                //Put newline at end of each line in JSON results
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                Log.i(LOG_TAG, "Stream was empty, returning null");
                return null;
            }
            recipeJsonStr = buffer.toString();
        } catch (IOException e) {
            Log.e("error", "Error ", e);
            // If the code didn't successfully get the data, there's no point in attempting
            // to parse it.
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("error", "Error closing stream", e);
                }
            }
        }

        try {
            return getRecipeDataFromJson(recipeJsonStr);
        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }
        return null;
    }

    private String[] getRecipeDataFromJson(String recipeJsonStr)
            throws JSONException {
        String[] resultStrings;
        // These are the names of the JSON objects that need to be extracted.
        JSONArray recipeArray = new JSONArray(recipeJsonStr);

        resultStrings = new String[recipeArray.length()];

        for (int i = 0; i < recipeArray.length(); i++) {
            JSONObject recipe = recipeArray.getJSONObject(i);
            resultStrings[i] = recipe.toString();
        }

        return resultStrings;
    }

    @Override
    protected void onPostExecute(String[] result) {
        listener.onTaskCompleted(result);
    }
}
