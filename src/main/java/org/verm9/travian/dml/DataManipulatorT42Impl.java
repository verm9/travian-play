package org.verm9.travian.dml;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * Created by nonu on 9/30/2016.
 */
public class DataManipulatorT42Impl implements DataManipulator {
    private static final Logger LOG = getLogger(DataManipulatorT42Impl.class);

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
    public InvocableTreeNode changeVillage;
    public InvocableTreeNode getStatisticsPage;
    public InvocableTreeNode getPlayerPage;
    @PostConstruct
    public void init() {
        dorf1 = new InvocableTreeNode(documentEvaluator, null) {
            protected Document execute(Object... args) throws IOException {
                InvocableTreeNode.currentNode = this;
                LOG.info("Going to " + server + "/dorf1.php");
                return JsoupDecorator.connect(server + "/dorf1.php")
                        .get();
            }
        };


        dorf1BuildPage = new InvocableTreeNode(documentEvaluator, dorf1, "dorf1BuildPageEvaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                Integer id = (Integer) ((Object[]) args[1])[0];
                LOG.info("Going to " + server + "/build.php?id=" + id);
                return JsoupDecorator.connect(server + "/build.php?id=" + id)
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
                    build += URLEncoder.encode(keyValue.getKey(), "UTF-8") + "=" + keyValue.getValue();
                    if (i++ == 0)
                        build += "&";
                }
                LOG.info("Going to " + server + build);
                return JsoupDecorator.connect(server + build)
                        .get();
            }
        };

        dorf2 = new InvocableTreeNode(documentEvaluator, dorf1, "dorf2Evaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                InvocableTreeNode.currentNode = this;
                LOG.info("Going to " + server + "/dorf2.php");
                return JsoupDecorator.connect(server + "/dorf2.php")
                        .get();
            }
        };

        dorf2BuildPage = new InvocableTreeNode(documentEvaluator, dorf2, "dorf2BuildPageEvaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                Integer id = (Integer) ((Object[]) args[1])[0];
                LOG.info("Going to " + server + "/build.php?id=" + id);
                return JsoupDecorator.connect(server + "/build.php?id=" + id)
                        .get();
            }
        };

        dorf2Build = new InvocableTreeNode(documentEvaluator, dorf2BuildPage, "dorf2Evaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                DataToSend data = (DataToSend) args[0];
                Integer idOfPlace = (Integer) ((Object[]) args[1])[0];
                Integer type = (Integer) ((Object[]) args[1])[1];
                String build = "/dorf2.php?";
                if (data.getData().containsKey("id")) { // build new
                    build = build + URLEncoder.encode("а", "UTF-8") + "=" + data.getData().get("а") + "&id=" + data.getData().get("id") + "&c=" + data.getData().get("c");
                } else { // upgrade
                    build = build + URLEncoder.encode("а", "UTF-8") + "=" + data.getData().get("а") + "&c=" + data.getData().get("c");
                }

                LOG.info("Going to " + server + build);
                return JsoupDecorator.connect(server + build)
                        .get();
            }
        };
        changeVillage = new InvocableTreeNode(documentEvaluator, dorf1, "dorf1Evaluator") {
            @Override
            protected Document execute(Object... args) throws IOException {
                Integer id = (Integer) ((Object[]) args[1])[0];
                LOG.info("Going to " + server + "/dorf1.php?newdid=" + id+ "&");
                return JsoupDecorator.connect(server + "/dorf1.php?newdid=" + id+ "&")
                        .get();
            }
        };
        getStatisticsPage = new InvocableTreeNode(documentEvaluator, dorf1, "statisticsGetMyProfilePage") {
            @Override
            protected Document execute(Object... args) throws IOException {
                LOG.info("Going to " + server + "/statistiken.php");
                return JsoupDecorator.connect(server + "/statistiken.php")
                        .get();
            }
        };
        getPlayerPage = new InvocableTreeNode(documentEvaluator, getStatisticsPage, "setCapitalFromProfilePage")  {
            @Override
            protected Document execute(Object... args) throws IOException {
                DataToSend data = (DataToSend) args[0];
                LOG.info("Going to " + server + "/spieler.php?uid="+data.getData().get("uid"));
                return JsoupDecorator.connect(server + "/spieler.php?uid="+data.getData().get("uid"))
                        .get();
            }
        };
    }


    @Override
    public Document getLoginPage() throws IOException {
        return JsoupDecorator.connect(server + "/login.php")
                .get();
    }

    @Override
    public Document login(Map<String, String> data) throws IOException {
        return JsoupDecorator.connect(server + "/login.php")
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
