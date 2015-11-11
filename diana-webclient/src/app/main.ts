/// <reference path="Viewer.ts" />

window.onload = function() {
    console.log("__ start main __");
    var viewer : Viewer = new Viewer();
    viewer.setupCommunication();
    console.log("__ end main __");
};