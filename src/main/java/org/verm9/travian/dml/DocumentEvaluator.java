package org.verm9.travian.dml;

import org.jsoup.nodes.Document;

import java.io.IOException;

/**
 * Created by nonu on 9/30/2016.
 */
public interface DocumentEvaluator {
    void login() throws IOException;

    void dorf1Evaluator(Document document);

    DataToSend dorf1BuildPageEvaluator(Document document);

    void dorf2Evaluator(Document document);

    DataToSend dorf2BuildPageEvaluator(Document document);
}
