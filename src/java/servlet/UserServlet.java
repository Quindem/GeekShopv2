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
import session.OrderFacade;
import session.ProductFacade;
import session.UserFacade;

/**
 *
 * @author pupil
 */
@WebServlet(name = "UserServlet", urlPatterns = {
    "/userRegistration",
    "/listUsers",
    "/createOrder"
    
})
public class UserServlet extends HttpServlet {
     public static enum Role {USER,MANAGER,ADMINISTRATOR};
    private EncryptPassword encryptPassword;
    @EJB private UserFacade userFacade; 
    @EJB private ProductFacade productFacade; 
    @EJB private OrderFacade orderFacade; 
    
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
        if(!authUser.getRoles().contains(UserServlet.Role.USER.toString())){
            job.add("info", "Вы не авторизованы!");
            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            return;
        }
        String path = request.getServletPath();
        switch (path) {
            case "/getAllProductCards":
                JsonArrayBuilder jabProductCard = Json.createArrayBuilder();
                List<Product> listProductCards = productFacade.findAll();

                for (int i = 0; i < listProductCards.size(); i++) {
                    Product p = listProductCards.get(i);
                    JsonObjectBuilder jobProduct = Json.createObjectBuilder(); // Создаем новый JsonObjectBuilder для каждого объекта Product

                    JsonObjectBuilder jobCategoryCard = Json.createObjectBuilder();
                    jobCategoryCard.add("id", p.getCategory().getId());
                    jobCategoryCard.add("name", p.getCategory().getName());

                    jobProduct.add("id", p.getId());
                    jobProduct.add("name", p.getName());
                    jobProduct.add("price", p.getPrice());
                    jobProduct.add("picture", p.getPicture());
                    jobProduct.add("category", jobCategoryCard.build());

                    jabProductCard.add(jobProduct); // Добавляем JsonObjectBuilder в JsonArrayBuilder
                }

                try (PrintWriter out = response.getWriter()) {
                    out.println(jabProductCard.build().toString());
                }

                break;
           
            case "/product":
                String productId = request.getParameter("productId");
                Product product = productFacade.find(Long.parseLong(productId));
                job=Json.createObjectBuilder();
                JsonObjectBuilder jobCategory=Json.createObjectBuilder();
                jobCategory.add("id", product.getCategory().getId());
                jobCategory.add("name", product.getCategory().getName());
                job.add("id", product.getId());
                job.add("name", product.getName());
                job.add("description", product.getDescription());
                job.add("price",product.getPrice());
                job.add("picture", product.getPicture());
                job.add("type",product.getType());
                job.add("height",product.getHeight());
                job.add("width",product.getWidth());
                job.add("weight",product.getWeight());
                job.add("material",product.getMaterial());
                job.add("category", jobCategory.build());

            try (PrintWriter out = response.getWriter()) {
                out.println(job.build().toString());
            }
            break;
                    
            case "/createOrder":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject orderJsonObject = jsonReader.readObject();
               // int orderDate = orderJsonObject.getInt("orderDate");
              
                boolean orderStatus = orderJsonObject.getBoolean("orderStatus");
                int prodId = orderJsonObject.getInt("productId");
                int userId = orderJsonObject.getInt("userId");
                product =  productFacade.find((long)prodId);
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
                break;         } 
        
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
