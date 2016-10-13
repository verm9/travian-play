package org.verm9.travian.dml;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by nonu on 9/30/2016.
 */
public interface DocumentEvaluator {
    void login() throws IOException;

    void dorf1Evaluator(Document document, Object... args);

    DataToSend dorf1BuildPageEvaluator(Document document, Object... args);

    void dorf2Evaluator(Document document, Object... args);

    DataToSend dorf2BuildPageEvaluator(Document document, Object... args);
}
