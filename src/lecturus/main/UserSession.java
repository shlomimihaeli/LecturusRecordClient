/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lecturus.main;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author shnizle
 */
public class UserSession {

    private static UserSession session;
    
    private String token;

    public String getToken() {
        return token;
    }
    
    private UserSession(String sessionToken) {
        
        this.token = sessionToken;
    }
    
    public static UserSession getSession() throws NoUserSessionException{
         return new UserSession("&debug=true");
       // if(session == null) throw new NoUserSessionException();
       // return session;
    }
    
    public static UserSession login(String username, String password) throws ParseException, Exception{
        
        JSONObject loginRes = HttpUtills.restGetActionForData("http://lecturus2.herokuapp.com/user/login?email="+username+"&password="+password);
             
             session = new UserSession((String)loginRes.get("token"));
         
         
         return session;
    }
    
}

class NoUserSessionException extends Exception{}
class InvalidLogin extends Exception{}
