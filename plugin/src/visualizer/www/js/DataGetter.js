var Visualizer = Visualizer || {};

Visualizer.DataGetter = (function () {
    var that = new Visualizer.EventTarget(),
    connection;

    function init(callback) {
        connection = new WebSocket('ws://127.0.0.1:8888');

        connection.onopen = function () {
            callback();
        };

// Log errors
        connection.onerror = function (error) {
            console.log('WebSocket Error ' + error);
        };

// Log messages from the server
        connection.onmessage = function (e) {
            let data = JSON.parse(e.data),
                event = new Event(data.type);

            event.payload = data.payload;
            that.dispatchEvent(event);
        };
    }

    function getDashboardData() {
        connection.send('{type: "dashboardData", payload: ""}');
    }

    function getProjectData(project) {
        connection.send('{type: "projectData", payload: "' + project + '"}');
    }

    function getGraphData() {
        connection.send('{type: "graphData", payload: ""}');
    }

    function log(data) {
        connection.send(data);
    }
    that.init = init;
    that.getDashboardData = getDashboardData;
    that.getGraphData = getGraphData;
    that.getProjectData = getProjectData;
    return that;
}());