class Vertx {
    static EVENTBUS:any; //TODO: make/find a TS definition for the event bus

    //could be a dictionnary of eventBus
    //then a parameter would allow to access a given bus
    static eventBus() {
        if (Vertx.EVENTBUS === undefined) {
            //create a event bus bridge to the server that served this file
            Vertx.EVENTBUS = new vertx.EventBus(window.location.protocol + '//'
                + window.location.hostname + ':' + window.location.port + '/eventbus');
        }
        return Vertx.EVENTBUS;
    }
}