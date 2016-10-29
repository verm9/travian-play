<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<html>
<head>
    <link rel="stylesheet" href="webjars/bootstrap/3.3.6/css/bootstrap.min.css">
    <script type="text/javascript" src="webjars/jquery/2.2.4/jquery.min.js"></script>
    <script type="text/javascript" src="webjars/bootstrap/3.3.6/js/bootstrap.min.js"></script>
    <script type="text/javascript" src="webjars/datatables/1.10.12/js/jquery.dataTables.min.js"></script>
    <script type="text/javascript" src="webjars/noty/2.3.8/js/noty/packaged/jquery.noty.packaged.min.js"></script>
</head>
<body>
<h1>Travian-play testing</h1>
<button type="button" class="btn btn-success btn-lg" id="startButton">Start!</button>
<table id="gameDataTable">
    <tbody>
    <tr>
        <td>

        </td>
    </tr>
    </tbody>
</table>
<h2>${gameData}</h2>
</body>

<script type="text/javascript">

    var ajaxUrl = '/ajax/';
    var gameData;

    $(function () {

        // Get gameData
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

        $("#startButton").click();

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
        var gameDataTable = $('#gameDataTable');
        var villages = gameData.villages;
        var innerHTML = "";
        villages.forEach(function(item, i) {
            
        });
    }

</script>

</html>