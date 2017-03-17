package com.percussion.pso.rxws.converter;

import com.percussion.pso.importer.converter.Converter;
import com.percussion.pso.rxws.item.RxWsContext;
import com.percussion.webservices.content.FolderRef;

public interface FromLocalPathToRemoteFolderRef extends 
    Converter<String, FolderRef, FolderRef, RxWsContext>{

}
