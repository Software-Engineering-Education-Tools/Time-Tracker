var Visualizer = Visualizer || {};

Visualizer.ProjectView = (function () {
    var that = new Visualizer.EventTarget(),
        view,
        content,
        dataGetter,
        timeElementCircle,
        locElementCircle,
        compilesElementCircle;

    function init(mainView, getter) {
        view = mainView;
        dataGetter = getter;

        dataGetter.addEventListener("projectData", onProjectDataAvailable);

        content = createElement("#template-project");
    }

    function onProjectDataAvailable(event) {
        console.log(event);
        if (event.payload.data === "none") {
            updateUIProjectUnavailable();
        } else {
            updateUIProjectAvailable(event.payload);
        }
    }


    function createElement(templateSelector) {
        let divElement = document.createElement("div");

        divElement.innerHTML = document.querySelector(templateSelector).innerHTML.trim();
        document.querySelector(".target").innerHTML = divElement.firstChild;

        return divElement.firstChild;
    }

    function setActiveElement() {
        document.querySelector(".button-project").classList.add("active");
    }

    function toggleHelpBox() {
        content.querySelector(".info").classList.toggle("hidden");
    }

    function render() {
        while (view.firstChild) {
            view.removeChild(view.firstChild);
        }
        setActiveElement();

        destroyCircles();

        view.appendChild(content);
    }

    function update(project) {
        render();
        dataGetter.getProjectData(project);
    }

    function destroyCircles() {
        if (timeElementCircle && locElementCircle && compilesElementCircle) {
            timeElementCircle.destroy();
            locElementCircle.destroy();
            compilesElementCircle.destroy();
        }
    }

    function generateCircle(target, displayValue, displayValueCompiles) {
        let circle = new ProgressBar.Circle(target, {
            color: '#aaa',
            // This has to be the same size as the maximum width to
            // prevent clipping
            strokeWidth: 4,
            trailWidth: 1,
            easing: 'easeInOut',
            duration: 1400,
            text: {
                autoStyleContainer: true
            },
            from: {color: ' #8B0000', width: 1},
            to: {color: '#4CAF50', width: 4},
            // Set default step function for all animate calls
            step: function (state, circle) {
                circle.path.setAttribute('stroke', state.color);
                circle.path.setAttribute('stroke-width', state.width);
                if (displayValueCompiles) {
                    if (circle.value() > displayValue / displayValueCompiles) {
                        circle.stop();
                    }
                    circle.setText(displayValue + "/" + displayValueCompiles);
                } else {
                    circle.setText(displayValue);
                }
            }
        });

        circle.text.style.fontFamily = '"Consolas", Monaco, sans-serif';
        circle.text.style.fontSize = '5vw';
        circle.animate(1.0);  // Number from 0.0 to 1.0

        return circle;
    }

    function updateUIProjectAvailable(payload) {
        let titleElement = document.querySelector(".title"),
            descriptionShortElement = document.querySelector(".description-short"),
            descriptionLongElement = document.querySelector(".description-long"),
            timeElement = document.querySelector(".data-time"),
            timeframeElement = content.querySelector(".timeframe"),
            locElement = document.querySelector(".data-locs"),
            compilesElement = document.querySelector(".data-compiles"),
            tagListElement = document.querySelector(".data-tags"),
            fileListelement = document.querySelector(".data-filelist"),
            dateDivider = document.createElement("span"),
            startTime = new Date(payload.config.startDate),
            startTimeWrapper = new Visualizer.Helper.DateWrapper(startTime),
            endTime = new Date(payload.config.endDate),
            endTimeWrapper = new Visualizer.Helper.DateWrapper(endTime),
            timeDiscrepancy = endTime - startTime,
            diff = new moment.duration(timeDiscrepancy).asDays(),
            totalCompiles = payload.compiles.fails + payload.compiles.successes;

        console.log(payload.config);
        timeElementCircle = generateCircle(timeElement, diff);
        locElementCircle = generateCircle(locElement, payload.lines);
        compilesElementCircle = generateCircle(compilesElement, payload.compiles.successes, totalCompiles);

        titleElement.innerHTML = payload.config.project + '<i class="info-button fas fa-info-circle"></i>';

        document.querySelector(".info-button").addEventListener('click', toggleHelpBox);
        content.querySelector(".info").addEventListener('click', toggleHelpBox);

        descriptionShortElement.innerHTML = payload.config.shortDescription;
        descriptionLongElement.innerHTML = payload.config.longDescription;
        timeframeElement.innerHTML = payload.dates.minTime + " bis " + payload.dates.maxTime;


        Visualizer.Helper.clearElementFromChildren(timeframeElement);
        Visualizer.Helper.clearElementFromChildren(fileListelement);
        Visualizer.Helper.clearElementFromChildren(tagListElement);


        dateDivider.classList.add("dateDivider");
        dateDivider.innerText = "bis";
        timeframeElement.appendChild(startTimeWrapper.getDateElement("start"));
        timeframeElement.appendChild(dateDivider);
        timeframeElement.appendChild(endTimeWrapper.getDateElement("end"));

        for (let file in payload.files) {
            let element = document.createElement('li');
            element.innerText = payload.files[file].file + ", Edits:" + payload.files[file].edits;
            fileListelement.appendChild(element);
        }

        for (let tag in payload.config.tags) {
            let element = document.createElement('li');
            element.innerText = payload.config.tags[tag];
            tagListElement.appendChild(element);
        }
    }

    function updateUIProjectUnavailable() {
        let titleElement = document.querySelector(".title"),
            hideHelperElement = document.querySelector(".hideHelper"),
            infoElement = document.querySelector(".info");

        titleElement.innerText = "Kein Projekt";
        hideHelperElement.classList.add("hidden");
        infoElement.classList.remove("hidden");
        infoElement.innerText = "Das ausgewählte Projekt hat keine Konfigurationsdatei. Dies bedeutet entweder dass " +
            "das ausgewählte Projekt nicht Teil eines Kurses ist oder du noch keine Änderungen am Code vorgenommen hast. " +
            "Wenn du magst, kannst du allerdings selbst eine Konfigurationsdatei erstellen."
    }

    that.init = init;
    that.update = update;
    return that;
}());