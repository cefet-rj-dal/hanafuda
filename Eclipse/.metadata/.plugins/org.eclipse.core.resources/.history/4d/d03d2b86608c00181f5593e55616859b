package dsws.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import br.gpca.survey.Item;
import br.gpca.survey.Survey;


/**
 *
 * @author Marcelo
 */
public class DAO {
    private Connection conn;
    public Connection getConn() {
        return conn;
    }
     public void setConn(Connection aConn) {
        conn = aConn;
    }
    public void grava(Survey survey){
        try {
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("select nextval('survey_seq')");
            int id = 0;
            rs.next(); 
            id = rs.getInt(1);
            rs.close();
            st.close();
            {
            PreparedStatement insStapp = conn.prepareStatement("insert into survey(id, appname, appversion, androidversion, androidlanguage, deviceinfo, useremail, username, day, month, year, hour, minute) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            insStapp.setInt(1,id);
            insStapp.setString(2,survey.appName);
            insStapp.setString(3,survey.appVersion);
            insStapp.setString(4,survey.androidVersion);
            insStapp.setString(5,survey.androidlanguage);
            insStapp.setString(6,survey.deviceInfo);
            insStapp.setString(7,survey.userEmail);
            insStapp.setString(8,survey.userName);
            insStapp.setInt(9,survey.day);
            insStapp.setInt(10,survey.month);
            insStapp.setInt(11,survey.year);
            insStapp.setInt(12,survey.hour);
            insStapp.setInt(13,survey.minute);
            insStapp.executeUpdate();
            }
            
            PreparedStatement insStq = conn.prepareStatement("insert into item(id, key, prop, value) values (?, ?, ?, ?)");
            for (int i = 0; i < survey.itemList.size(); i++) {
            	try {
                Item qi =  survey.itemList.get(i);
                insStq.setInt(1,id);
                insStq.setString(2, qi.key);
                insStq.setString(3, qi.prop);
                insStq.setString(4, qi.value);
                insStq.executeUpdate();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
