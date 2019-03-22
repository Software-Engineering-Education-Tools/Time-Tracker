var Visualizer = Visualizer || {};

Visualizer.Tabbar = (function () {
    var that = new Visualizer.EventTarget(),
        element;

    function init(barElement) {
        element = barElement;
        element.addEventListener("click", onIconbarClicked);
    }

    function onIconbarClicked(event) {
        let navEvent = new Event("navClicked"),
            activeElement = document.querySelector(".active");

        navEvent.classList = event.target.classList;
        that.dispatchEvent(navEvent);

        activeElement.classList.remove("active");
    }

    that.init = init;
    return that;
}());