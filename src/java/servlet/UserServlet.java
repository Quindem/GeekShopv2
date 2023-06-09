/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.CustomerOrder;
import entity.Product;
import tools.EncryptPassword;
import entity.User;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.GregorianCalendar;
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
import session.OrderFacade;
import session.ProductFacade;
import session.UserFacade;

/**
 *
 * @author pupil
 */
@WebServlet(name = "UserServlet", urlPatterns = {
    "/createOrder",
    
    
})
public class UserServlet extends HttpServlet {
     public static enum Role {USER,MANAGER,ADMINISTRATOR};
    private EncryptPassword encryptPassword;
    @EJB private UserFacade userFacade; 
    @EJB private ProductFacade productFacade; 
    @EJB private OrderFacade orderFacade;
    @EJB private CategoryFacade categoryFacade;
    
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    
      public static boolean isRole(String role){
        for(int i=0;i<UserServlet.Role.values().length;i++){
            if(UserServlet.Role.values()[i].toString().equals(role)){
                return true;
            }
        }
        return false;
    }
    
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
        if(!authUser.getRoles().contains(UserServlet.Role.USER.toString())){
            job.add("info", "Вы не авторизованы!");
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        String path = request.getServletPath();
        switch (path) {
           
                    
            case "/createOrder":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject orderJsonObject = jsonReader.readObject();
               // int orderDate = orderJsonObject.getInt("orderDate");
              
                boolean orderStatus = orderJsonObject.getBoolean("orderStatus");
                int prodId = orderJsonObject.getInt("productId");
                int userId = orderJsonObject.getInt("userId");
               Product product =  productFacade.find((long)prodId);
                 User user =  userFacade.find((long)userId);
                CustomerOrder order = new CustomerOrder();
                order.setOrderStatus(orderStatus);
                
                order.setOrderDate(new GregorianCalendar().getTime());
                order.setUser(user);
                order.setProduct(product);
                orderFacade.create(order);
                JsonObjectBuilder jobProduct=Json.createObjectBuilder();
                JsonObjectBuilder jobUser=Json.createObjectBuilder();
                jobProduct.add("id", order.getProduct().getId());
                jobProduct.add("name", order.getProduct().getName());
                jobUser.add("id", order.getUser().getId());
                jobUser.add("email", order.getUser().getEmail());
                job.add("orderStatus", order.getOrderStatus());
              
                job.add("orderDate", order.getOrderDate().toString());
                job.add("product", jobProduct.build());
                job.add("user", jobUser.build());
                try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
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



