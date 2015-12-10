class RootGraphicalElement extends GraphicalElement {
    public constructor(aDtn : MyNode) {
        super(aDtn);
    }
    
    public createSVG() {
        this.paper = Snap('100%', '100%');
        this.native = this.paper;
    }
    
    public propertyChange(event: PropertyChangeEvent): void {
        return;
    }
    
    public getNative():Snap.Paper {
        return <Snap.Paper>super.getNative();
    }
}