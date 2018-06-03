package com.example.android.afterguardian;

import android.text.TextUtils;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public final class QueryUtils {

    private QueryUtils() {
    }

    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e("--- Error ---", "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == urlConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e("--- Error ---", "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e("--- Error ---", "Problem retrieving the article JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link Article} objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<Article> extractFeatureFromJson(String articleJSON) {
        String mPillarName;

        if (TextUtils.isEmpty(articleJSON)) {
            return null;
        }

        List<Article> articles = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(articleJSON);
            JSONObject articleObj = baseJsonResponse.getJSONObject(RESPONSE);
            JSONArray arrayArticle = articleObj.getJSONArray(RESULTS);

            for (int i = 0; i < arrayArticle.length(); i++) {

                JSONObject result = arrayArticle.getJSONObject(i);

                //if key exist
                if (result.has("pillarName")) {
                    mPillarName = "pillarName";
                } else {
                    mPillarName = "sectionName";
                }

                String pillarName = result.getString(mPillarName);

                String webTitle = result.getString("webTitle");

                String webPublicationDate = result.getString("webPublicationDate");

                String url = result.getString("webUrl");

                JSONArray tagsArray = result.getJSONArray("tags");

                String author = "";
                if (tagsArray.length() > 0) {
                    JSONObject resultTag = tagsArray.getJSONObject(0);
                    author = resultTag.getString("webTitle");
                } else {
                    author = "Unknown author";
                }

                Article article = new Article(pillarName, webTitle, webPublicationDate, url, author);

                articles.add(article);
            }

        } catch (JSONException e) {
            Log.e("-- QueryUtils --", "Problem parsing the Article JSON results", e);
        }

        return articles;
    }

    /**
     * Query the THE GUARDIAN dataset and return a list of {@link Article} objects.
     */
    public static List<Article> fetchArticleData(String requestUrl) {

        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e("--fetchArticleData--", "Problem making the HTTP request.", e);
        }

        List<Article> articles = extractFeatureFromJson(jsonResponse);

        return articles;
    }
}
