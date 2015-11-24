declare module Vertx {
    export interface EventBus {
        onopen: () => void;
        onclose: () => void;
        
        registerHandler: (address: string, handler: Handler) => void;
        unregisterHandler: (address: string, handler: Handler) => void;
        send: (address: string, message: any, handle: Handler)=> void;
        publish: (address: string, message: any)=> void;
    }
    
    export interface Handler {
        (message: any): void;
    }
}