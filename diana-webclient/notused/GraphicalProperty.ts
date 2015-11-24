class GraphicalProperty {
    public static deserializationFunctionDict: {[key: string]: (input:any)=>any;} = {
        'default' : function (input:any) : any { return input;}
    };
    
    public static getDeserializationFunction(key: string = 'default') : (input:any)=>any {
        var fun = GraphicalProperty.deserializationFunctionDict[key];
        if(fun === undefined) {
            fun = GraphicalProperty.deserializationFunctionDict['default'];
        }
        return fun;
    }
    
    /*static {
        deserializationFunctionDict['default'] = function (input:any) : any { return input;}; 
    }*/
}