class PropertyChangeSupport {
    private source: any;
    private propertyChangeListeners: Array<PropertyChangeListener>;
    
    public constructor(source: any) {    
        this.source = source;
        this.propertyChangeListeners = new Array<PropertyChangeListener>();
    }
    
    public addPropertyChangeListener(listener: PropertyChangeListener): void {
        this.propertyChangeListeners.push(listener);
    }
    
    public addBoundPropertyChangeListener(propertyName: string, listener: PropertyChangeListener): void {
        this.addPropertyChangeListener(new PropertyChangeListenerProxy(propertyName, listener));
    }
    
    /*public removePropertyChangeListener(listener: PropertyChangeListener) {
        
    }*/
    
    public firePropertyChange(propertyName: string, oldValue: any, newValue: any): void {
        var event = new PropertyChangeEvent(this.source, propertyName, oldValue, newValue);
        for(var listener of this.propertyChangeListeners) {
            listener.propertyChange(event);
        }
    }
    
    /*public firePropertyChange(propertyName: string, oldValue: any, newValue: any): void {
        if(listener && typeof listenerOrProperty == "string") {
            addPropertyChangeListener(<string>listenerOrProperty, listener);
        }
        else {
            addPropertyChangeListener(<PropertyChangeListener>listenerOrProperty);
        }
    }*/
}