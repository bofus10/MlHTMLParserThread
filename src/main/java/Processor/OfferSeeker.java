/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;
import Bots.TeleBot;

public class OfferSeeker{
    
    static ArrayList<String> ofertas = new ArrayList<String>();
    static ResultSet rs; static ResultSet tmp;
    
    public static void offerseeker(){

        try {
            String sql = null;
            DecimalFormat df = new DecimalFormat("##");
            df.setRoundingMode(RoundingMode.HALF_EVEN);
            //System.out.println("Seeker start");
            
            //Thread.sleep(10000);
            
            if(!Data.getInstance().isDbFlag()){
                //while(true){
                Connection connThread; 
                connThread = DriverManager.getConnection(Configuration.getConfig().getProperty("db_url").replace("\"",""),Configuration.getConfig().getProperty("db_user").replace("\"",""),Configuration.getConfig().getProperty("db_passwd").replace("\"","")); 
                //connThread = DriverManager.getConnection(Data.getInstance().getDB_URL(),Data.getInstance().getUSER(),Data.getInstance().getPASS());
                connThread.setAutoCommit(false);
                sql = "SELECT * FROM ofertas;";
                Statement stmntThread = connThread.createStatement();
                rs = stmntThread.executeQuery(sql);
                    if(!rs.next()) {    
                        //System.out.println("No data on Ofertas"); 
                        Data.getInstance().setColaLog("No data on Ofertas \n");
                        rs.close();
                        stmntThread.close();
                        //Thread.sleep(1000);
                    }else{
                        rs.previous();
                        while(rs.next()) {
                          //System.out.println("rs.next "+rs.getString("ID")+" "+rs.getInt("price"));
                          //Evaluamos si la oferta es >= a 30%   
                          //System.out.println("OfferSeeker!\r");
                          //System.out.println("AA.next "+rs.getString("ID")+" "+rs.getInt("price"));
                          sql = "SELECT weekly,monthly FROM historicos WHERE ID='" +rs.getString("ID") +"';";
                          Statement stmntThread2 = connThread.createStatement();
                          tmp = stmntThread2.executeQuery(sql);
                          //System.out.println("price "+tmp.getInt("weekly"));
                          tmp.next();
                          int percentaje = 0;
                          float primer = tmp.getFloat("weekly");
                          float segundo = tmp.getFloat("monthly");
                          String ofer = "";
                          
                          if(primer == segundo){
                              segundo++;
                          }
                          
                          if(primer < segundo){
                              if(primer != 0){
                                  percentaje = Integer.parseInt(df.format(((primer-rs.getFloat("price"))/primer)*100));
                              }else{
                                  percentaje = Integer.parseInt(df.format(((segundo-rs.getFloat("price"))/segundo)*100));
                              }
                          //System.out.println(rs.getString("ID")+" "+rs.getFloat("price")+" "+tmp.getFloat("weekly")+" %"+percentaje);
                          }else{
                              if(segundo == 0){
                                  percentaje = Integer.parseInt(df.format(((primer-rs.getFloat("price"))/primer)*100));
                              }else{
                                  percentaje = Integer.parseInt(df.format(((segundo-rs.getFloat("price"))/segundo)*100));
                              }
                          }  
                          
                          //Bloque anti corridas xD
                          if((rs.getString("ID").contains("ML") || rs.getString("ID").contains("Garba") || rs.getString("ID").contains("FBL")) &&
                             (percentaje > Integer.parseInt(Configuration.getConfig().getProperty("anti_run_perc"))) 
                              && (tmp.getFloat("weekly") == tmp.getFloat("monthly")) 
                              && (primer > Integer.parseInt(Configuration.getConfig().getProperty("anti_run_price")) || primer == 9999) ){
                              
                               System.out.println("SCAMMMMMMMMMMMMMMM");
                               
                          }else{
                              
                              if (percentaje >= Integer.parseInt(Configuration.getConfig().getProperty("perc_regular")) ){                   
                                ofer = String.format("<%s>: [%s] al %d%% precio $%.2f\n %s\n",Data.getVendorDisplay().get(rs.getString("vendor_id")),rs.getString("articulo"),percentaje,rs.getFloat("price"),rs.getString("url"));
                                TeleBot.PushMSG(ofer,percentaje);
                                //System.out.println(ofer);
                                
                              }
                          
                              if (percentaje >= Integer.parseInt(Configuration.getConfig().getProperty("perc_special")) ){ 
                              Data.getInstance().setColaLog("OFERTA: "+rs.getString("ID")+","+rs.getFloat("price")+";"+ofer+"\n");
                              sql = "UPDATE historicos set special=1 WHERE ID='" +rs.getString("ID") +"';";
                                Statement stmntThread1 = connThread.createStatement();
                                stmntThread1.execute(sql);
                                connThread.commit();
                              }
                          }
                          
                          //Fin de Bloque

                          sql = "DELETE FROM ofertas WHERE ID='" +rs.getString("ID") +"';";
                          Statement stmntThread1 = connThread.createStatement();
                          stmntThread1.execute(sql);
                          connThread.commit();
                          
                          tmp.close();
                          stmntThread1.close();
                          stmntThread2.close();
                        }
                   rs.close();
                   //Thread.sleep(5000);

                  }
            connThread.close();
        }
            //System.out.println("Offer Sekker Finished");  
            //Data.getInstance().setColaLog("Offer Sekker Finished\n");
            //Data.getInstance().setOSFlag(true);
            
            
        } catch (SQLException ex) {
           System.out.println("OferSeeker:93"+ex);

        } catch (UnsupportedEncodingException ex) {
            System.out.println("OferSeeker:98"+ex);
        } catch (IOException ex) {
            System.out.println("OferSeeker:100"+ex);
        }
          
        
    }
    
}
