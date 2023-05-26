/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import entity.Category;
import entity.Product;
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
import session.CategoryFacade;
import session.ProductFacade;
import session.UserFacade;

/**
 *
 * @author pupil
 */
@WebServlet(name = "ProductServlet", urlPatterns = {
    "/createProduct",
    "/product",
    "/getAllProductCards",
   
    
    
})
public class ManagerServlet extends HttpServlet {
    @EJB private UserFacade userFacade; 
    @EJB private ProductFacade productFacade;
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
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        JsonObjectBuilder job = Json.createObjectBuilder();
        String path = request.getServletPath();
        switch (path) {
            case "/createProduct":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject productJsonObject = jsonReader.readObject();
                String name = productJsonObject.getString("name","");
                String description = productJsonObject.getString("description","");
                String price = productJsonObject.getString("price","");
                String picture = productJsonObject.getString("urlPicture","");
                String type = productJsonObject.getString("type");
                String height = productJsonObject.getString("height");
                String width = productJsonObject.getString("width");
                String  weight = productJsonObject.getString("weight");
                String material = productJsonObject.getString("material");
                int  categoryId = productJsonObject.getInt("categoryId");
                Category category =  categoryFacade.find((long)categoryId);
                Product product = new Product();
                product.setName(name);
                product.setDescription(description);
                product.setPrice(price);
                product.setCategory(category);
                product.setPicture(picture);
                product.setType(type);
                product.setHeight(height);
                product.setWidth(width);
                product.setWeight(weight);
                product.setMaterial(material);
                productFacade.create(product);
                    JsonObjectBuilder jobCategory=Json.createObjectBuilder();
                    jobCategory.add("id", product.getCategory().getId());
                    jobCategory.add("name", product.getCategory().getName());
                    job.add("id", product.getId());
                    job.add("name", product.getName());
                    job.add("description", product.getDescription());
                    job.add("price",product.getPrice());
                    job.add("type",product.getType());
                    job.add("height",product.getHeight());
                    job.add("width",product.getWidth());
                    job.add("weight",product.getWeight());
                    job.add("material",product.getMaterial());
                    job.add("urlPicture", product.getPicture());
                    job.add("category", jobCategory.build());
                 try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
                }
                break;
                
        
          
        
    
    
                /*case "/listProductCard":
                JsonArrayBuilder jabProductCard = Json.createArrayBuilder();
                List<Product> listProductCards = productFacade.findAll();
                 job=Json.createObjectBuilder();
                for (int i = 0; i < listProductCards.size(); i++) {
                    Product p = listProductCards.get(i);
                    jobCategory=Json.createObjectBuilder();
                    jobCategory.add("id", p.getCategory().getId());
                    job.add("id", p.getId());
                    job.add("name", p.getName());
                    job.add("price",p.getPrice());
                    job.add("picture", p.getPicture());
                    job.add("category", jobCategory.build());
                }
                try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
                }
                break;
                */
                
                
             
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
    
    

