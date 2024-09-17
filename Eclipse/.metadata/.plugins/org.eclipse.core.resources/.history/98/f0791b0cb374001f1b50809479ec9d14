package amews;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

public class AMEWS extends HttpServlet {
	  private static final long serialVersionUID = 1L;
	  
	  
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
