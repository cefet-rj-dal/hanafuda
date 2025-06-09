package amews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class AMEWS extends HttpServlet {
	  private static final long serialVersionUID = 1L;
	  
	  
	  //localhost:8080/amews/survey.spring?json=%7B%22androidLanguage%22%3A%22eng%22%2C%22androidVersion%22%3A%2235+%2815%29%22%2C%22appVersion%22%3A%222.21.0%22%2C%22computerAlgorithm%22%3A%22RandomPlayer%22%2C%22computerCombos%22%3A%22%22%2C%22computerPoints%22%3A180%2C%22day%22%3A17%2C%22deviceInfo%22%3A%22sdk_gphone64_x86_64+%28sdk_gphone64_x86_64%29%22%2C%22hour%22%3A1%2C%22minute%22%3A17%2C%22month%22%3A9%2C%22playerCombos%22%3A%22%22%2C%22playerName%22%3A%221W5X1VQY5QZ9EVN2OAR6%22%2C%22playerPoints%22%3A170%2C%22playerStarted%22%3A1%2C%22year%22%3A2024%7D	  
	  
   private String getResult(String json) throws IOException, SQLException {
      	Gson gson = new Gson();
	   	Survey obj = gson.fromJson(json, Survey.class); // Converte a string-json recebida  em objeto Survey
	   	DAOConnect conectar = new DAOConnect();
       Connection conn = conectar.createConnection();
       DAO dao = new DAO();
       dao.setConn(conn);
       int result = dao.grava(obj);
       return  Integer.toString(result);	   
   }
	  
   public void doGet(HttpServletRequest request, HttpServletResponse response)
           throws ServletException, IOException {
	   String text = "0";
       try {
           String json = request.getParameter("json");
           if (json != null)
        	   text = getResult(json);
       } 
       catch (SQLException e) {
		text = e.getMessage();
       } 
       finally {
    	   response.setContentType("text/plain");  // Set content type of the response so that jQuery knows what it can expect.
    	   response.setCharacterEncoding("UTF-8"); // You want world domination, huh?
    	   response.getWriter().write(text);        
       }
   }   
  
}
