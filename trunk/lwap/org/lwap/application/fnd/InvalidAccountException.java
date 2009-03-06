/*
 * Created on 2005-10-8
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.lwap.application.fnd;

/**
 * @author Jian
 *
 */
import org.lwap.validation.ValidationException;

public class InvalidAccountException extends ValidationException
{

    public InvalidAccountException(String user_name)
    {
        super("error.fnd.InvalidAccount", null, user_name, null);
    }

}
