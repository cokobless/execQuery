package io.aespinoza;

import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;

public class MySQLQueryExecutor {
    public static JSONArray executeQuery(JSONObject config) {
        JSONArray resultArray = new JSONArray();

        String host = config.getString("host");
        int puerto = config.getInt("puerto");
        String database = config.getString("database");
        String user = config.getString("user");
        String password = config.getString("password");
        String query = config.getString("sql");

        String url = "jdbc:mysql://" + host + ":" + puerto + "/" + database;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (rs.next()) {
                JSONObject row = new JSONObject();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(metaData.getColumnName(i), rs.getObject(i));
                }
                resultArray.put(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultArray;
    }

    public static void main(String[] args) {
        String jsonConfig = "";
        JSONObject config = new JSONObject(jsonConfig);
        JSONArray result = executeQuery(config);
        System.out.println(result.toString(2)); // Imprime el JSON con indentación de 2 espacios
        String aux = result.toString(2);
        System.out.println("largo de caracteres " + aux.length());
    }
}
