/*
 * Created on 2007-1-18
 */
package test;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {


    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
       String name = request.getParameter("name");
       Writer out = response.getWriter();
       out.write("<html>");
       out.write("<h1>Hello,world</h1>");
       out.write("Your name is:"+name);
       out.write(request.getRemoteHost());
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
           out.write("Your name is:"+name+"<br>Your password is:"+password);
       }
       out.write("</html>");
    }



    public void init(ServletConfig config)
    throws ServletException{
       System.out.println("Hello servlet inited");
    }
    

    
}
