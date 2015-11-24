/*/// <reference path="Change.ts" />

class CreateChange extends Change {
    private createdNode: MyNode;
    private parentId: number;
    
    public deserialize(input: any): CreateChange {
        this.createdNode = MyNode.deserialize(input.created);
        if(input.parent !== undefined) {
            this.parentId = input.parent;
        }
    }
    
    public apply(drawing: Drawing): void {
        drawing.getNode(parentId).addChild(createdNode);
    }
}*/