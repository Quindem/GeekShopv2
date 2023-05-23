/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package converters;

import entity.User;
import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;

/**
 *
 * @author pupil
 *


/**
 *
 * @author pupil
 */
public class ConvertorToJson {
    JsonArrayBuilder jab = Json.createArrayBuilder();
    JsonObjectBuilder job = Json.createObjectBuilder();
    
    public JsonObject getJsonObjectUser(User user){
        job.add("id", user.getId());
        job.add("firstname", user.getFirstname());
        job.add("lastname", user.getLastname());
        job.add("address", user.getAddress());
        job.add("email", user.getEmail());
        job.add("roles", getJsonArrayRoles(user.getRoles()));
        return job.build();
    }
    public JsonArray getJsonArrayRoles(List<String> roles){
        for (int i = 0; i < roles.size(); i++) {
            String role = roles.get(i);
            jab.add(role);
        }
        return jab.build();
    }
   
    public JsonArray getJsonArrayUsers(List<User>listUsers){
        for (int i = 0; i < listUsers.size(); i++) {
            User user = listUsers.get(i);
            jab.add(getJsonObjectUser(user));
        }
        return jab.build();
    }

}
