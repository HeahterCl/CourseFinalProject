package courseproject.huangyuming.utility;

import android.util.Pair;
import android.util.StringBuilderPrinter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;

import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by lusiwei on 2017/1/7.
 */

public class WordProcessor {
    private static final String DIVIDER_URL = "http://api.ltp-cloud.com/analysis/";
    private static final String BOSON_TIME_URL = "http://api.bosonnlp.com/time/analysis?pattern=";
    private static final String TOKEN = "hMmHWWid.11283._tGryZoQ5HpS";
    private static final String API_KEY = "q9R131y6HtyFTMCe3ukqNXXeHWGO2IWk6FRCaq2X";
    private static final int UPDATE_CONTENT = 0;

    private static final Map<String, String> queries = new HashMap<>();

//    public String[] divide(final String string) {
//        return []
//    }



    private static String encodeQueries(Map<String, String> query) {
        StringBuilder stringBuilder = new StringBuilder("?");
        query.forEach(new BiConsumer<String, String>() {
            @Override
            public void accept(String s, String s2) {
                stringBuilder.append(s);
                stringBuilder.append("=");
                stringBuilder.append(s2);
                stringBuilder.append("&");
            }
        });
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
