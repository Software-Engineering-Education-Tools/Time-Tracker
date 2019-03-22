var Visualizer = Visualizer || {};

Visualizer.EventTarget = function() {
    this.listeners = {};
};

Visualizer.EventTarget.prototype.listeners = null;
Visualizer.EventTarget.prototype.addEventListener = function(type, callback) {
    if (!(type in this.listeners)) {
        this.listeners[type] = [];
    }
    this.listeners[type].push(callback);
};

Visualizer.EventTarget.prototype.removeEventListener = function(type, callback) {
    if (!(type in this.listeners)) {
        return;
    }
    var stack = this.listeners[type];
    for (var i = 0, l = stack.length; i < l; i++) {
        if (stack[i] === callback){
            stack.splice(i, 1);
            return;
        }
    }
};

Visualizer.EventTarget.prototype.dispatchEvent = function(event) {
    if (!(event.type in this.listeners)) {
        return true;
    }
    var stack = this.listeners[event.type].slice();

    for (var i = 0, l = stack.length; i < l; i++) {
        stack[i].call(this, event);
    }
    return !event.defaultPrevented;
};