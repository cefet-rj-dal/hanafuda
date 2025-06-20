package amews;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 */
public class DAOConnect {
    public Connection createConnection() throws IOException, SQLException {
        Connection conn = null;
        String classname = "org.postgresql.Driver";
        try {
            Class.forName(classname);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        String servidor = "";
        String base = "";
        String usuario = "";
        String senha = "";
        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("/amews.properties");   
        properties.load(inputStream);
        servidor = properties.getProperty("db.servidor");
        base = properties.getProperty("db.base");
        usuario = properties.getProperty("db.usuario");
        senha = properties.getProperty("db.senha");
        String url = "jdbc:postgresql://" + servidor + "/" + base + "?charSet=UTF8";
        Properties props = new Properties();
        props.setProperty("user", usuario);
        props.setProperty("password", senha);
        
        conn = DriverManager.getConnection(url, props);
        return conn;
    }
}