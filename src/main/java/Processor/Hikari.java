/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;



public class Hikari {
    
   
   private static HikariDataSource datasource;
		
    public static HikariDataSource getDataSource()
    {
            if(datasource == null)
            {
              HikariConfig config = new HikariConfig("./config/hikari.properties");
              datasource = new HikariDataSource(config);
            }
                return datasource;
    }
    
    //Not in use 
    /*
    public static void sendQuery(String msg){
        
        Connection connection = null;
        Statement stmt = null;
        ResultSet resultSet = null;
        try
        {
            HikariDataSource dataSource = getDataSource();
            connection = dataSource.getConnection();
            stmt = connection.createStatement();

            String sql = "INSERT INTO productos (ID, vendor_id, articulo, url, price) VALUES " + msg + " ON DUPLICATE KEY UPDATE ID = VALUES(ID),vendor_id = VALUES(vendor_id),articulo = VALUES(articulo),url = VALUES(url),price = VALUES(price);"; 
            stmt.execute(sql);
            
            stmt.close();
            connection.close();
        }
        catch (SQLException e)
        {
            System.out.println(e);
        }
        
        
        
    }*/
}
