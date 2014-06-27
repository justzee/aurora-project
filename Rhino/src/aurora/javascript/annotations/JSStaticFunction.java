/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package aurora.javascript.annotations;

import java.lang.annotation.*;

/**
 * An annotation that marks a Java method as JavaScript static function. This can
 * be used as an alternative to the <code>jsStaticFunction_</code> prefix desribed in
 * {@link aurora.javascript.ScriptableObject#defineClass(aurora.javascript.Scriptable, java.lang.Class)}.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface JSStaticFunction {
    String value() default "";
}
