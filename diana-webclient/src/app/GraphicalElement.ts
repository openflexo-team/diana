/// <reference path="MyNode.ts" />

class GraphicalElement {
	public native: Snap.Element;
    public dtn: MyNode;
    
    public constructor(aDtn : MyNode, sElement: Snap.Element) {
        this.dtn = aDtn;
        this.native = sElement;
    }
    
    public getNode(): MyNode {
        return this.dtn;
    }
}