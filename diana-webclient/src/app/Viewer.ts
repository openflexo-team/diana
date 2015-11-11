/// <reference path="Vertx.ts" />
/// <reference path="MyNode.ts" />

class Viewer {
    private rootNode : MyNode; //TODO: encapsulate in a Drawing class
    private eventBus;
    
    public constructor() {
        console.log("a new viewer");
    }
    
    public setupCommunication() : void {
            this.eventBus = Vertx.eventBus();
            var eb = this.eventBus;
            eb.onopen = function() {
                eb.registerHandler('org.openflexo.server.Server.announcements',
                        function(message: any) {
                            console.log('received a message: ' + JSON.stringify(message));
                        });
                
                //eb.send('some-address', {name: 'tim', age: 587});
                eb.publish('org.openflexo.server.Server.announcements', 'TS client here, bus opened');
                
                //get representation of the current Drawing
                eb.send('client.query', 'new client',
                        function(message: any) {
                            console.log('received a reply: ' + JSON.stringify(message));
                            this.rootNode = <RootNode>MyNode.deserialize(message);
                            this.rootNode.print();
                            this.rootNode.createSVG();
                        });
            };
        }
}