var Visualizer = Visualizer || {};

Visualizer.Helper = (function () {
    const MONTHS = ['JAN', 'FEB', 'MAR', 'APR', 'MAI', 'JUN', 'JUL', 'AUG', 'SEP', 'OKT', 'NOV', 'DEZ'];
    var that = {};

    function DateWrapper(date) {
        this.date = date;

    }

    DateWrapper.prototype.getDate = function () {
        return this.date;
    };

    DateWrapper.prototype.getYear = function () {
        return this.date.getFullYear();
    };

    DateWrapper.prototype.getMonth = function () {
        return this.date.getMonth()+1;
    };

    DateWrapper.prototype.getMonthShortName = function () {
        return MONTHS[this.date.getMonth()];
    };

    DateWrapper.prototype.getDay = function () {
        return this.date.getDate();
    };

    DateWrapper.prototype.getDifference = function (date) {

    };

    DateWrapper.prototype.getDateElement = function (additionalClass) {
        let div = document.createElement("div");

        div.innerHTML =  document.querySelector("#template-date").innerHTML.trim();

        div.querySelector(".month").innerHTML = this.getMonthShortName();
        div.querySelector(".day").innerHTML = this.getDay();
        div.querySelector(".year").innerHTML = this.getYear();

        div.firstChild.classList.add(additionalClass);
        return div.firstChild;
    };

    function clearElementFromChildren(element) {
        while (element.firstChild) {
            element.removeChild(element.firstChild);
        }
    }

    function log(message) {
        let data = {message: message};
        $.ajax({
            type: "POST", crossDomain: true, url: "http://localhost:8080/log", data: JSON.stringify(data), success: function () {
                console.log("Sent");
            }, error: function (x, a, r) {
                console.log(x);
                console.log(a);
                console.log(r);
                console.log("ERROR");
            }, contentType: "application/json"
        });
    }

    function getWeekday(day) {
        switch(day) {
            case "0":
                return "Sonntag";
            case "1":
                return "Montag";
            case "2":
                return "Dienstag";
            case "3":
                return "Mittwoch";
            case "4":
                return "Donnerstag";
            case "5":
                return "Freitag";
            case "6":
                return "Samstag";
            default:
                return "Sonntag";
        }
    }

    that.getWeekday = getWeekday;
    that.DateWrapper = DateWrapper;
    that.clearElementFromChildren = clearElementFromChildren;
    that.log = log;

    return that;
}());