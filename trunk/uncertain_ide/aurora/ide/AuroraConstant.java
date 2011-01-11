/**
 * 
 */
package aurora.ide;

import uncertain.composite.QualifiedName;




/**
 * @author linjinxiao
 *
 */
public interface AuroraConstant {
	//uri
	public final static String ApplicationUri = "http://www.aurora-framework.org/application";
	public final static String BMUri = "http://www.aurora-framework.org/schema/bm";
	//qn
	public final static QualifiedName screenQN = new QualifiedName(ApplicationUri, "screen");
	public final static QualifiedName viewQN = new QualifiedName(ApplicationUri, "view");
	public final static QualifiedName dataSetQN = new QualifiedName(ApplicationUri, "dataSet");
	public static QualifiedName modelQN = new QualifiedName(BMUri, "model");
	
	public static String[] buildinFileExtension = new String[]{"screen","bm"};
	public static String screenFileExtension = "screen";
	public static String serviceFileExtension = "service";
	public static String svcFileExtension = "svc";

}
