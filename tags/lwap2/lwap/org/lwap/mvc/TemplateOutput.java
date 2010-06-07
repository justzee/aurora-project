/**
 * Created on: 2002-11-25 19:24:19
 * Author:     zhoufan
 */
package org.lwap.mvc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import uncertain.util.QuickTagParser;
import org.lwap.application.BaseService;
import org.lwap.application.WebApplication;

import uncertain.composite.CompositeMap;
import uncertain.composite.TextParser;

/**
 *  Layout views in tabular format or by template
 *  <code>
 *      <std:template 
 *            dataModel="model_name"
 *           [Template="template_name" | Content="content with tag"]
 *      </std:template>
 *  </code>
 */
public class TemplateOutput implements View {
	
	public static final String KEY_TEMPLATE = "Template";
	public static final String KEY_CONTENT = "Content";
		
	//static AdaptiveTagParser parser = AdaptiveTagParser.newUnixShellParser();	
	

	/**
	 * @see org.lwap.mvc.View#build(BuildSession, CompositeMap, CompositeMap)
	 */
	public void build(
		BuildSession session,
		CompositeMap model,
		CompositeMap view)
		throws ViewCreationException {
		
		model = DataBindingConvention.getDataModel(model,view);
		try{
			String content = view.getString(KEY_CONTENT);
			if( content != null){
				session.getWriter().write(TextParser.parse(content, model));
			}else{
				String tplt = view.getString(KEY_TEMPLATE);
				if( tplt == null) throw new IllegalArgumentException("TemplateOutput: Must specify either 'Template' or 'Content' property");
				BaseService svc = session.getService();
				if( svc == null) return;
				File file = ((WebApplication)svc.getApplication()).getCompositeLoader().getFile(tplt);
				if( file == null) throw new ViewCreationException("Can't load file "+tplt);
				FileReader reader = null;
				try{
					reader = new FileReader(file);
				} catch(FileNotFoundException ex){
					throw new ViewCreationException("Can't load template "+file.getPath());
				}
				session.getWriter().write(TextParser.parse(reader, model));				
			}
		} catch(IOException ex){
			throw new ViewCreationException(ex);
		}	

	}

	/**
	 * @see org.lwap.mvc.View#getViewName()
	 */
	public String getViewName() {
		return "template";
	}

}
