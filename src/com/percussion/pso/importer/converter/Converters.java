package com.percussion.pso.importer.converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Converters {
    

    public static <SOURCE, INIT, TARGET, CONTEXT> 
    List<TARGET> convertAll(Converter<SOURCE, INIT, TARGET, CONTEXT> converter,
            List<SOURCE> sourceItems, Class<INIT> klass, CONTEXT context ) {
        List<CONTEXT> contexts = makeList(sourceItems.size(), context);
        return convertAll(converter, sourceItems, klass, contexts);
    }
    
    public static <T> List<T> makeList (int size, T o) {
        return new ArrayList<T>(Collections.nCopies(size, o));
    }
    
    public static <T> List<T> makeList(int size, Class<T> klass) {
        if (klass == null) {
            T type = null;
            return makeList(size, type);
        }
        List<T> initItems = new ArrayList<T>();
        for (int i = 0; i < size; i++) {
            try {
                initItems.add(klass.newInstance());
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("Cannot Instatiate klass " + klass, e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Cannot Instatiate klass " + klass, e);
            }
        }
        return initItems;
    }
    
    public static <SOURCE, INIT, TARGET, CONTEXT> 
    List<TARGET> convertAll(Converter<SOURCE, INIT, TARGET, CONTEXT> converter,
            List<SOURCE> sourceItems, Class<INIT> klass, List<CONTEXT> contexts)  {
        if (sourceItems == null)
            throw new IllegalArgumentException("source items is null");
        List<INIT> initItems = makeList(sourceItems.size(), klass);
        return convertAll(converter, sourceItems, initItems, contexts);
    }

    public static <SOURCE, INIT, TARGET, CONTEXT> 
    List<TARGET> convertAll(Converter<SOURCE, INIT, TARGET, CONTEXT> converter,
            List<SOURCE> sourceItems, CONTEXT context) {
        List<INIT> nullInit = makeList(sourceItems.size(), null);
        return convertAll(converter, sourceItems, nullInit, context);
    }
            
    public static <SOURCE, INIT, TARGET, CONTEXT> 
    List<TARGET> convertAll(Converter<SOURCE, INIT, TARGET, CONTEXT> converter,
            List<SOURCE> sourceItems, List<INIT> initItems, CONTEXT context) {
        List<CONTEXT> contexts = makeList(sourceItems.size(), context);
        return convertAll(converter, sourceItems, initItems, contexts);
    }
    public static <SOURCE, INIT, TARGET, CONTEXT> 
    List<TARGET> convertAll(Converter<SOURCE, INIT, TARGET, CONTEXT> converter,
            List<SOURCE> sourceItems, List<INIT> initItems, List<CONTEXT> contexts) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter cannot be null");
        }
        if (sourceItems == null)
            throw new IllegalArgumentException("source items is null");
        if (initItems == null)
            throw new IllegalArgumentException("init items is null");
        if (sourceItems.size() != initItems.size())
            throw new IllegalArgumentException(
                    "More or less source items than target items");
        if (contexts == null || sourceItems.size() != contexts.size()) {
            throw new IllegalArgumentException("The number of contexts " +
                    "must = source items");
        }
        List<TARGET> targetItems = new LinkedList<TARGET>();
        for (int i = 0; i < sourceItems.size(); i++) {
            SOURCE source = sourceItems.get(i);
            INIT init = initItems.get(i);
            CONTEXT context = contexts.get(i);
            TARGET target = converter.convert(source, init, context);
            targetItems.add(target);
        }
        return targetItems;
    }

}
