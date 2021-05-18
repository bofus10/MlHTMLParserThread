/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;


import parsers.Process;
import java.io.IOException;
import java.sql.Connection;
import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.logging.Logger;
import java.util.Date;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.FileHandler;
import java.util.logging.SimpleFormatter;
import Bots.searchbot;




public class MLParser {

    //static ExecutorService executorPool = Executors.newFixedThreadPool(Integer.getInteger(Configuration.getConfig().getProperty("thread_num").replace("\"","")));
    static Logger vendors = Logger.getLogger("MyLog");
    static Logger bots = Logger.getLogger("MyLog2");
    static SimpleFormatter formatter = new SimpleFormatter();
    static int count = 0;
    static int flag = 1;
    

   public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, InterruptedException {

       //Codigo
       DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
       //Inicializamos
       Data.getInstance();
       ExecutorService executorPool = Executors.newFixedThreadPool(Integer.parseInt(Configuration.getConfig().getProperty("thread_num").replace("\"","")));
       
       
        try {       
            getVendorID_Map();
        } catch (SQLException ex) {
            System.out.println("MLParser:44"+ex);
        }
        //start the monitoring thread
        //ThreadMonitor monitor = new ThreadMonitor(executorPool, 3);
        //Thread monitorThread = new Thread(monitor);
        //monitorThread.start();
        

            FileHandler fh_vendors = new FileHandler(Configuration.getConfig().getProperty("log_file").replace("\"","")); 
            vendors.addHandler(fh_vendors);
            vendors.setUseParentHandlers(false);
            fh_vendors.setFormatter(formatter);
            
            FileHandler fh_bots = new FileHandler(Configuration.getConfig().getProperty("bot_log_file").replace("\"","")); 
            bots.addHandler(fh_bots);
            bots.setUseParentHandlers(false);
            fh_bots.setFormatter(formatter);
            
   
		
	while(true){
            if(flag == 1){
                //submit work to the thread pool
                executorPool.execute(new DBWriter());
                //executorPool.execute(new OfferSeeker());
                for(int j=0; j< Data.getTiendas().size(); j++){
                    executorPool.execute(new Process(Data.getTiendas().get(j)));
                }
                flag = 0;
                Date date = new Date();
                System.out.println(dateFormat.format(date) +" Todo Cargado");
            }
            else if((Data.getTiendas().isEmpty() && Data.getInstance().isDbFlag()) || Data.getInstance().isWDLockFlag()){
                    System.out.println("Realoading Pool");
                   // executorPool.shutdown();
                       // if(executorPool.isShutdown()){
                            Data.getInstance().setDbFlag(false);
                            Data.getInstance().setOSFlag(false);
                            Data.getInstance().setLogFlag(false);
                            Data.getInstance().setWDLockFlag(false);
                            //Thread.sleep(5000);
                            System.out.println("Reloaded...");
                            getVendorID_Map();
                            flag = 1;
                       // }
                    if(LocalDateTime.now().getHour() == 1 && Data.getInstance().isSyncFlag()){
                        Data.getInstance().setSyncFlag(false);
                    }
            }
            Write2Log("vendors");
            Write2Log("bots");
            try {
                OfferSeeker.offerseeker();
                searchbot.searcher();
                Thread.sleep(3000);
                //System.out.println(Data.getTiendas().size());
                WatchDogMonitor();
                //System.out.println("aaa");
                
            } catch (InterruptedException e) {
                System.out.println("MLParser:86"+e);
            }
        
        }//while
        
    }
   
   
   public static void Write2Log(String file) throws IOException{
           
       // if(Data.getInstance().isLogFlag()){
            
            
           switch(file){
               case "vendors":
                             LinkedBlockingQueue<String> colaLog = new LinkedBlockingQueue<String>();
                             if(!Data.getInstance().getColaLog().isEmpty()){
                             Data.getInstance().getColaLog().drainTo(colaLog);
                             vendors.info(colaLog.toString());
                             }
                             //fh_vendors.close();
                             break; 
                case "bots":
                             LinkedBlockingQueue<String> colaBot = new LinkedBlockingQueue<String>();
                             if(!Data.getInstance().getColaBot().isEmpty()){
                             Data.getInstance().getColaBot().drainTo(colaBot);
                             bots.info(colaBot.toString());
                             }
                             break; 
           }
           
       // }else{
            
            /*Data.getInstance().setLogFlag(true);
            FileHandler fh_vendors = new FileHandler("./log/vendors.log"); 
            //FileHandler fh_sql = new FileHandler("./log/sql.log"); 
            //FileHandler fh_offer = new FileHandler("./log/offers.log");
            
            vendors.addHandler(fh_vendors);
            vendors.setUseParentHandlers(false);
            fh_vendors.setFormatter(formatter);*/
           /* sql.addHandler(fh_sql);
            sql.setUseParentHandlers(false);
            fh_sql.setFormatter(formatter);
            offers.addHandler(fh_offer);
            offers.setUseParentHandlers(false);
            fh_offer.setFormatter(formatter);*/
            
        //}  
                      
   }
   
   public static void getVendorID_Map() throws SQLException, IOException{
       //System.out.println(Configuration.getConfig().getProperty("db_url"));
       Connection connThread = DriverManager.getConnection(Configuration.getConfig().getProperty("db_url").replace("\"",""),Configuration.getConfig().getProperty("db_user").replace("\"",""),Configuration.getConfig().getProperty("db_passwd").replace("\"","")); 
       //Connection connThread = DriverManager.getConnection("jdbc:mysql://10.11.99.180/parser?autoReconnect=true&useSSL=false","joaco","AvanzitDB@11"); 
       connThread.setAutoCommit(false);
       Statement stmntThreadVendor = connThread.createStatement();
       String sql = "SELECT * FROM vendors;";
       
       ResultSet rs = stmntThreadVendor.executeQuery(sql);
       ResultSet sr = stmntThreadVendor.executeQuery("select vendors_id,display from vendors group by vendors_id;");
       
       while(sr.next()){
           Data.getVendorDisplay().put(String.valueOf(sr.getInt("vendors_id")), sr.getString("display"));
       }
       
       while(rs.next()) {
        Data.getVendorID().put(String.valueOf(rs.getString("name")), rs.getString("vendors_id"));
        //tiendas.add(rs.getString("name"));
        if(!rs.getString("name").contains("Sitio_")){
           Data.getTiendas().add(rs.getString("name")); 
        }
        
       }
       
       //System.out.println("Vendor List"+Data.getVendorID().toString());
       
       stmntThreadVendor.close();
       System.out.println("Vendors Loaded");

      
   }
   
   public static void WatchDogMonitor() throws InterruptedException{
       
       if(Data.getInstance().isLockFlag()){
           
               System.out.println("Lock Detected.");
               Data.getTiendas().clear();
               Data.getInstance().setLockFlag(false);
               //executorPool.shutdownNow();
               Thread.sleep(20000);
               
               Data.getInstance().setWDLockFlag(true);
               //System.exit(1);
               System.out.println("Restarting...");
           
       }
   }
}
