package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class GestionDB {
    static Connection connection;

    // Creamos la bbdd
    private static void createConnection(){
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s/%s", SchemeDB.HOST, SchemeDB.DB_NAME);
            connection = DriverManager.getConnection(url,"root","");

        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() {
        if (connection == null) { // En caso de que la conexion a la bbdd no haya sido creada antes, la creo
            createConnection();
        }
        return connection;
    }

}
