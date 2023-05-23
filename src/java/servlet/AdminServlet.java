/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import session.CategoryFacade;
import session.ProductFacade;
import session.UserFacade;

/**
 *
 * @author dimad
 */
@WebServlet(name = "AdminServlet", urlPatterns = {
    "/addRole",
    "/removeRole",
    
})
public class AdminServlet extends HttpServlet {
@EJB
    private ProductFacade productFacade;
    @EJB
    private CategoryFacade categoryFacade;
    @EJB
    private UserFacade userFacade;
   
    
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);
        JsonObjectBuilder job = Json.createObjectBuilder();
        if(session == null){
            job.add("info", "У вас нет права. Авторизуйтесь");
            job.add("status", false);
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        User authUser = (User) session.getAttribute("authUser");
        if (authUser == null){
            job.add("info", "У вас нет права. Авторизуйтесь");
            job.add("status", false);
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        if(!authUser.getRoles().contains(UserServlet.Role.ADMINISTRATOR.toString())){
            job.add("info", "У вас нет права. Авторизуйтесь");
            job.add("status", false);
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
         String path = request.getServletPath();
        switch (path) {
         case "/addRole":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject jsonObject = jsonReader.readObject();
                String userId = jsonObject.getString("userId");
                String role = jsonObject.getString("role");
                User user = userFacade.find(Long.parseLong(userId));
                if(user == null){
                    job.add("status", false);
                    job.add("info", "Нет такого пользователя");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                    break;
                   }
                if(user.getRoles().contains(role)){
                    job.add("status", false);
                    job.add("info", "Такая роль у пользователя уже есть");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                    break;
                }else{
                    user.getRoles().add(role);
                    userFacade.edit(user);
                    session.setAttribute("authUser", user);
                    job.add("status", true);
                    job.add("info", "Роль добавлена");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                }
                break;
            
         case "/removeRole":
                jsonReader = Json.createReader(request.getReader());
                jsonObject = jsonReader.readObject();
                userId = jsonObject.getString("userId");
                role = jsonObject.getString("role");
                user = userFacade.find(Long.parseLong(userId));
                if(user == null){
                    job.add("status", false);
                    job.add("info", "Нет такого пользователя");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                    break;
                }
                if(!user.getRoles().contains(role)){
                    job.add("status", false);
                    job.add("info", "Такой роли у пользователя нет");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                    break;
                }else{
                    user.getRoles().remove(role);
                    userFacade.edit(user);
                    session.setAttribute("authUser", user);
                    job.add("status", true);
                    job.add("info", "Роль удалена");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                }
                break;
           

        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
  

    }
     

