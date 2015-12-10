/**
 * Encapsulates a graphical element from the SnapSVG library.
 */
abstract class GraphicalElement implements PropertyChangeListener {
    public paper: Snap.Paper;
	public native: Snap.Element;
    public dtn: MyNode;
    
    public constructor(aDtn : MyNode, paper?: Snap.Paper) {
        this.dtn = aDtn;
        this.dtn.getPropertyChangeSupport().addPropertyChangeListener(this);
        this.paper = paper;
    }
    
    public abstract createSVG(): void;
    
    public deleteSVG(): void {
        this.native.remove();
    }
    
    public selectChange(): void {
        return;
    }
    
    public abstract propertyChange(event: PropertyChangeEvent): void;
    
    public getNode(): MyNode {
        return this.dtn;
    }
    
    public getNative(): Snap.Element {
        return this.native;
    }
    
    public getPaper(): Snap.Paper {
        if(this.paper === undefined) {
            console.log(this);
            this.paper = this.getNode().getParent().getGraphic().getPaper();
        }
        return this.paper;
    }
}