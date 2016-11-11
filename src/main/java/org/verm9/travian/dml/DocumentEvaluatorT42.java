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
import org.verm9.travian.dto.Point;
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

    private volatile String password;
    private volatile String login;

    @Autowired
    private DataManipulatorT42Impl dataManipulator;
    @Autowired
    private Central central;


    @Override
    public void login() throws IOException{
        Document pageWithLoginForm = dataManipulator.getLoginPage();
        Element loginForm = null;
        try {
            loginForm = pageWithLoginForm.getElementsByClass("innerLoginBox").first().getElementsByTag("form").first();
        } catch (NullPointerException e) {
            // The app might be logged in already. Let's check it.
            // Next line can throw UnexpectedPageException. Which is right if it is not a login page or page where
            // resources are shown (these are shown on every page but login one.
            parseResourceCount(pageWithLoginForm);
            return;
        }

        Map<String, String> formData = getDataFromForm(loginForm);
        if (!formData.containsKey("user") || !formData.containsKey("pw")) {
            throw new UnexpectedPageException();
        }
        formData.put("user", login);
        formData.put("pw", password);
        logger.info("Logging in with " + login);
        Document redirectedToDorf1 = dataManipulator.login(formData);

        // Check if login was successful. Process UnexpectedPageException if not.
        parseResourceCount(redirectedToDorf1);

        dorf1Evaluator(redirectedToDorf1);
    }

    @Override
    public void dorf1Evaluator(Document document, Object... args) {
        logger.trace("Parsing dorf1...");

        parseVillagesList(document);

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

        // todo: throw for no resources
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
        logger.trace("Parsing dorf2...");

        parseVillagesList(document);

        // Get <map> with clickareas. It may content an upgrade's price.
        Element buildingElement = document.getElementById("clickareas");

        Map<Integer, Dorf2.Building> result = new HashMap<>();
        Integer id = 19; // id of first building in dorf2

        // Get <img> with building. Next <div> will be containing palisade.
        while(  !( buildingElement = buildingElement.nextElementSibling() ).tagName().equals("div")  ) {
            Dorf2.Building.Type type = null;
            Integer level = null;
            String title = buildingElement.attr("alt");
            if (title.equals("Construction Site")) {
                type = Dorf2.Building.Type.NO_DATA;
                level = 0;
            } else if (title.contains("Rally Point")) {
                String[] temp = title.split(" ");
                if (temp.length == 3) {
                    level = Integer.valueOf(temp[2]);
                } else {
                    // Rally point lvl zero doesn't have a note about it's level.
                    level = 0;
                }
                type = Dorf2.Building.Type.RALLY_POINT;
            } else {
                String[] temp = title.split(" Level ");
                String name = temp[0];
                if (temp.length == 2) {
                    level = Integer.valueOf(temp[1]);
                } else {
                    // Rally point lvl zero doesn't have a note about it's level.
                    level = 0;
                }
                switch(name.trim()) {
                    case "Main Building":
                        type = Dorf2.Building.Type.MAIN_BUILDING;
                        break;
                    case "Warehouse":
                        type = Dorf2.Building.Type.WAREHOUSE;
                        break;
                    case "Granary":
                        type = Dorf2.Building.Type.GRANARY;
                        break;
                    case "Rally Point":
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
                    case "Sawmill":
                        type = Dorf2.Building.Type.SAWMILL;
                        break;
                    case "Brickworks":
                        type = Dorf2.Building.Type.BRICKWORKS;
                        break;
                    case "Iron Foundry":
                        type = Dorf2.Building.Type.IRON_FOUNDRY;
                        break;
                    case "Grain Mill":
                        type = Dorf2.Building.Type.GRAIN_MILL;
                        break;
                    case "Bakery":
                        type = Dorf2.Building.Type.BAKERY;
                        break;
                    case "Tournament Square":
                        type = Dorf2.Building.Type.TOURNAMENT_SQUARE;
                        break;
                    case "Marketplace":
                        type = Dorf2.Building.Type.MARKETPLACE;
                        break;
                    case "Barracks":
                        type = Dorf2.Building.Type.BARRACKS;
                        break;
                    case "Residence":
                        type = Dorf2.Building.Type.RESIDENCE;
                        break;
                    case "Treasury":
                        type = Dorf2.Building.Type.TREASURY;
                        break;
                    case "Trapper":
                        type = Dorf2.Building.Type.TRAPPER;
                        break;
                    case "Heromansion":
                        type = Dorf2.Building.Type.HEROMANSION;
                        break;
                    case "Academy":
                        type = Dorf2.Building.Type.ACADEMY;
                        break;
                    case "Smithy":
                        type = Dorf2.Building.Type.SMITHY;
                        break;
                    case "Workshop":
                        type = Dorf2.Building.Type.WORKSHOP;
                        break;
                    case "Town Hall":
                        type = Dorf2.Building.Type.TOWN_HALL;
                        break;
                    case "Stable":
                        type = Dorf2.Building.Type.STABLE;
                        break;
                    case "Trade Office":
                        type = Dorf2.Building.Type.TRADE_OFFICE;
                        break;
                    default:
                        throw new UnexpectedLocalizationException();
                }
            }
            Dorf2.Building building = new Dorf2.Building(type, level);
            result.put(id++, building);
        }

        // Now process the palisade.
        Element palisadeData = document.getElementById("clickareas").getElementsByTag("area")
                .last().nextElementSibling();
        Integer level;
        if (palisadeData != null) {
            level = Integer.valueOf(palisadeData.attr("alt").split(" level ")[1]);
        } else {
            level = 0;
        }
        result.put(id, new Dorf2.Building(Dorf2.Building.Type.PALISADE, level));


        central.getCurrentVillage().setAvailableResources( parseResourceCount(document) );
        central.getCurrentVillage().getDorf2().setBuildings(result);
    }

    @Override
    public DataToSend dorf2BuildPageEvaluator(Document document, Object... args) {
        Map<String, String> data = new HashMap<>();
        Integer id = (Integer) args[1];
        Integer placeId = (Integer) args[0];
        logger.info("Parsing dorf2 building page for buildPlace " + placeId + "...");

        // Check if building queue can get one more building
        Element first = document.select("div#contract").first();
        if (first.html().contains("All Workers are busy.")) {
            throw new BuildingQueueIsFullException("");
        }

        boolean isUpgrade = false;
        Elements buttons = document.select("button.green.new"); // "new" for new buildings
        if (buttons.isEmpty()) {
            buttons = document.select("button.green.small");  // "small" for building to upgrade
            isUpgrade = true;
        }
        String[] csrfArr;
        String[] spotIdArr;
        String[] buildingArr = new String[2];
        for (Element e : buttons) {
            String onClick = e.attr("onClick");
            String getRequest = StringUtils.substringBetween(onClick, "'", "'");
            String csrfKeyValue = getRequest.substring(getRequest.lastIndexOf("&") + 1);
            csrfArr = csrfKeyValue.split("=");
            getRequest = getRequest.substring(0, getRequest.lastIndexOf("&"));
            if (!isUpgrade) {
                String spotIdKeyValue = getRequest.substring(getRequest.lastIndexOf("&") + 1);
                spotIdArr = spotIdKeyValue.split("=");

                getRequest = getRequest.substring(0, getRequest.lastIndexOf("&"));
                getRequest = getRequest.substring(getRequest.lastIndexOf("?") + 1);
                String buildingKeyValue = getRequest.substring(getRequest.lastIndexOf("&") + 1);
                buildingArr = buildingKeyValue.split("=");
                logger.debug("\t available building IDs (not all): " + buildingArr[1]);
            } else {
                getRequest = getRequest.substring(getRequest.lastIndexOf("?")+1);
                String spotIdKeyValue = getRequest.substring(getRequest.lastIndexOf("?") + 1);
                spotIdArr = spotIdKeyValue.split("=");
            }


            if (isUpgrade) {
                data.put(spotIdArr[0], spotIdArr[1]);
                data.put(csrfArr[0], csrfArr[1]);
                break; // There are two buttons int the barracks.
            } else {
                // there are several buttons with different buildings
                if (buildingArr[1].equals(String.valueOf(id))) {
                    data.put(spotIdArr[0], spotIdArr[1]);
                    data.put(buildingArr[0], buildingArr[1]);
                    data.put(csrfArr[0], csrfArr[1]);
                    break;
                }
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

    public DataToSend statisticsGetMyProfilePage(Document document, Object... args) {
        Map<String, String> data = new HashMap<>();
        Elements tdWithHref = document.select("table#player>tbody>tr.hl>td.pla");
        String id = StringUtils.substringBetween(tdWithHref.html(), "spieler.php?uid=", "\">");
        data.put("uid", id);
        return new DataToSend(data);
    }

    public void setCapitalFromProfilePage(Document document, Object... args) {
        Element tableWithVillages = document.select("table#villages>tbody").first();
        for (Element e : tableWithVillages.getElementsByTag("tr")) {
            Integer id = Integer.parseInt( StringUtils.substringBetween(e.html(), "\"karte.php?d=", "\">") );
            if (e.html().contains("mainVillage")) {
                central.getVillage(id).setCapital(true);
            } else {
                central.getVillage(id).setCapital(false);
            }
        }
    }


    private Map<Village.Resource, Long> parseResourceCount(Document document) {
        if (document.getElementById("l1") == null) {
            throw new UnexpectedPageException();
        }
        Map<Village.Resource, Long> result = new HashMap<>(4);
        result.put( Village.Resource.WOOD, Long.parseLong(document.getElementById("l1").text()) );
        result.put( Village.Resource.CLAY, Long.parseLong(document.getElementById("l2").text()) );
        result.put( Village.Resource.IRON, Long.parseLong(document.getElementById("l3").text()) );
        result.put( Village.Resource.CROP, Long.parseLong(document.getElementById("l4").text()) );

        return result;
    }

    private void parseVillagesList(Document document) {
        Elements villageEntry = document.select("div.innerBox.content>ul>li");
        for (Element e : villageEntry) {
            if (e.id().contains("infoID")) {
                continue; // most likely it is not that we want (under protection div)
            }
            String nameAndCooridinate = e.attr("title");
            String name = nameAndCooridinate.substring(0, nameAndCooridinate.indexOf("(")).trim();
            String[] coordinates = StringUtils.substringBetween(nameAndCooridinate, "(", ")").split("\\|");
            String id = StringUtils.substringBetween(e.getElementsByTag("a").first().attr("href"), ".php?newdid=", "&");
            if (e.attr("class").contains("active")) {
                central.setCurrentVillage(Integer.parseInt(id));
            }
            Village toAdd = new Village( Integer.parseInt(id), name, new Point(Integer.parseInt(coordinates[0]), Integer.parseInt(coordinates[1])) );
            central.addNewVillageIfNotAdded(Integer.parseInt(id), toAdd);
        }
    }

    private Map<String,String> getDataFromForm(Element form) {
        Map<String, String> result = new HashMap<>();
        Elements inputFields = form.getElementsByTag("input");
        for (Element e : inputFields) {
            result.put(e.attr("name"), e.attr("value"));
        }
        return result;
    }


    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }
}
