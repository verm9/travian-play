package org.verm9.travian.dml;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Map;

/**
 * Created by nonu on 9/30/2016.
 */
public interface DataManipulator {
    Document getLoginPage() throws IOException;
    Document login(Map<String, String> data) throws IOException;
}
