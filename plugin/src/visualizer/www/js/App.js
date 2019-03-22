var Visualizer = Visualizer || {};

Visualizer.App = (function () {
    var that = {},
        dataGetter = Visualizer.DataGetter,
        tabbar = Visualizer.Tabbar,
        dashboardView = Visualizer.DashboardView,
        projectView = Visualizer.ProjectView,
        graphView = Visualizer.GraphView;

    function init() {
        let tabbarElement = document.querySelector(".icon-bar");
        dataGetter.init(function () {
            tabbar.init(tabbarElement);
            tabbar.addEventListener("navClicked", onNavClicked);
            let targetElement = document.querySelector(".target");
            projectView.init(targetElement, dataGetter);
            dashboardView.init(targetElement, dataGetter);
            graphView.init(targetElement, dataGetter);
            dashboardView.addEventListener("projectClicked", onProjectClicked);
            dashboardView.update();
        });
    }

    function onNavClicked(event) {
        if (event.classList.contains("button-dashboard")) {
            dashboardView.update();
        } else if (event.classList.contains("button-project")) {
            projectView.update("current");
        } else if (event.classList.contains("button-graph")) {
            graphView.update();
        }
    }

    function onProjectClicked(event) {
        projectView.update(event.project);
    }

    that.init = init;
    return that;
}());

Visualizer.App.init();