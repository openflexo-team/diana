/// <reference path="MyNode.ts" />
/// <reference path="Deserializable.ts" />

class RootNode extends MyNode implements Deserializable<RootNode> {
    
    deserialize(input:any) : RootNode {
        super.deserialize(input);
        return this;
    }
    
    public createSVG(s: Snap.Paper = null) : void {
        s = Snap('svg');
        this.graphic = s;
        super.createSVG(s);
    }
    
    public print(depth: number = 0): void {
        var toPrint: string = this.getId().toString();
        toPrint = toPrint + ' : I am Root';
        for(var i: number = 0; i < depth; i++) {
            toPrint = '   ' + toPrint;
        }
        console.log(toPrint);

        for(var child of this.getChildren()) {
            child.print(depth + 1);
        }
    }
}