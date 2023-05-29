
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlet;

import tools.EncryptPassword;
import converters.ConvertorToJson;
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
import javax.servlet.http.HttpSession;
import session.CategoryFacade;
import session.ProductFacade;
import session.UserFacade;

/**
 *
 * @author dimad
 */
@WebServlet(name = "LoginServlet", loadOnStartup = 1, urlPatterns = {
    "/login",
    "/logout",
    "/userRegistration",
    "/getAllProductCards",
    "/product",
})
public class LoginServlet extends HttpServlet {
    
@EJB
    private UserFacade userFacade;
@EJB
 private ProductFacade productFacade;
@EJB private CategoryFacade categoryFacade;


private EncryptPassword encryptPassword;
    
        
 @Override
    public void init() throws ServletException {
        super.init();
        if(userFacade.count()>0) return;
        User user = new User();
        user.setFirstname("Dimitri");
        user.setLastname("Petrov");
        user.setEmail("petrov@mail.ru");
        user.setAddress("Aruserva.45-1, Sompa");
        EncryptPassword pe = new EncryptPassword();
        user.setSalt(pe.getSalt());
        user.setPassword(pe.getEncryptedPass("12345", user.getSalt()));
        user.getRoles().add(UserServlet.Role.USER.toString());
        user.getRoles().add(UserServlet.Role.MANAGER.toString());
        user.getRoles().add(UserServlet.Role.ADMINISTRATOR.toString());
        userFacade.create(user);
    }
    
    
     protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        JsonObjectBuilder job = Json.createObjectBuilder();
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
                    
     
            case "/login":
                JsonReader jsonReader = Json.createReader(request.getReader());
                JsonObject loginJsonObject = jsonReader.readObject();
                String email = loginJsonObject.getString("email");
                String password = loginJsonObject.getString("password");
                User user = userFacade.findUser(email);
                if(user == null){
                    job.add("info","Неправильная почта или пароль");
                        try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                        break;
                }
                EncryptPassword pe = new EncryptPassword();
                password=pe.getEncryptedPass(password, user.getSalt());
                if(!password.equals(user.getPassword())){
                    job.add("info","Неправильная почта или пароль");
                    try (PrintWriter out = response.getWriter()) {
                        out.println(job.build().toString());
                    }
                   
    
                }
                HttpSession session = request.getSession(true);
                session.setAttribute("authUser", user);
                job.add("info", "Вы вошли как "+ user.getEmail());
                job.add("authUser", new ConvertorToJson().getJsonObjectUser(user));
                try (PrintWriter out = response.getWriter()) {
                    out.println(job.build().toString());
                }
                break;
                
                  case "/userRegistration":
                jsonReader = Json.createReader(request.getReader());
                JsonObject jsonObject = jsonReader.readObject();
                String firstname = jsonObject.getString("firstname");
                String lastname = jsonObject.getString("lastname");
                email=jsonObject.getString("email");
                String address=jsonObject.getString("address");
                password = jsonObject.getString("password");
               
                user = new User();
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
                
                case "/logout":
                session = request.getSession(false);
                if(session != null){
                    if(session.getAttribute("authUser") != null){
                        session.invalidate();
                        job.add("info","Вы вышли из программы");
                        try (PrintWriter out = response.getWriter()) {
                            out.println(job.build().toString());
                        }
                    }
                }
                break;
        }

        
    }
        
        

                    
                    
        
                    
                 
   
                    
                    
                    
    /*@Override
    public void init() throws ServletException {
        if(userFacade.count() > 0) return;
        String salt = encryptPassword.createSalt();
        String password = encryptPassword.createHash("12345", salt);
        Customer customer = new Customer("Dimitri", "Petrov", 1000);
        customerFacade.create(customer);
        User user = new User("admin", password, salt, customer);
        userFacade.create(user);
        Role role = new Role("ADMIN");
        roleFacade.create(role);
        UserRoles userRoles = new UserRoles(user, role);
        userRolesFacade.create(userRoles);
        role = new Role("MANAGER");
        roleFacade.create(role);
        userRoles = new UserRoles(user,role);
        userRolesFacade.create(userRoles);
        role = new Role("CUSTOMER");
        roleFacade.create(role);
        userRoles = new UserRoles(user,role);
        userRolesFacade.create(userRoles);
        
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


          
