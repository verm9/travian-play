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
import org.verm9.travian.business.Central;
import org.verm9.travian.dto.Dorf1;
import org.verm9.travian.dto.Dorf2;
import org.verm9.travian.dto.Village;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.verm9.travian.dto.Dorf1.*;

/**
 * Created by nonu on 9/30/2016.
 */
public class DocumentEvaluatorT42 implements DocumentEvaluator {
    private static final Logger logger = LoggerFactory.getLogger(DocumentEvaluatorT42.class);

    private String password;
    private String login;

    @Autowired
    private DataManipulatorT42Impl dataManipulator;
    @Autowired
    private Central central;


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
    public void dorf1Evaluator(Document document, Object... args) {
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
                case "The field is in the maximum level":
                    resourceField.setType(ResourceField.ResourceType.FULLY_UPGRADED);
                    break;
                default:
                    throw new UnexpectedLocalizationException();
            }

            Matcher m = p.matcher(div.html());
            m.find();
            if (resourceField.getType() == ResourceField.ResourceType.FULLY_UPGRADED) {
                resourceField.setLevel(20); // todo: check if capital
            } else {
                int resourceLevel = Integer.parseInt(m.group(1));
                resourceField.setLevel(resourceLevel);
            }
            result.put(id, resourceField);
        }


        central.getCurrentVillage().setAvailableResources( parseResourceCount(document) );
        central.getCurrentVillage().getDorf1().setFields(result);

    }

    @Override
    public DataToSend dorf1BuildPageEvaluator(Document document, Object... args) {
        Elements button = document.select("button.green.small");
        String onclick = button.attr("onclick");

        // todo: throw for no resource
        // todo: throw for full building queue (in game)
        if (document.getElementById("build_value").nextElementSibling().html().contains("The building is at the maximum level.")) {
            throw new BuildingAtTheMaximumLevelException("");
        }

        // ?%D0%B0=2&c=0Mp
        onclick = onclick.substring(onclick.lastIndexOf("?") + 1);
        if (onclick.equals("")) {
            throw new BuildingQueueIsFullException("");
        }
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
    public void dorf2Evaluator(Document document, Object... args) {
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
                    case "Warehouse":
                        type = Dorf2.Building.Type.WAREHOUSE;
                        break;
                    case "Granary":
                        type = Dorf2.Building.Type.GRANARY;
                        break;
                    case "Rally point":
                        type = Dorf2.Building.Type.RALLY_POINT;
                        break;
                    case "Embassy":
                        type = Dorf2.Building.Type.EMBASSY;
                        break;
                    case "Cranny":
                        type = Dorf2.Building.Type.CRANNY;
                        break;
                    case "Palisade":
                        type = Dorf2.Building.Type.PALISADE;
                        break;
                    default:
                        throw new UnexpectedLocalizationException();
                }
                level = Integer.valueOf(StringUtils.substringBetween(title, "level ", "</div>"));
            }
            Dorf2.Building building = new Dorf2.Building(type, level);
            result.put(id, building);
        }
        central.getCurrentVillage().setAvailableResources( parseResourceCount(document) );
        central.getCurrentVillage().getDorf2().setBuildings(result);
    }

    @Override
    public DataToSend dorf2BuildPageEvaluator(Document document, Object... args) {
        Map<String, String> data = new HashMap<>();
        Integer id = (Integer) args[1];
        logger.info("Parsing dorf2 building page for buildPlace"+id+"...");

        // Get any csrf token from any button.
        Elements buttons = document.select("button.green.new");
        String[] csrfArr;
        String[] spotIdArr;
        String[] buildingArr;
        for (Element e : buttons) {
            String onClick = e.attr("onClick");
            String getRequest = StringUtils.substringBetween(onClick, "'", "'");
            String csrfKeyValue = getRequest.substring(getRequest.lastIndexOf("&") + 1);
            csrfArr = csrfKeyValue.split("=");
            getRequest = getRequest.substring(0, getRequest.lastIndexOf("&"));
            String spotIdKeyValue = getRequest.substring(getRequest.lastIndexOf("&") + 1);
            spotIdArr = spotIdKeyValue.split("=");
            getRequest = getRequest.substring(0, getRequest.lastIndexOf("&"));
            getRequest = getRequest.substring(getRequest.lastIndexOf("?")+ 1);
            String buildingKeyValue = getRequest.substring(getRequest.lastIndexOf("&") + 1);
            buildingArr = buildingKeyValue.split("=");

            logger.debug("\t available building IDs (not all): " + buildingArr[1]);
            if (buildingArr[1].equals(String.valueOf(id))) {
                data.put(buildingArr[0], buildingArr[1]);
                data.put(spotIdArr[0], spotIdArr[1]);
                data.put(csrfArr[0], csrfArr[1]);
                break;
            }
        }

        if (data.isEmpty()) {
            throw new WrongBuildingIdException("Button with building id is not found");
            // Reasons? No free slots, wrong id (building with this id is never existed),
            // no required buildings. Or a building is fully upgraded. Or a building is built
            // on another slot.
        }

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
