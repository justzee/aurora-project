package sqlj.core.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
	/**
	 * table name
	 * 
	 * @return
	 */
	String name();

	/**
	 * is standard who enabled
	 * 
	 * @return
	 */
	boolean stdwho() default true;
}
