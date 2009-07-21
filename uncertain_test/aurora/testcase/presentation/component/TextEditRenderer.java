/*
 * Created on 2007-8-16 03:18:22
 */
package aurora.testcase.presentation.component;

import java.io.IOException;
import java.util.Map;

import uncertain.composite.CompositeMap;
import uncertain.ocm.ISingleton;
import aurora.presentation.BuildSession;
import aurora.presentation.ViewContext;
import aurora.presentation.markup.HTMLContent;
import aurora.presentation.markup.HtmlPageContext;

public class TextEditRenderer  implements ISingleton {
    
    public static final String KEY_HTML_TEXTEDIT_SIZE = "html.textedit.size";
    public static final String KEY_UI_INPUT_TEXTEDIT = "ui.input.textedit";

    public void onCreateViewContent( BuildSession session, ViewContext context ){
        CompositeMap view = context.getView();
        Map map = context.getMap();
        HTMLContent content = new HTMLContent(context);

        content.setHtmlClass(KEY_UI_INPUT_TEXTEDIT);
        
        String size = view.getString("size");
        if(size==null) size="20";        
        map.put(KEY_HTML_TEXTEDIT_SIZE, size);
        
        String type = view.getString("type", "text");
        map.put("html.textedit.type", type);
        
        
    }
    
    public void onPreparePageContent( BuildSession session, ViewContext context )
        throws IOException
    {
        HtmlPageContext page = HtmlPageContext.getInstance(context);
        String js = session.getResourceUrl("textedit.js");
        page.addScript(js);
    }
}
