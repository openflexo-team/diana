class RectangleGraphicalElement extends ShapeGraphicalElement {
    
    public constructor(aDtn : MyNode, s: Snap.Paper) {
        super(aDtn, s);
    }
    
    public createSVG(paper?: Snap.Paper) {
        this.native = this.paper.rect(0, 0, this.getNode().getWidth(), this.getNode().getHeight());
        super.createSVG();
    }
    
    public createControlPoints(): void {
        var cPointsDescription = [{'relX': 0.5, 'relY': 0, 'type': 'NORTH'},
                                    {'relX': 0.5, 'relY': 1, 'type': 'SOUTH'},
                                    {'relX': 0, 'relY': 0.5, 'type': 'WEST'},
                                    {'relX': 1, 'relY': 0.5, 'type': 'EAST'},
                                    {'relX': 0, 'relY': 0, 'type': 'NORTHWEST'},
                                    {'relX': 1, 'relY': 0, 'type': 'NORTHEAST'},
                                    {'relX': 0, 'relY': 1, 'type': 'SOUTHWEST'},
                                    {'relX': 1, 'relY': 1, 'type': 'SOUTHEAST'}];
        for(var cPoint of cPointsDescription) {
            this.getControlPoints().push(new ControlPoint(this.getNode(), this.getPaper(), cPoint.relX, cPoint.relY, cPoint.type));
        }
    }
}