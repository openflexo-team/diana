class CreateChange extends Change {
    private createdNode: MyNode;
    private parentId: number;
    
    public deserialize(input: any): CreateChange {
        this.createdNode = MyNode.deserialize(input.created);
        if(input.parent !== undefined) {
            this.parentId = input.parent;
        }
        return this;
    }
    
    public apply(drawing: Drawing): void {
        drawing.addNode(this.parentId, this.createdNode);
    }
}