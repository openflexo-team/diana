class PropertyChangeEvent {
    private source: any;
    private propertyName: string;
    private oldValue: any;
    private newValue: any;
    
    public constructor (source: any, propertyName: string, oldValue: any, newValue: any) {
        this.source = source;
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    public getSource(): any {
        return this.source;
    }
    
    public getPropertyName(): string {
        return this.propertyName;
    }
    
    public getOldValue(): any {
        return this.oldValue;
    }
    
    public getNewValue(): any {
        return this.newValue;
    }
}