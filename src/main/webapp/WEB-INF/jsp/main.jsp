<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.min.css">
    <link rel="stylesheet" href="resources/css/main.css">
    <script type="text/javascript" src="webjars/jquery/2.2.4/jquery.min.js"></script>
    <script type="text/javascript" src="webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="webjars/datatables/1.10.12/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="webjars/noty/2.3.8/js/noty/packaged/jquery.noty.packaged.min.js"></script>
</head>
<body>
<h1>Travian-play testing</h1>
<div><button type="button" class="btn btn-success btn-lg" id="startButton" style="position: absolute; top: 78px; left: 15px;">Start!</button></div>
<div style="position: absolute; top: 70px; left: 120px;">
    <table id="gameDataTable" class="table-bordered">
        <tbody>
        <tr>
            <td>

            </td>
        </tr>
        </tbody>
    </table>
</div>

<div id="feedback" style="position: absolute; top: 600px;"></div>
<div id="buildingModal" class="modal fade" role="dialog">
    <div class="modal-dialog">

    <!-- Modal content-->
    <div class="modal-content">
        <div class="modal-header">
            <button type="button" class="close" data-dismiss="modal">&times;</button>
            <h4 class="modal-title">Building Menu</h4>
        </div>
        <div class="modal-body" id="buildingModalContent">
            <p></p>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        </div>
    </div>

</div>
</div>
</body>

<script type="text/javascript">

    var ajaxUrl = '/ajax/';
    var gameData;

    $(function () {

        // Get gameData
        function getGameData() {
            $.ajax({
                type: "GET",
                contentType : "application/json",
                url: ajaxUrl + "getGameData",
                dataType : "json",
                timeout : 100000,
                success : function(data) {
                    gameData = data;
                    console.log("SUCCESS: ", data);
                    display(data);
                    drawTable(gameData);
                },
                error : function(e) {
                    console.log("ERROR: ", e);
                    display(e);
                },
                done : function(e) {
                    console.log("DONE");
                }
            });
        }


        // Setup a listener on "Start!"/"Pause" button.
        $("#startButton").click(function() {
            $.ajax({
                type: "GET",
                contentType : "application/json",
                url: ajaxUrl + "switchRunningState",
                dataType : "json",
                timeout : 100000,
                success : function(data) {
                    console.log("SUCCESS: ", data);
                    setupStartButton(data);
                },
                error : function(e) {
                    console.log("ERROR: ", e);
                    display(e);
                },
                done : function(e) {
                    console.log("DONE");
                }
            });
        });

        //$("#startButton").click();

        // Update gameData every 2 seconds.
        getGameData();
        setInterval(function () {
            getGameData();
        }, 2000);

    });


    function setupStartButton(isRunning) {
        var button = $('#startButton');
        if (isRunning == false) {
            button[0].innerHTML = "Pause!";
            button.removeClass("btn-success");
            button.addClass("btn-warning");
        } else {
            button[0].innerHTML = "Start!";
            button.removeClass("btn-warning");
            button.addClass("btn-success");
        }
    }


    // For json debugging.
    function display(data) {
        var json = "<h4>Ajax Response</h4><pre>"
                + JSON.stringify(data, null, 4) + "</pre>";
        $('#feedback').html(json);
    }

    // Fills the gameDataTable.
    function drawTable(gameData) {
        var villages = gameData.villages;
        var innerHTML = "<thead align='center'><tr><td>Village</td><td>Resources</td><td>Dorf1 resource fields</td><td>Dorf2 buildings</td>" +
                "<td>Priority</td><td>ChgPriority</td></tr></thead>";
        innerHTML += "<tbody>";
        display(gameData); // for debugging purposes
        for (var index in gameData.villages) {
            var v = villages[index];
            innerHTML += "<tr id='"+index+"'>";
            innerHTML += "<td class='villageName' rowspan=2>" + v.name + "<br/>("+v.coordinates.x+"|"+v.coordinates.y+")<br/>id: "+ index +"</td>";

            innerHTML += "<td class='resources'>" +
                    "<div class='wood' data-toggle='tooltip' data-placement='top' title='WOOD'>"+v.availableResources.WOOD+"</div>" +
                    "<div class='clay' data-toggle='tooltip' data-placement='top' title='CLAY'>"+v.availableResources.CLAY+"</div>" +
                    "<div class='iron' data-toggle='tooltip' data-placement='top' title='IRON'>"+v.availableResources.IRON+"</div>" +
                    "<div class='crop' data-toggle='tooltip' data-placement='top' title='CROP'>"+v.availableResources.CROP+"</div></td>";

            innerHTML += "<td><table class=\"dorf1\" class=\"table-bordered table-condensed \"><tr>";
            for (var fieldIndex in v.dorf1.fields) {
                var f = v.dorf1.fields[fieldIndex];
                var colorClass;
                switch(f.type) {
                    case "CROPLAND":
                        colorClass = "crop";
                        break;
                    case "CLAY_PIT":
                        colorClass = "clay";
                        break;
                    case "IRON_MINE":
                        colorClass = "iron";
                        break;
                    case "WOODCUTTER":
                        colorClass = "wood";
                        break;
                    default:
                        colorClass = "fullyUpgraded";
                        break;
                }
                var type = "<div data-toggle=\"tooltip\" data-placement=\"top\" title=\""+f.type+"\">"+f.type.substring(0, 2)+"</div>";
                innerHTML += "<td class=\""+colorClass+"\">"+type+"<br/>"+f.level+"</td>";
            }
            innerHTML += "</tr></table></td>";

            innerHTML += "<td><table class=\"dorf2\"><tr>";
            if (!jQuery.isEmptyObject(v.dorf2.buildings)) {
                for (var buildingIndex in v.dorf2.buildings) {
                    var b = v.dorf2.buildings[buildingIndex];
                    var type = "<div data-toggle=\"tooltip\" data-placement=\"top\" title=\"" + b.type + "\">" + b.type.substring(0, 1) + "</div>";
                    innerHTML += "<td>" + type + "<br/>" + b.level + "</td>";
                }
            }
            innerHTML += "</tr></table></td>";

            innerHTML += "<td>"+v.priority+"</td>";

            innerHTML += "<td><div><button type='button' class='prioritySet0 btn btn-primary btn-xs'>set 0</button>";
            innerHTML += "<button type='button' class='prioritySet1 btn btn-primary btn-xs'>set 1</button>";
            innerHTML += "<button type='button' class='prioritySet2 btn btn-primary btn-xs'>set 2</button>";
            innerHTML += "<button type='button' class='prioritySet5 btn btn-primary btn-xs'>set 5</button>";
            innerHTML += "<button type='button' class='prioritySet10 btn btn-primary btn-xs'>set 10</button>";
            innerHTML += "</div></td>";

            innerHTML += "</tr>";

            innerHTML += "<tr id='"+index+"'>";

            innerHTML += "<td colspan=5 class='buttons_row'>";
            innerHTML += "<button type='button' class='maxAllBuildingsButton btn btn-primary btn-sm'>Build all to max level</button>";
            innerHTML += "<button type='button' class='buildingMenuButton btn btn-info btn-xs' data-toggle='modal' data-target='#buildingModal' style='margin-left:8px;'>Building menu<br/><div class='queueLengthMessage'>(<b>"+v.buildingQueue.length+"</b> in building queue)</div></button>";
            innerHTML += "</td>";

            innerHTML += "</tr>";
        }
        innerHTML += "</tbody>";

        $('#gameDataTable').html(innerHTML);
        $('[data-toggle="tooltip"]').tooltip();

        $('.prioritySet1').click(function() { setPriority($(this).closest('tr').attr('id'), 1); });
        $('.prioritySet2').click(function() { setPriority($(this).closest('tr').attr('id'), 2); });
        $('.prioritySet5').click(function() { setPriority($(this).closest('tr').attr('id'), 5); });
        $('.prioritySet10').click(function() { setPriority($(this).closest('tr').attr('id'), 10); });
        $('.maxAllBuildingsButton').click(function() { maxAllBuildings($(this).closest('tr').attr('id')); });
        $('.buildingMenuButton').click(function() { drawBuildingMenu($(this).closest('tr').attr('id')); });
    }

    function drawBuildingMenu(villageId) {
        var buildingModalContentInnerHtml = "<div><table><thead class='menuHeader' align='center'><tr><td>Bulding Queue</td><td>Build</td></tr></thead><tbody>" +
                "<tr id='"+villageId+"' ><td>"
        var buildingQueue = gameData.villages[villageId].buildingQueue;
        if (!jQuery.isEmptyObject(buildingQueue)) {
            buildingModalContentInnerHtml += "<ul class='buldingQueueList'>";
            for (var i = 0; i < buildingQueue.length; i++) {
                var buildingEntry = buildingQueue[i];
                var what;
                if (buildingEntry.what == null) {
                    what = "Resource field";
                } else {
                    what = buildingEntry.what;
                }
                buildingModalContentInnerHtml += "<li>"+what + " on " + buildingEntry.where + "</li>";
            }
            buildingModalContentInnerHtml += "</ul>";
        } else {
            buildingModalContentInnerHtml += "<b>Building Queue is Empty.</b>";
        }

        buildingModalContentInnerHtml += "</td><td>"
        buildingModalContentInnerHtml += "<button type='button' class='buildButton buildMainBuildingTo20Button btn btn-primary btn-xs'>Build main building to 20 level</button><br/>";
        buildingModalContentInnerHtml += "<button type='button' class='buildButton buildBarracksTo20Button btn btn-primary btn-xs'>Build barracks to 20 level</button><br/>";
        buildingModalContentInnerHtml += "<button type='button' class='buildButton buildStableTo20Button btn btn-primary btn-xs'>Build stable to 20 level</button><br/>";
        buildingModalContentInnerHtml += "</td>"

        buildingModalContentInnerHtml += "</td></tr></tbody></table></div>";


        $('#buildingModalContent').html( buildingModalContentInnerHtml );
        $('.buildMainBuildingTo20Button').click(function() { buildAtDorf2($(this).closest('tr').attr('id'), "MAIN_BUILDING", 20); });
        $('.buildStableTo20Button').click(function() { buildAtDorf2($(this).closest('tr').attr('id'), "STABLE", 20); });
        $('.buildBarracksTo20Button').click(function() { buildAtDorf2($(this).closest('tr').attr('id'), "BARRACKS", 20); });
    }

    function buildAtDorf2(villageId, what, level) {
        $.ajax({
            type: "GET",
            contentType : "application/json",
            url: ajaxUrl + "buildAtDorf2",
            data : {villageId: villageId,
                    what: what,
                    level: level},
            timeout : 100000,
            success : function(data) {
                console.log("SUCCESS: ", data);
            },
            error : function(e) {
                console.log("ERROR: ", e);
                display(e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function maxAllBuildings(villageId) {
        $.ajax({
            type: "GET",
            contentType : "application/json",
            url: ajaxUrl + "maxAllBuildings",
            data : {villageId: villageId},
            timeout : 100000,
            success : function(data) {
                console.log("SUCCESS: ", data);
            },
            error : function(e) {
                console.log("ERROR: ", e);
                display(e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

    function setPriority(villageId, priority) {
        $.ajax({
            type: "GET",
            contentType : "application/json",
            url: ajaxUrl + "changePriority",
            data : {villageId: villageId, priority: priority},
            timeout : 100000,
            success : function(data) {
                console.log("SUCCESS: ", data);
            },
            error : function(e) {
                console.log("ERROR: ", e);
                display(e);
            },
            done : function(e) {
                console.log("DONE");
            }
        });
    }

</script>

</html>