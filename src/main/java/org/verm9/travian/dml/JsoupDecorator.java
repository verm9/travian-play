package org.verm9.travian.dml;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by nonu on 10/25/2016.
 * Purpose of the class is to simplify using of jsoup by encapsulating all generalized logic
 * (referrers, cookies, timeout, user agent).
 */
public class JsoupDecorator {
    private Connection connection;

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.82 Safari/537.36 OPR/39.0.2256.48";
    private static final Logger logger = LoggerFactory.getLogger(JsoupDecorator.class);
    private static Map<String, String> cookies = new HashMap<>();
    private static String referrer = "";

    public JsoupDecorator(String url) {
        connection = Jsoup.connect(url);
        connection.timeout(5000)
                .userAgent(USER_AGENT)
                .cookies(cookies);

        if (!referrer.equals("")) {
            connection.referrer(referrer);
        }
    }

    public static JsoupDecorator connect(String url) {
        return new JsoupDecorator(url);
    }

    public Document get() throws IOException {
        Document result = connection.get();
        handleCookieAndReferrer();
        return result;
    }

    public Document post() throws IOException {
        Document result = connection.post();
        handleCookieAndReferrer();
        return result;
    }

    /**
     * Adds auto referrer inlining using last request's url. Async request will be counted too.
     * Also adds all cookies to request which has been gained in every request before. In other
     * words, path property for cookie from HTTP response will be not processed.
     */
    private void handleCookieAndReferrer() {
        try {
            Field res = connection.getClass().getDeclaredField("res");
            res.setAccessible(true);
            HttpConnection.Response response = (HttpConnection.Response) res.get(connection);

            referrer = response.url().toString();

            // Todo: even cookies with wrong path will be attached in this way. Another cookie params are not counted too.
            cookies.putAll( response.cookies() );

        } catch (NoSuchFieldException | IllegalAccessException e) {
            logger.error(e.getMessage());
        }
    }

    public JsoupDecorator data(Map<String, String> data) {
        connection.data(data);
        return this;
    }

    public JsoupDecorator followRedirects(boolean b) {
        connection.followRedirects(b);
        return this;
    }

}
