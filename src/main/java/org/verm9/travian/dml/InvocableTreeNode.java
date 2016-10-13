package org.verm9.travian.dml;


import org.jsoup.nodes.Document;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nonu on 9/30/2016.
 */
abstract public class InvocableTreeNode {
    protected static InvocableTreeNode currentNode;

    private InvocableTreeNode parent;
    private String evaluator;

    private DocumentEvaluator documentEvaluator;

    public InvocableTreeNode(DocumentEvaluator documentEvaluator, InvocableTreeNode parent)  {
        this.documentEvaluator = documentEvaluator;
        this.parent = parent;
    }
    public InvocableTreeNode(DocumentEvaluator documentEvaluator, InvocableTreeNode parent, String evaluator) {
        this.documentEvaluator = documentEvaluator;
        this.parent = parent;
        this.evaluator = evaluator;
    }

    protected abstract Document execute(Object... args) throws IOException;

    /**
     * args[] is sent from business layer.
     * data is parsed from documents
     */
    public DataToSend invoke(Object... args) throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        DataToSend data = null;
        if (this.parent != null) {
            data = this.parent.invoke(args);
        }

        // Do a request.
        Document document = this.execute(data, args);

        // Parse it with documentEvaluator if evaluator exists.
        if (evaluator != null) {
            data = (DataToSend) documentEvaluator.getClass().getMethod(evaluator, Document.class).invoke(documentEvaluator, document);
        }
        return data;
    }

}
