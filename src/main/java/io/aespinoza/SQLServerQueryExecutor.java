package io.aespinoza;

import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.*;

public class SQLServerQueryExecutor {
    public static String executeQuery(String configString) {
        JSONObject response = new JSONObject();
        JSONArray resultArray = new JSONArray();

        try {
            JSONObject config = new JSONObject(configString);
            String host = config.getString("host");
            int puerto = config.getInt("puerto");
            String database = config.getString("database");
            String user = config.getString("user");
            String password = config.getString("password");
            String query = config.getString("sql");

            // URL para SQL Server
            String url = "jdbc:sqlserver://" + host + ":" + puerto + ";databaseName=" + database + ";encrypt=true;trustServerCertificate=true";

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {

                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();

                while (rs.next()) {
                    JSONObject row = new JSONObject();
                    for (int i = 1; i <= columnCount; i++) {
                        Object value = rs.getObject(i);
                        if (value instanceof String) {
                            value = ((String) value).trim();
                        }
                        row.put(metaData.getColumnName(i), value);
                    }
                    resultArray.put(row);
                }
                response.put("errCode", "0");
                response.put("errDesc", "Ok");
                response.put("data", resultArray);
            }
        } catch (ClassNotFoundException e) {
            response.put("errCode", "1");
            response.put("errDesc", "SQL Server Driver not found");
            response.put("data", new JSONArray());
        } catch (SQLException e) {
            response.put("errCode", "2");
            response.put("errDesc", e.getMessage());
            response.put("data", new JSONArray());
        } catch (Exception e) {
            response.put("errCode", "3");
            response.put("errDesc", "Invalid JSON input");
            response.put("data", new JSONArray());
        }
        return response.toString();
    }

    public static void main(String[] args) {
        String jsonConfig = "{\"host\": \"127.0.0.1\",\"puerto\": 3306, \"database\": \"xwai03\",\"user\": \"TECHNO\",\"password\": \"1234\", \"sql\": \"SELECT DISTINCT Especie FROM RecFru\"}";
        String result = executeQuery(jsonConfig);
        System.out.println(result); // Imprime el JSON en una sola línea
    }
}
