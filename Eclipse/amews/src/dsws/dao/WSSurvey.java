package dsws.dao;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.sql.Connection;

import com.google.gson.Gson;

import br.gpca.survey.DAO;
import br.gpca.survey.DAOConnect;
import br.gpca.survey.Survey;

@Controller
@RequestMapping("/survey.spring")
public class WSSurvey 
{        
    @RequestMapping(value="/", method = RequestMethod.GET)
    public @ResponseBody String write(@RequestParam("json") String json) 
    {   	
       	Gson gson = new Gson();
    	Survey obj = gson.fromJson(json, Survey.class); // Converte a string-json recebida  em objeto Survey
    	DAOConnect conectar = new DAOConnect();
        Connection conn = conectar.createConnection();
        DAO dao = new DAO();
        dao.setConn(conn);
        int result = dao.grava(obj);
        return  Integer.toString(result);
    }     
} 