/*
 * Created on 2007-1-18
 */
package test;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       response.setHeader("pragma", "no-cache");
       response.setHeader("Cache-control", "no-cache, no-store, must-revalidate");
       Writer out = response.getWriter();
       HttpSession session = request.getSession(false);
       if(session==null){
           System.out.println("redirected " + new java.util.Date() );
           response.sendRedirect("index.htm");
           return;
       }else{
           String l = request.getParameter("logoff");
           if("true".equals(l)){
               session.invalidate();
               out.write("<html>");
               out.write("You have logoff. <a href=index.htm>Re-login</a>");
               out.write("</html>");
               return;
           }
       }
       String name = (String)session.getAttribute("name");

       out.write("<html>");
       out.write("Hello, "+name+", welcome!");
       out.write("<a href='login?logoff=true'>log off</a>");
       out.write("</html>");
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String name = request.getParameter("username");
        String password = request.getParameter("password");
        String save_password = request.getParameter("save_password");
        Writer out = response.getWriter();
        out.write("<html>");
        if(name==null||password==null||name.length()==0||password.length()==0){
            out.write("<font color=red>User name and password is required</font>");
        }
        else{
            HttpSession session = request.getSession(true);
            session.setAttribute("name", name);
            out.write("Your name is:"+name+"<br>Your password is:"+password);
        }
        out.write("</html>");
     }
    

}
