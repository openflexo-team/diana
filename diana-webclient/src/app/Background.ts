/// <reference path="Deserializable.ts" />

class Background implements Deserializable<Background> {
    private red: number;
    private green: number;
    private blue: number;
    
    deserialize(input: any): Background {
        this.red = input.red;
        this.green = input.green;
        this.blue = input.blue;
        
        return this;
    }
    
    public getRed(): number {
        return this.red;
    }
    
    public getGreen(): number {
        return this.green;
    }
    
    public getBlue(): number {
        return this.blue;
    }
    
    public toAttribute(): string {
        return "rgb(" + this.getRed() + ", " + this.getGreen() + ", " + this.getBlue() + ")";
    }
    
    public print() : void {
        console.log("Ce BG : " + this.getRed() + ", " + this.getGreen() + ", " + this.getBlue());
    }
}