/// <reference path="MyNode.ts"/>

class Change implements Deserializable<Change> {
    static deserialize(input: any): Change {
        if(input.type !== undefined){
            if(input.type === "CREATE") {
                return new Change().deserialize(input);
            }
        }
    }
    
    private createdNode: MyNode;
    private parentId: number;
    
    public deserialize(input: any): Change {
        this.createdNode = MyNode.deserialize(input.created);
        if(input.parent !== undefined) {
            this.parentId = input.parent;
        }
        console.log("change deserialized");
        return this;
    }
    
    public apply(drawing: Drawing): void {
        drawing.addNode(this.parentId, this.createdNode);
        this.createdNode.createSVG();
    }
}