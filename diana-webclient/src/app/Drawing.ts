/// <reference path="MyVertx.ts" />
/// <reference path="RootNode.ts" />

/**
 * Client-side representation of a drawing.<br />
 * 
 * Contains a tree-like structure similar to the server-side Drawing, and keeps them synchronized.
 * Communicates with the server through the Vertx eventbus.
 */
class Drawing {
    private rootNode: RootNode;
    private eventBus: Vertx.EventBus;
    private nodes: Array<MyNode>;
    private selectedNodes: Array<MyNode>;
    //private handlers: Array<Vertx.Handler>;
    
    public constructor() {
        this.nodes = new Array<MyNode>();
        this.selectedNodes = new Array<MyNode>();
        console.log("a new Drawing");
    }
    
    public setRootNode(aRootNode: RootNode): void {
        this.rootNode = aRootNode;
    }
    
    public setupCommunication() : void {
        var drawing = this;
        var messageHandler: Vertx.Handler = function(message: any) {
            console.log('received a reply: ' + JSON.stringify(message));
            var change: Change = Change.deserialize(message);
            change.apply(drawing);
        };
        
        this.eventBus = MyVertx.eventBus();
        var eb: Vertx.EventBus = this.eventBus;
        eb.onopen = function() {
            eb.registerHandler('org.openflexo.server.Server.announcements',
                    function(message: any) {
                        console.log('received a message: ' + JSON.stringify(message));
                    });
            
            eb.registerHandler('server.changes', messageHandler);
            //eb.send('some-address', {name: 'tim', age: 587});
            eb.publish('org.openflexo.server.Server.announcements', 'TS client here, bus opened');
            
            //get representation of the current Drawing
            eb.send('client.query', 'new client', messageHandler);
        };
    }
    
    public getNode(id: number): MyNode {
        return this.nodes[id];
    }
    
    public addNode(parentId: number, node: MyNode) {
        if(parentId === undefined) {
            this.setRootNode(<RootNode>node);
            node.setDrawing(this);
        }
        else {
            this.getNode(parentId).addChildNode(node);
        }
        node.createGraphic();
    }
    
    public select(selectedNode: MyNode):void {
        for(var node of this.selectedNodes) {
            if(node !== selectedNode) {
                (<MyNode>node).setSelected(false);
            }
            else {
                var alreadySelected: boolean = true;
            }
        }
        if(!alreadySelected) {
            selectedNode.setSelected(true);
        }
        this.selectedNodes = new Array<MyNode>();
        this.selectedNodes.push(selectedNode);
    }
    
    public deselectAll(): void {
        console.log("deselect");
        for(var node of this.selectedNodes) {
            (<MyNode>node).setSelected(false);
        }
    }
    
    public refNode(node: MyNode) {
        this.nodes[node.getId()] = node;
    }
    
    public publishChange(change: UpdateChange) {
        var json: any = change.toJson();
        console.log("sending : " + JSON.stringify(json));
        this.eventBus.publish("client.changes", change.toJson());
    }
}