package org.verm9.travian.dml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.verm9.travian.dml.dto.Dorf2;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Map;

/**
 * Created by nonu on 9/30/2016.
 */
public class DataManipulatorT42Impl implements DataManipulator {

    // set up in spring-app.xml
    private String server;

    @Autowired
    private DocumentEvaluator documentEvaluator;



    public InvocableTreeNode dorf1;
    public InvocableTreeNode dorf1BuildPage;
    public InvocableTreeNode dorf1Build;
    public InvocableTreeNode dorf2;
    public InvocableTreeNode dorf2BuildPage;
    public InvocableTreeNode dorf2Build;
    @PostConstruct
    public void init() {
        dorf1 = new InvocableTreeNode(documentEvaluator, null) {
            protected Document execute(Object... args) throws IOException {
                return Jsoup.connect(server + "/dorf1.php")
                        .get();
            }
        };


        dorf1BuildPage = new InvocableTreeNode(documentEvaluator, dorf1, "dorf1BuildPageEvaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                Integer id = (Integer) ((Object[]) args[1])[0];
                return Jsoup.connect(server + "/build.php?id=" + id)
                        .get();
            }
        };

        dorf1Build = new InvocableTreeNode(documentEvaluator, dorf1BuildPage, "dorf1Evaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                DataToSend data = (DataToSend) args[0];
                String build = "/dorf1.php?";
                int i = 0;
                for (Map.Entry<String, String> keyValue : data.getData().entrySet()) {
                    build += keyValue.getKey() + "=" + keyValue.getValue();
                    if (i++ == 0)
                        build += "&";
                }
                return Jsoup.connect(server + build)
                        .get();
            }
        };

        dorf2 = new InvocableTreeNode(documentEvaluator, dorf1, "dorf2Evaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                return Jsoup.connect(server + "/dorf2.php")
                        .get();
            }
        };

        dorf2BuildPage = new InvocableTreeNode(documentEvaluator, dorf2, "dorf2BuildPageEvaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                Integer id = (Integer) ((Object[]) args[1])[0];
                return Jsoup.connect(server + "/build.php?id=" + id)
                        .get();
            }
        };

        dorf2Build = new InvocableTreeNode(documentEvaluator, dorf2BuildPage, "dorf2Evaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                DataToSend data = (DataToSend) args[0];
                Integer idOfPlace = (Integer) ((Object[]) args[1])[0];
                Dorf2.Building.Type type = (Dorf2.Building.Type) ((Object[]) args[1])[1];
                String build = "/dorf2.php?";
                build += "%D0%B0=" + idOfPlace;
                build += "&id=" + type.getId();
                build += "&c=";
                build += data.getData().get("c"); // csrf from evaluator

                return Jsoup.connect(server + build)
                        .get();
            }
        };
    }



    @Override
    public Document getLoginPage() throws IOException {
        return Jsoup.connect(server + "/login.php")
                .get();
    }

    @Override
    public Document login(Map<String, String> data) throws IOException {
        return Jsoup.connect(server + "/login.php")
                .data(data)
                .followRedirects(true) // get 302 to dorf1
                .post();
    }


    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public DocumentEvaluator getDocumentEvaluator() {
        return documentEvaluator;
    }

    public void setDocumentEvaluator(DocumentEvaluator documentEvaluator) {
        this.documentEvaluator = documentEvaluator;
    }
}
