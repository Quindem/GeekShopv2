/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
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
    "/listUsers",
    
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
        /*if(session == null){
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
        }*/
         String path = request.getServletPath();
        switch (path) {
         case "/addRole":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject jsonObject = jsonReader.readObject();
                String userId = jsonObject.getString("userId");
                String role = jsonObject.getString("role");
                User user = userFacade.find(Long.parseLong(userId));
                if(user == null){
                    job.add("info", "Нет такого пользователя");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                    break;
                }
               
                if("Administrator".equals(user.getEmail())){
                    job.add("info", "Administrator неприкасаем");
                    job.add("status", false);
                        try (PrintWriter out = response.getWriter()) {
                            out.println(job.build().toString());
                        }
                        break;
                }
                
                if(!user.getRoles().contains(role) && UserServlet.isRole(role)){
                    //если у пользователя нет такой роли и роль такая сущесвтвует в статическом enum
                    user.getRoles().add(role);
                    job.add("info", "Роль изменена");
                    userFacade.edit(user);
                }else{
                    job.add("info", "Такая роль у пользователя уже есть");
                }
                try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
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
            
         case "/listUsers":
                JsonArrayBuilder jabUsers = Json.createArrayBuilder();
                List<User> listUsers = userFacade.findAll();
                for (int i = 0; i < listUsers.size(); i++) {
                    User u = listUsers.get(i);
                    JsonArrayBuilder jabUserRoles = Json.createArrayBuilder();
                    for(int j = 0; j< u.getRoles().size();j++){
                        jabUserRoles.add(u.getRoles().get(j));
                    }
                    JsonObjectBuilder jobUser = Json.createObjectBuilder(); // Создаем JsonObjectBuilder для каждого пользователя

                    jobUser.add("id", u.getId());
                    jobUser.add("firstname", u.getFirstname());
                    jobUser.add("lastname", u.getLastname());
                    jobUser.add("email", u.getEmail());
                    jobUser.add("address", u.getAddress());
                    jobUser.add("roles", jabUserRoles.build());
                    

                    jabUsers.add(jobUser); // Добавляем JsonObjectBuilder в JsonArrayBuilder
                }

                try (PrintWriter out = response.getWriter()) {
                    out.println(jabUsers.build().toString()); // Выводим JsonArrayBuilder
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
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    }
     

