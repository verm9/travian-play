package org.verm9.travian.dml;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.verm9.travian.dml.dto.Dorf1;
import org.verm9.travian.dml.dto.Dorf2;
import org.verm9.travian.dml.dto.Village;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.verm9.travian.dml.dto.Dorf1.*;

/**
 * Created by nonu on 9/30/2016.
 */
public class DocumentEvaluatorT42 implements DocumentEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(DocumentEvaluatorT42.class);

    private String password;
    private String login;

    @Autowired
    private DataManipulatorT42Impl dataManipulator;

    private Village village = new Village();

    @Override
    public void login() throws IOException{
        Document pageWithLoginForm = dataManipulator.getLoginPage();
        Element loginForm = pageWithLoginForm.getElementsByClass("innerLoginBox").first().getElementsByTag("form").first();
        Map<String, String> formData = getDataFromForm(loginForm);
        if (!formData.containsKey("user") || !formData.containsKey("pw")) {
            throw new UnexpectedPageException();
        }
        formData.put("user", login);
        formData.put("pw", password);
        logger.info("Logging in with " + login);
        Document redirectedToDorf1 = dataManipulator.login(formData);
        dorf1Evaluator(redirectedToDorf1);
    }

    @Override
    public void dorf1Evaluator(Document document) {
        logger.info("Parsing dorf1...");
        Elements resources = null;
        try {
            resources = document.getElementById("rx").getElementsByTag("area");
            resources.remove(resources.size()-1); // the last contains link to the dorf2
        } catch (NullPointerException e) {
            throw new UnexpectedPageException();
        }

        /* Resources are collection of <area>'s with pattern:
        <area href="dorf1Build.php?id=1" coords="180,80,28" shape="circle" title="<div style='color:#FFF'><b>Woodcutter</b> level 0</div> Upgrading to level 1 costs:<br/>
             <span class='resources r1'> <img class='r1' src='img/x.gif' > 40 </span>
             <span class='resources r2'> <img class='r2' src='img/x.gif' > 100 </span>
             <span class='resources r3'> <img class='r3' src='img/x.gif' > 50 </span>
             <span class='resources r4'> <img class='r4' src='img/x.gif' > 60 </span> "/> */
        Pattern p = Pattern.compile("> level ([0-9]*)");
        Map<Integer, Dorf1.ResourceField> result = new HashMap<>();
        for (Element e : resources) {
            ResourceField resourceField = new ResourceField();
            Element div = Jsoup.parse(e.attr("title"), "", Parser.xmlParser());
            String resourceType = div.getElementsByTag("b").first().text();
            String href = e.getElementsByTag("area").first().attr("href");
            Integer id = Integer.parseInt( href.substring(href.lastIndexOf("=")+1) );
            switch (resourceType) {
                case "Woodcutter":
                    resourceField.setType(ResourceField.ResourceType.WOODCUTTER);
                    break;
                case "Clay Pit":
                    resourceField.setType(ResourceField.ResourceType.CLAY_PIT);
                    break;
                case "Iron Mine":
                    resourceField.setType(ResourceField.ResourceType.IRON_MINE);
                    break;
                case "Cropland":
                    resourceField.setType(ResourceField.ResourceType.CROPLAND);
                    break;
                default:
                    throw new UnexpectedLocalizationException();
            }

            Matcher m = p.matcher(div.html());
            m.find();
            int resourceLevel = Integer.parseInt(m.group(1));
            resourceField.setLevel(resourceLevel);
            result.put(id, resourceField);
        }

        village.setAvailableResources( parseResourceCount(document) );
        village.getDorf1().setFields(result);
    }

    @Override
    public DataToSend dorf1BuildPageEvaluator(Document document) {
        Elements button = document.select("button.green.small");
        String onclick = button.attr("onclick");

        // ?%D0%B0=2&c=0Mp
        onclick = onclick.substring(onclick.lastIndexOf("?") + 1);
        onclick = onclick.substring(0, onclick.lastIndexOf("'"));
        String[] keyValue = onclick.split("&");
        Map<String, String> data = new HashMap<>();
        for (String s : keyValue) {
            String[] split = s.split("=");
            data.put(split[0], split[1]);
        }
        return new DataToSend(data, DataToSend.Type.GET);
    }

    @Override
    public void dorf2Evaluator(Document document) {
        logger.info("Parsing dorf2...");
        Elements buildings = document.getElementById("clickareas").getElementsByTag("area");

        Map<Integer, Dorf2.Building> result = new HashMap<>();
        for (Element e : buildings) {
            String href = e.attr("href");
            Integer id = Integer.valueOf( href.substring(href.lastIndexOf("=") + 1) );

            String title = e.attr("title");
            Dorf2.Building.Type type = null;
            Integer level = null;
            if (title.equals("Building is fully upgraded")) {
                type = Dorf2.Building.Type.NO_DATA;
                level = 0;
            } else {
                String name = StringUtils.substringBetween(title, "<b>", "</b>");
                switch(name) {
                    case "Main Building":
                        type = Dorf2.Building.Type.MAIN_BUILDING;
                        break;
                    default:
                        throw new UnexpectedLocalizationException();
                }
                level = Integer.valueOf(StringUtils.substringBetween(title, "level ", "</div>"));
            }
            Dorf2.Building building = new Dorf2.Building(type, level);
            result.put(id, building);
        }
        village.setAvailableResources( parseResourceCount(document) );
        village.getDorf2().setBuildings(result);
    }

    @Override
    public DataToSend dorf2BuildPageEvaluator(Document document) {
        // Will return csrf (c=) token only. And check availability. Other data DataManipulator will get from
        // business level.
        Map<String, String> data = new HashMap<>();

        // Get any csrf token from any button.
        Elements buttons = document.select("button.green.new");
        String onClick = buttons.first().attr("onClick");
        String getRequest = StringUtils.substringBetween(onClick, "'", "'");
        String csrfKeyValue = getRequest.substring(getRequest.lastIndexOf(";") + 1);
        String csrfArr[] = csrfKeyValue.split("=");
        data.put(csrfArr[0], csrfArr[1]);

        // Check availability.
        Pattern p = Pattern.compile("");
                // fail
        // reform DataToSend to array (yes, dude, make array of Maps)
        // or reform the tree with sending data to parsers.


        return new DataToSend(data, DataToSend.Type.GET);
    }


    private Map<Village.Resource, Integer> parseResourceCount(Document document) {
        if (document.getElementById("l1") == null) {
            throw new UnexpectedPageException();
        }
        Map<Village.Resource, Integer> result = new HashMap<>(4);
        result.put( Village.Resource.WOOD, Integer.parseInt(document.getElementById("l1").text()) );
        result.put( Village.Resource.CLAY, Integer.parseInt(document.getElementById("l2").text()) );
        result.put( Village.Resource.IRON, Integer.parseInt(document.getElementById("l3").text()) );
        result.put( Village.Resource.CROP, Integer.parseInt(document.getElementById("l4").text()) );

        return result;
    }

    private Map<String,String> getDataFromForm(Element form) {
        Map<String, String> result = new HashMap<>();
        Elements inputFields = form.getElementsByTag("input");
        for (Element e : inputFields) {
            result.put(e.attr("name"), e.attr("value"));
        }
        return result;
    }



    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }
}
