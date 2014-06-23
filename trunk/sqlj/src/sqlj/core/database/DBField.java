package sqlj.core.database;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {
	boolean pk() default false;

	/**
	 * the real name of column
	 * 
	 * @return
	 */
	String name() default "";
}
