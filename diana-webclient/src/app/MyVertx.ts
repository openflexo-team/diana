class MyVertx {
    static EVENTBUS: Vertx.EventBus; //TODO: make/find a TS definition for the event bus

    //could be a dictionnary of eventBus
    //then a parameter would allow to access a given bus
    static eventBus() {
        if (MyVertx.EVENTBUS === undefined) {
            //create a event bus bridge to the server that served this file
            MyVertx.EVENTBUS = new vertx.EventBus(window.location.protocol + '//'
                + window.location.hostname + ':' + window.location.port + '/eventbus');
        }
        return MyVertx.EVENTBUS;
    }
}