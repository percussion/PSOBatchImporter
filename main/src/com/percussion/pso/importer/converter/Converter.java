/**
 * 
 */
package com.percussion.pso.importer.converter;

public interface Converter<SOURCE, INIT, TARGET, CONTEXT> {
    public TARGET convert(SOURCE source, INIT initial, CONTEXT context);
    
    /**
     * To signal to converter implementers or users that there is 
     * initialization object
     * @author adamgent
     *
     */
    public class NoInit {
        
    }
}