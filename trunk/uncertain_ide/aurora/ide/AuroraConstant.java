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
	public final static String ApplicationUri = "http://www.aurora-framework.org/application";
	public final static QualifiedName screenQN = new QualifiedName(ApplicationUri, "screen");
	public final static String BMUri = "http://www.aurora-framework.org/schema/bm";
}
