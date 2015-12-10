/// <reference path="MyNode.ts"/>

abstract class Change implements Deserializable<Change> {
    static deserialize(input: any): Change {
        if(input.type !== undefined){
            if(input.type === "CREATE") {
                return new CreateChange().deserialize(input);
            }
            else if(input.type === "UPDATE") {
                return new UpdateChange().deserialize(input);
            }
        }
    }
    
    public abstract apply(drawing: Drawing): void;
    
    public abstract deserialize(input: any): Change;
}