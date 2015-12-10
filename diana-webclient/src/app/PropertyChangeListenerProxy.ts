class PropertyChangeListenerProxy implements PropertyChangeListener {
    private propertyName: string;
    private listener: PropertyChangeListener;
    
    public constructor(propertyName: string, listener: PropertyChangeListener) {
        this.propertyName = propertyName;
        this.listener = listener;
    }
    
    public propertyChange(event: PropertyChangeEvent) {
        if(event.getPropertyName() === this.propertyName) {
            this.listener.propertyChange(event);
        }
    }
}