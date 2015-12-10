/// <reference path="Drawing.ts" />

window.onload = function() {
    console.log("__ start main __");
    var drawing : Drawing = new Drawing();
    drawing.setupCommunication();
    console.log("__ end main __");
};