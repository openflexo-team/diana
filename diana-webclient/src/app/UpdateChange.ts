class UpdateChange extends Change {
    private nodeId: number;
    private changedProperty: string;
    private newValue: any;
    
    public constructor(nodeId?: number, changedProperty?: string, newValue?: number) {
        super();
        this.nodeId = nodeId;
        this.changedProperty = changedProperty;
        this.newValue = newValue;
    }
    
    public apply(drawing: Drawing): void {
        var node: MyNode = drawing.getNode(this.nodeId);
        if(this.changedProperty === "x") {
            (<ShapeNode>node).setX(this.newValue);
        }
        else if(this.changedProperty === "y") {
            (<ShapeNode>node).setY(this.newValue);
        }
    }
    
    public toJson(): any {
        var returned: any = {"type": "UPDATE",
                                "updated": this.nodeId,
                                "changedProperty": this.changedProperty,
                                "newValue": this.newValue};
        return returned;
    }
    
    public deserialize(input: any): UpdateChange {
        this.nodeId = input.updated;
        this.changedProperty = input.changedProperty;
        this.newValue = input.newValue;
        return this;
    }
}