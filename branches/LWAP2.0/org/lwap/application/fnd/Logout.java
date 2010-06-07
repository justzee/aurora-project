package org.lwap.application.fnd;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.lwap.application.BaseService;

public class Logout extends BaseService
{

    public Logout()
    {
    }

    public void doService()
        throws IOException, ServletException
    {
        HttpSession s = request.getSession(false);
        if(s != null)
        {
            s.invalidate();
        }
        response.addCookie(new Cookie("user_name", ""));
        response.addCookie(new Cookie("user_password", ""));
    }
}
