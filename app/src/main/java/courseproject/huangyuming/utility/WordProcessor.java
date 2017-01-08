package courseproject.huangyuming.utility;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lusiwei on 2017/1/7.
 */

public class WordProcessor {
    private static final String DIVIDER_URL = "http://api.ltp-cloud.com/analysis/";
    private static final String BOSON_TIME_URL = "http://api.bosonnlp.com/time/analysis";
    private static final String TOKEN = "hMmHWWid.11283._tGryZoQ5HpS";
    private static final String API_KEY = "q9R131y6HtyFTMCe3ukqNXXeHWGO2IWk6FRCaq2X";
    private static final int UPDATE_CONTENT = 0;

    private static final Map<String, String> queries = new HashMap<>();

    public WordProcessor() {
        queries.put("api_key", API_KEY);
        queries.put("format", "json");
        queries.put("pattern", "all");
    }

    public List<String> divider(final String text) {
        HttpURLConnection connect = null;
        List<String> words = new ArrayList<>();

        try {
            Map<String, String> query = new HashMap<>(queries);
            query.put("text", URLEncoder.encode(text, "UTF-8"));

            connect = (HttpURLConnection) new URL(DIVIDER_URL +
                    encodeQueries(query)).openConnection();
            connect.setRequestMethod("GET");
            connect.setReadTimeout(150000);
            connect.setConnectTimeout(150000);
            connect.setDoInput(true);

            InputStream inputStream = connect.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while (null != (line = reader.readLine())) {
                stringBuilder.append(line + "\n");
            }

            words = analyzeDividerResponse(stringBuilder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connect != null) { connect.disconnect(); }
        }

        return words;
    }
    private List<String> analyzeDividerResponse(String response) {
        List<String> words = new ArrayList<>();

        try {
            JSONArray array = new JSONArray(response)
                    .getJSONArray(0)
                    .getJSONArray(0);

            final int rowNum = 5;
            for (int i = 0; i < array.length() / rowNum + 1; ++i) {
                int max = 0;
                if (i == array.length() / rowNum) { max = array.length() - i * rowNum; }
                else { max = rowNum; }

                for (int j = 0; j < max; ++j) {
                    String word = array.getJSONObject(i * rowNum + j).getString("cont");
                    words.add(word);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return words;
    }

    public String[] bosonTime(final String text) {
        Map<String, String> body = new HashMap<>();
        String[] result = null;

        try {
            body.put("pattern", URLEncoder.encode(text, "UTF-8"));
            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(BOSON_TIME_URL + encodeQueries(body));

            httpPost.addHeader("Content-Type", "application/json");
            httpPost.addHeader("Accept", "application/json");
            httpPost.addHeader("X-Token", TOKEN);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity entity = httpResponse.getEntity();
            InputStream inputStream = entity.getContent();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while (null != (line = reader.readLine())) {
                line = URLDecoder.decode(line, "UTF-8");
                stringBuilder.append(line + "\n");
            }
            JSONObject timeObject = new JSONObject(stringBuilder.toString());
            String[] timestamp = timeObject.getString("timestamp").split(" ");
            result = timestamp;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    private static String encodeQueries(Map<String, String> query) {
        StringBuilder stringBuilder = new StringBuilder("?");
        for (String key : query.keySet()) {
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(query.get(key));
            stringBuilder.append("&");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
