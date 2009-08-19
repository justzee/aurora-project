/*
 * Created on 2009-7-28
 */
package org.lwap.feature;

import java.util.HashMap;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.composite.CompositeUtil;
import uncertain.core.IGlobalInstance;

public class FileSubstitute implements IFileSubstitute, IGlobalInstance {
    
    Map             mFileMap = new HashMap();
    CompositeMap    mFiles;

    public String getRealFile(String requested_file) {
        // TODO Auto-generated method stub
        String new_file = (String)mFileMap.get(requested_file);
        return new_file == null? requested_file: new_file;
    }
    
    public void addFiles( CompositeMap files ){
        this.mFiles = files;
        CompositeUtil.fillMap(mFileMap, mFiles, "origin_file", "new_file");
    }
    
    public CompositeMap getFiles(){
        return mFiles;
    }

}
