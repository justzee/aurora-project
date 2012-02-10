/**
 * Created on: 2003-4-14 17:26:37
 * Author:     zhoufan
 */
package org.lwap.metadata;

import javax.servlet.ServletException;

import org.lwap.application.BaseService;

import uncertain.composite.CompositeMap;

/**
 * 
 */
public interface MetadataProcessor {
	
	public void processMetaData( BaseService service, CompositeMap metadata, CompositeMap config ) throws ServletException ;

}
