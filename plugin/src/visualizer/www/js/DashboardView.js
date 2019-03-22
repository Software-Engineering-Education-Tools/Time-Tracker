var Visualizer = Visualizer || {};

Visualizer.DashboardView = (function () {
    var that = new Visualizer.EventTarget(),
        view,
        content,
        dataGetter,
        timeElementCircle,
        locElementCircle,
        numProjectsElementCircle;

    function init(mainView, getter) {

        view = mainView;
        dataGetter = getter;
        dataGetter.addEventListener("dashboardData", onDashboardDataAvailable);
        content = createElement("#template-dashboard");
        content.querySelector(".info-button").addEventListener('click', toggleHelpBox);
        content.querySelector(".info").addEventListener('click', toggleHelpBox);
        content.querySelector(".data-projectlist").addEventListener(('click'), onProjectListClicked);

        render();
    }

    function onProjectListClicked(event) {
        let projectListEvent = new Event("projectClicked");
        projectListEvent.project = event.target.innerText;
        that.dispatchEvent(projectListEvent);
    }

    function toggleHelpBox() {
        content.querySelector(".info").classList.toggle("hidden");
    }

    function onDashboardDataAvailable(event) {
        updateUI(event.payload);
    }

    function createElement(templateSelector) {
        let divElement = document.createElement("div");

        divElement.innerHTML = document.querySelector(templateSelector).innerHTML.trim();

        return divElement.firstChild;
    }


    function setActiveElement() {
        document.querySelector(".button-dashboard").classList.add("active");
    }

    function render() {
        while (view.firstChild) {
            view.removeChild(view.firstChild);
        }
        setActiveElement();

        destroyCircles();

        view.appendChild(content);
    }

    function update() {
        render();
        dataGetter.getDashboardData();
    }

    function destroyCircles() {
        if (timeElementCircle && locElementCircle && numProjectsElementCircle) {
            timeElementCircle.destroy();
            locElementCircle.destroy();
            numProjectsElementCircle.destroy();
        }
    }

    function generateCircle(target, displayValue) {
        let circleShape = new ProgressBar.Circle(target, {
            color: '#000',
            // This has to be the same size as the maximum width to
            // prevent clipping
            strokeWidth: 4,
            fill: '#FFFFFF',
            trailWidth: 1,
            easing: 'easeInOut',
            duration: 1400,
            text: {
                autoStyleContainer: true
            },
            from: {color: ' #8B0000', width: 1},
            to: {color: '#758E4F', width: 4},
            // Set default step function for all animate calls
            step: function (state, circle) {
                circle.path.setAttribute('stroke', state.color);
                circle.path.setAttribute('stroke-width', state.width);

                circle.setText(displayValue);
            }
        });
        circleShape.text.style.fontFamily = '"Consolas", Monaco, sans-serif';
        circleShape.text.style.fontSize = '7.5vw';
        circleShape.animate(1.0);  // Number from 0.0 to 1.0
        return circleShape;
    }

    function updateUI(payload) {
        let timeframeElement = content.querySelector(".timeframe"),
            timeElement = content.querySelector(".data-time"),
            locElement = content.querySelector(".data-locs"),
            numProjectsElement = content.querySelector(".data-numprojects"),
            projectList = content.querySelector(".data-projectlist"),
            dateDivider = document.createElement("span"),
            startTime = new Date(payload.dates.minTime),
            startTimeWrapper = new Visualizer.Helper.DateWrapper(startTime),
            endTime = new Date(payload.dates.maxTime),
            endTimeWrapper = new Visualizer.Helper.DateWrapper(endTime),
            timeDiscrepancy = endTime - startTime,
            diff = new moment.duration(timeDiscrepancy).asDays();

        timeElementCircle = generateCircle(timeElement, diff);
        locElementCircle = generateCircle(locElement, payload.lines);
        numProjectsElementCircle = generateCircle(numProjectsElement, payload.numProjects); //TODO: Put into Helper

        Visualizer.Helper.clearElementFromChildren(projectList);
        Visualizer.Helper.clearElementFromChildren(timeframeElement);


        dateDivider.classList.add("dateDivider");
        dateDivider.innerText = "bis";
        timeframeElement.appendChild(startTimeWrapper.getDateElement("start"));
        timeframeElement.appendChild(dateDivider);
        timeframeElement.appendChild(endTimeWrapper.getDateElement("end"));

        for (let project in payload.projects) {
            let element = document.createElement('li');
            element.innerText = payload.projects[project].project; //TODO: GEt Name from config
            element.classList.add('data-project');
            projectList.appendChild(element);
        }
    }

    that.init = init;
    that.update = update;
    return that;
}());