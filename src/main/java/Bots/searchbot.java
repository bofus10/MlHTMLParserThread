/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Bots;

import Processor.CommandExecuter;
import Processor.Configuration;
import Processor.Data;
import java.io.IOException;
import java.sql.SQLException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import org.json.*;
/**
 *
 * @author Mauro
 */
public class searchbot{
        //static String Configuration.getConfig().getProperty("searcher_token") = Configuration.getConfig().getProperty("searcher_Configuration.getConfig().getProperty("searcher_token")");
        //static String chatID = "-300143794";
        static String chatID = null;
         
        
    public static void searcher(){
            String url = null;
            boolean flag_stop = true;
            
            try {
                //System.out.println("start");
            Document doc;
            long offset = 0, id = 0;
            boolean f_command = false;
            String command_response = "";

            while(flag_stop){
            url = String.format("https://api.telegram.org/bot%s/getUpdates?offset=%d",Configuration.getConfig().getProperty("searcher_token").replace("\"",""),offset);
                    //System.out.println(url);
                    doc = Jsoup.connect(url).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.108 Safari/537.36")
                            .referrer("http://www.google.com")
                            .timeout(180000)
                            .ignoreContentType(true)
                            .get();
                        
                    
                    
                    JSONObject obj = new JSONObject(doc.getElementsByTag("body").text());
                    boolean status = obj.getBoolean("ok");
                   
                    if(status){
                        
                        JSONArray arr = obj.getJSONArray("result");
                        if(arr.length() == 0){
                            flag_stop = false;
                        }else{
                            for (int i = 0; i < arr.length(); i++)
                            {
                                id = arr.getJSONObject(i).getLong("update_id");
                                String user = arr.getJSONObject(i).getJSONObject("message").getJSONObject("from").getString("first_name");
                                chatID = Long.toString(arr.getJSONObject(i).getJSONObject("message").getJSONObject("chat").getLong("id"));
                                String message = arr.getJSONObject(i).getJSONObject("message").getString("text");
                                
                                if(user.equals("Mauro") && message.startsWith("!exec")){
                                    command_response = Processor.CommandExecuter.executeCommand(message.replaceAll("\\!exec",""));
                                    System.out.println("Respuesta: "+command_response + "fin");
                                    f_command = true;
                                }
                                
                                if(message.length() >= 3 && !f_command){
                                    //System.out.println(message);
                                  searchQuery(message.replaceAll("\\s+","\\%").replaceAll("\\;",""),user,chatID);  
                                }else if(f_command && !command_response.isEmpty()){
                                   System.out.println("Se envio el Comando: "+message.replaceAll("\\!exec ","")); 
                                   System.out.println("Respuesta: "+command_response + "fin");
                                   TeleBot.sendMessage(command_response, Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);   
                                   f_command = false;
                                }

                               System.out.println("id: "+id);
                               offset = id+1;
                            }  
                        }
                        //TeleBot.sendMessage("Respúesta", "694993539:AAF0tw-n_x3cYImubWE88I2LkQLhKOdlWIk", "-315765887");
                    }
            //Thread.sleep(3000);
           }
        } catch (Exception ex) {
            System.out.println(ex);
                System.out.println(url);
            
        }
    }
    
    static public void searchQuery(String txt, String user, String chatID) throws SQLException, InterruptedException, UnsupportedEncodingException, IOException{
        Connection connThread; 
        connThread = DriverManager.getConnection(Configuration.getConfig().getProperty("db_url").replace("\"",""),Configuration.getConfig().getProperty("db_user").replace("\"",""),Configuration.getConfig().getProperty("db_passwd").replace("\"","")); 
        connThread.setAutoCommit(false);
                
        String sql = "SELECT * FROM productos where articulo like '%"+txt+"%' group by articulo order by price ASC limit "+Configuration.getConfig().getProperty("searcher_limit_query").replace("\"","") +";";
        Statement stmntThread = connThread.createStatement();
        ResultSet rs = stmntThread.executeQuery(sql);  
        
                        int rowCount = 0;
                        if (rs.last()) {//make cursor to point to the last row in the ResultSet object
                          rowCount = rs.getRow();
                          rs.beforeFirst(); //make cursor to point to the front of the ResultSet object, just before the first row.
                            System.out.println("RowCount= "+rowCount);
                        }
            TeleBot.sendMessage("@"+user, Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);      
            if(!rs.next()) {    
                        TeleBot.sendMessage("No hay Productos que Coincidan con la Busqueda", Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);
                        rs.close();
                        stmntThread.close();
                    }else{
                        rs.previous();
                        while(rs.next()) {
                          String product = String.format("<%s>: [%s] precio $%.2f\n %s\n",Data.getVendorDisplay().get(rs.getString("vendor_id")),rs.getString("articulo"),rs.getFloat("price"),rs.getString("url"));
                          TeleBot.sendMessage(product, Configuration.getConfig().getProperty("searcher_token").replace("\"",""), chatID);  
                        }
                   rs.close();
                  }
    
            stmntThread.close();
            connThread.close();
    }

}