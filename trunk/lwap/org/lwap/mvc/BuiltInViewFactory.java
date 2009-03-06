/**
 * Created on: 2002-11-20 17:40:41
 * Author:     zhoufan
 */
package org.lwap.mvc;


public class BuiltInViewFactory {
	
	
	static ClassViewFactory init( ClassViewFactory builder){
		builder.registerView(new LinkView() );
		builder.registerView(new MailLinkView() );
		builder.registerView(new Selector() );
		builder.registerView(new Layout());
		builder.registerView(new ImageLinkView());
		builder.registerView(new Repeater());
		builder.registerView(new ViewBundle());
		builder.registerView(new ServiceDispatchView());
		builder.registerView(new ModelIterator());
		builder.registerView(new TemplateOutput());
		return builder;
	}
	
	public static ClassViewFactory createBuiltInViewBuilder( String namespace ){
		ClassViewFactory builder = new ClassViewFactory(namespace);
		return init(builder);
	}
	
	public static ClassViewFactory createBuiltInViewBuilder(){
		return createBuiltInViewBuilder( ClassViewFactory.DEFAULT_NAMESPACE_URL); 
	}
	

}
