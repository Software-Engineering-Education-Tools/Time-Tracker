var Visualizer = Visualizer || {};

Visualizer.GraphView = (function () {
    var that = new Visualizer.EventTarget(),
        view,
        content,
        dataGetter,
        targetElement,
        hourlyLabels = ["0 - 1",
            "0 - 1",
            "1 - 2",
            "2 - 3",
            "3 - 4",
            "4 - 5",
            "5 - 6",
            "6 - 7",
            "7 - 8",
            "8 - 9",
            "9 - 10",
            "10 - 11",
            "11 - 12",
            "12 - 13",
            "13 - 14",
            "14 - 15",
            "15 - 16",
            "16 - 17",
            "17 - 18",
            "18 - 19",
            "19 - 20",
            "20 - 21",
            "21 - 22",
            "22 - 23",
            "23 - 24"],
        dailyLabels = ["Sonntag", "Montag", "Dienstag", "Mittwoch", "Donnerstag", "Freitag", "Samstag"],
        chartMode = false,
        dataDaily,
        dataHourly;

    function init(mainView, getter) {
        view = mainView;
        dataGetter = getter;
        dataGetter.addEventListener("graphData", onGraphDataAvailable);

        content = createElement("#template-graph");
        content.querySelector(".info-button").addEventListener('click', toggleHelpBox);
        content.querySelector(".info").addEventListener('click', toggleHelpBox);

        targetElement = content.querySelector("#graphcontainer");
        content.querySelector(".graph-button").addEventListener('click', toggleGraph);
    }

    function toggleGraph() {
        if (chartMode) {
            chartMode = false;
            drawChart(dataDaily, "Aktivität an Wochentag", dailyLabels);
        } else {
            chartMode = true;
            drawChart(dataHourly, "Aktivität zu Tageszeit", hourlyLabels);
        }
    }

    function onGraphDataAvailable(event) {
        updateUI(event.payload);
    }

    function toggleHelpBox() {
        content.querySelector(".info").classList.toggle("hidden");
    }

    function createElement(templateSelector) {
        let divElement = document.createElement("div");

        divElement.innerHTML = document.querySelector(templateSelector).innerHTML.trim();

        return divElement.firstChild;
    }

    function setActiveElement() {
        document.querySelector(".button-graph").classList.add("active");
    }

    function render() {
        while
            (view.firstChild) {
            view.removeChild(view.firstChild);
        }
        setActiveElement();
        view.appendChild(content);
    }

    function update() {
        render();
        dataGetter.getGraphData();
    }

    function updateUI(payload) {
        dataHourly = [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0];
        dataDaily = [0, 0, 0, 0, 0, 0, 0];
        for (const val in payload.dataHourly) {
            dataHourly[payload.dataHourly[val].time] = payload.dataHourly[val].loc;
        }
        for (const val in payload.dataDaily) {
            dataDaily[payload.dataDaily[val].time] = payload.dataDaily[val].loc;
        }

        drawChart(dataDaily, "Aktivität an Wochentag", dailyLabels);
    }

    function drawChart(data, title, labels,) {
        var myChart = new Chart(targetElement, {
                type: 'bar',
                data: {
                    labels: labels,
                    datasets: [{
                        label: title,
                        data: data,
                        backgroundColor: [
                            'rgba(11, 102, 35, 0.2)',
                            'rgba(112, 130, 56, 0.2)',
                            'rgba(63, 122, 77, 0.2)',
                            'rgba(143, 151, 121, 0.2)',
                            'rgba(41, 171, 135, 0.2)',
                            'rgba(138, 154, 91, 0.2)',
                            'rgba(11, 102, 35, 0.2)',
                            'rgba(112, 130, 56, 0.2)',
                            'rgba(63, 122, 77, 0.2)',
                            'rgba(143, 151, 121, 0.2)',
                            'rgba(41, 171, 135, 0.2)',
                            'rgba(138, 154, 91, 0.2)',
                            'rgba(11, 102, 35, 0.2)',
                            'rgba(112, 130, 56, 0.2)',
                            'rgba(63, 122, 77, 0.2)',
                            'rgba(143, 151, 121, 0.2)',
                            'rgba(41, 171, 135, 0.2)',
                            'rgba(138, 154, 91, 0.2)',
                            'rgba(11, 102, 35, 0.2)',
                            'rgba(112, 130, 56, 0.2)'
                        ],
                        borderColor: [
                            'rgba(11, 102, 35, 1)',
                            'rgba(112, 130, 56, 1)',
                            'rgba(63, 122, 77, 1)',
                            'rgba(143, 151, 121, 1)',
                            'rgba(41, 171, 135, 1)',
                            'rgba(138, 154, 91, 1)',
                            'rgba(11, 102, 35, 1)',
                            'rgba(112, 130, 56, 1)',
                            'rgba(63, 122, 77, 1)',
                            'rgba(143, 151, 121, 1)',
                            'rgba(41, 171, 135, 1)',
                            'rgba(138, 154, 91, 1)',
                            'rgba(11, 102, 35, 1)',
                            'rgba(112, 130, 56, 1)',
                            'rgba(63, 122, 77, 1)',
                            'rgba(143, 151, 121, 1)',
                            'rgba(41, 171, 135, 1)',
                            'rgba(138, 154, 91, 1)',
                            'rgba(11, 102, 35, 1)',
                            'rgba(112, 130, 56, 1)'
                        ],
                        borderWidth: 1
                    }]
                },
                options: {
                    scales: {
                        yAxes: [{
                            ticks: {
                                beginAtZero: true
                            }
                        }]
                    }
                }
            })
        ;
    }

    that.init = init;
    that.update = update;
    return that;
}());