/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import tools.EncryptPassword;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import session.UserFacade;

/**
 *
 * @author pupil
 */
@WebServlet(name = "UserServlet", urlPatterns = {
    "/userRegistration",
    "/listUsers",
    
})
public class UserServlet extends HttpServlet {
     public static enum Role {USER,MANAGER,ADMINISTRATOR};
    private EncryptPassword encryptPassword;
    @EJB private UserFacade userFacade; 
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        JsonObjectBuilder job = Json.createObjectBuilder();
        
        HttpSession session = request.getSession(false);
        if(session == null){
            job.add("info", "Вы не авторизованы!");
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        User authUser = (User) session.getAttribute("authUser");
        if(authUser == null){
            job.add("info", "Вы не авторизованы!");
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        if(!authUser.getRoles().contains(UserServlet.Role.MANAGER.toString())){
            job.add("info", "Вы не авторизованы!");
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        String path = request.getServletPath();
        switch (path) {
            case "/userRegistration":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject jsonObject = jsonReader.readObject();
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                String email=jsonObject.getString("email");
                String address=jsonObject.getString("address");
                String password = jsonObject.getString("password");
               
                User user = new User();
                user.setFirstname(firstname);
                user.setLastname(lastname);
                user.setEmail(email);
                user.setAddress(address);
                encryptPassword = new EncryptPassword();
                user.setSalt(encryptPassword.getSalt());
                password = encryptPassword.getEncryptedPass(password, user.getSalt());
                user.setPassword(password);
                user.getRoles().add(UserServlet.Role.USER.toString());
                userFacade.create(user);
                job.add("info", "Пользователь добавлен");
                try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
                }
              break;
                  
            /*case "/listUsers":
                 JsonArrayBuilder jabUser = Json.createArrayBuilder();
                List<User> listUsers = userFacade.findAll();
                job=Json.createObjectBuilder();
                for (int i = 0; i < listUsers.size(); i++) {
                    User u = listUsers.get(i);
                    job.add("id", u.getId());
                    job.add("firstname", u.getFirstname());
                    job.add("lastname", u.getLastname());
                    job.add("email", u.getEmail());
                    job.add("address", u.getAddress());
                }
              
                try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
                
                break;*/
              case "/listUsers":
    JsonArrayBuilder jabUser = Json.createArrayBuilder();
    List<User> listUsers = userFacade.findAll();
    for (int i = 0; i < listUsers.size(); i++) {
        User u = listUsers.get(i);
        JsonObjectBuilder jobUser = Json.createObjectBuilder(); // Создаем JsonObjectBuilder для каждого пользователя
        
        jobUser.add("id", u.getId());
        jobUser.add("firstname", u.getFirstname());
        jobUser.add("lastname", u.getLastname());
        jobUser.add("email", u.getEmail());
        jobUser.add("address", u.getAddress());
        
        jabUser.add(jobUser); // Добавляем JsonObjectBuilder в JsonArrayBuilder
    }
    
    try (PrintWriter out = response.getWriter()) {
        out.println(jabUser.build().toString()); // Выводим JsonArrayBuilder
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
