/// <reference path="Deserializable.ts" />

class Background implements Deserializable<Background> {
    public static deserialize(input: any): Background {
        return new Background().deserialize(input);
    }
    
    private red: number;
    private green: number;
    private blue: number;
    
    public deserialize(input: any): Background {
        this.red = input.red;
        this.green = input.green;
        this.blue = input.blue;
        
        return this;
    }
    
    public constructor() {
        this.red = 255;
        this.green = 255;
        this.blue = 255;
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
}