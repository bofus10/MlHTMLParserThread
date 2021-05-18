/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;


import Bots.TeleBot;
import static Processor.Hikari.getDataSource;
import com.zaxxer.hikari.HikariDataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.concurrent.LinkedBlockingQueue;


public class DBWriter implements Runnable{
    
    String sql = null;    
    LinkedBlockingQueue<String> colaSQL = new LinkedBlockingQueue<String>();
    Connection connThread = null;
    Statement stmntThreadDB = null;
    int count = 0;
    
     public void run() {
  try {
      try {
          Thread.sleep(5000);
      } catch (InterruptedException ex) {
          System.out.println("DBWritter:34"+ex);
      }
      
    HikariDataSource dataSource = getDataSource();
    connThread = dataSource.getConnection();
    stmntThreadDB = connThread.createStatement();
   
   //stmntThread.execute(sql);
   //System.out.println("Atroden");
   //System.out.println(Data.getInstance().getColaParser().size());
      
   while((!Data.getTiendas().isEmpty()) || !Data.getInstance().getColaParser().isEmpty()){
     //while(true){
        //System.out.println("Atroden 2");
        Data.getInstance().getColaParser().drainTo(colaSQL,Integer.parseInt(Configuration.getConfig().getProperty("sql_queue_size")));
        if(LocalDateTime.now().getHour() != 0 || Data.getInstance().isSyncFlag()){
            Data.getInstance().setLockFlag(false);
            //System.out.println("local date");
            if(!colaSQL.isEmpty()){
            String valores = this.colaSQL.toString();
            valores = valores.substring(1, valores.length() - 1);
            //System.out.println(valores);
            //sql = "REPLACE INTO mercadolibre (ID, vendor_id, articulo, url, price, minor_price) VALUES " +valores +";";
            sql = "INSERT INTO productos (ID, vendor_id, articulo, url, price) VALUES " + valores + " ON DUPLICATE KEY UPDATE ID = VALUES(ID),vendor_id = VALUES(vendor_id),articulo = VALUES(articulo),url = VALUES(url),price = VALUES(price);"; 
            stmntThreadDB.execute(sql);
            //System.out.println(Thread.currentThread().getName() +  " ColaSQL=" + colaSQL.size() + " impactando.");
             //MLParser.Write2Log(Thread.currentThread().getName() +  " ColaSQL=" + colaSQL.size() + " impactando. Restan: "+Data.getInstance().getColaParser().size(), "vendors");
             Data.getInstance().setColaLog(Thread.currentThread().getName() +  " ColaSQL=" + colaSQL.size() + " impactando. Restan: "+Data.getInstance().getColaParser().size() + "\n");
             connThread.commit();
             colaSQL.clear();
             
             count = 0;
            }else{
                count++;
           
           if(count >= 150){
               Data.getInstance().setLockFlag(true);
               stmntThreadDB.close();
               connThread.close();
               break;
           }
                
                Data.getInstance().setColaLog("Cola Size: "+Data.getInstance().getColaParser().size()+ "\n");
            }
        }else{
            stmntThreadDB.close();
            connThread.close();
            move_db_diary();
            connThread = dataSource.getConnection();
            stmntThreadDB = connThread.createStatement();
        }
         //System.out.println(Data.getInstance().getColaParser().size());
         try {
             Thread.sleep(2000);
         } catch (InterruptedException ex) {
             System.out.println("DBWritter:83"+ex);
             //Thread.currentThread().interrupt(); // restore interrupted status
         }
    
   }
    Data.getInstance().setColaLog("DB Writter Finished"+ "\n");
    stmntThreadDB.close();
    connThread.close();
    //System.out.println("Empty: "+Data.getTiendas().isEmpty());
    //System.out.println(MLParser.areAlive());
    Data.getInstance().setDbFlag(true);

  } catch (SQLException e) {
   //e.printStackTrace();
   this.colaSQL.drainTo(Data.getInstance().getColaParser());
      System.out.println("DBWritter:97"+e);
      //System.out.println(sql);
      Data.getInstance().setColaLog(e+"\n"+sql);
      Data.getInstance().setDbFlag(true);
  }     catch (IOException ex) {
            System.out.println("DBWritter:101"+ex);
        }
  
 }
     
     
 public static void move_db_diary() throws SQLException, IOException{
     
     Connection connThread = null;
     Statement stmntThread = null;
     Statement stmntThread1 = null;
     ResultSet rs;
     String sql = "";
     
     HikariDataSource dataSource = getDataSource();
     connThread = dataSource.getConnection();
     stmntThread = connThread.createStatement();
     stmntThread1 = connThread.createStatement();
     //Movemos a Diario
     System.out.println("Moving Diaries...");
    
     /*
     System.out.println("Checking Missing");
     sql = "select vendors_id,display from vendors where vendors_id not in\n" +
           "(select vendor_id from productos group by vendor_id);";
     rs = stmntThread.executeQuery(sql);
        if(!rs.next()) {    
            Data.getInstance().setColaLog("No Vendor Missing \n");
            rs.close();
            stmntThread.close();
        }else{
          rs.previous();  
          TeleBot.sendMessage("Missing Vendors Data", "BOT_TOKEN", "CHATID");
          while(rs.next()){
            TeleBot.sendMessage(rs.getString("vendors_id")+","+rs.getString("display"), "BOT_TOKEN", "CHATID");
          }
          rs.close();
        }*/
     //MLParser.Write2Log("Backing up Diaries", "sql");
     sql = "INSERT INTO productos_diario_ml (ID,vendor_id,articulo,url,day_price,date)\n" +
            "select a.ID,a.vendor_id,a.articulo,a.url,a.price,(CURDATE() - interval 1 day)\n" +
            "from productos a\n" +
            "	left outer join (\n" +
            "		SELECT id,day_price,date\n" +
            "		from productos_diario_ml \n" +
            "		JOIN (SELECT `id`, MAX(`date`) `date`\n" +
            "		FROM `productos_diario_ml`\n" +
            "		GROUP BY `id`) `d1` USING (`id`, `date`) ) as b\n" +
            "	on (a.ID=b.ID)\n" +
            "where\n" +
            "a.id like 'MLA%' and\n" +
            "((a.price <> b.day_price) or\n" +
            " (b.day_price is null))";
    
     stmntThread.execute(sql);
     connThread.commit();
     
     sql = "INSERT INTO productos_diario_sitios (ID,vendor_id,articulo,url,day_price,date)\n" +
            "select a.ID,a.vendor_id,a.articulo,a.url,a.price,(CURDATE() - interval 1 day)\n" +
            "from productos a\n" +
            "	left outer join (\n" +
            "		SELECT id,day_price,date\n" +
            "		from productos_diario_sitios \n" +
            "		JOIN (SELECT `id`, MAX(`date`) `date`\n" +
            "		FROM `productos_diario_sitios`\n" +
            "		GROUP BY `id`) `d1` USING (`id`, `date`) ) as b\n" +
            "	on (a.ID=b.ID)\n" +
            "where\n" +
            "a.id not like 'MLA%' and\n" +
            "((a.price <> b.day_price) or\n" +
            " (b.day_price is null))";
     
     stmntThread.execute(sql);
     connThread.commit();
        
    //Generamos el Weekly
    /*System.out.println("Generating Weekly"); 
    //MLParser.Write2Log("Generating Weekly", "sql");
    sql = "REPLACE INTO historicos (ID,weekly,udate)  \n" +
    "    SELECT ID,max(day_price),(CURDATE() - interval 1 day) as date\n" +
    "    FROM `productos_diario_ML`\n" +
    "    WHERE date BETWEEN (curdate() - interval 7 day) AND (curdate() - interval 1 day)\n" +
    "    GROUP BY ID;"; 
    
     stmntThread.execute(sql);
     connThread.commit();
     
     sql = "REPLACE INTO historicos (ID,weekly,udate)  \n" +
    "    SELECT ID,max(day_price),(CURDATE() - interval 1 day) as date\n" +
    "    FROM `productos_diario_Sitios`\n" +
    "    WHERE date BETWEEN (curdate() - interval 7 day) AND (curdate() - interval 1 day)\n" +
    "    GROUP BY ID;"; 
    
     stmntThread.execute(sql);
     connThread.commit();
     //Actualizamos UDATE para los nuevos!
     System.out.println("Updating New ones on Udate");
     sql = "update historicos set udate=(curdate() + interval 7 day) where udate is null;";
     
     stmntThread.execute(sql);
     connThread.commit();
     */
     
     /*
     //Truncamos la Tabla cada Semana
     DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
     Date date = new Date();
     sql = "select min(udate) from historicos where udate is not null;";
     ResultSet rs = stmntThread.executeQuery(sql);
     rs.next();
     try {
        if(rs.getString("udate").equals(dateFormat.format(date))){
            System.out.println("Truncating Table");
            String sql1 = String.format("delete from productos where ID IN (select ID from historicos where udate='%s');", rs.getString("udate")); 
            stmntThread1.execute(sql1);
            connThread.commit();
            
            sql1 = String.format("update historicos set udate=(curdate() + interval 7 day) where udate='%s';", rs.getString("udate"));
            stmntThread1.execute(sql1);
            connThread.commit();
     }
     } catch (SQLException e) {
         System.out.println("DBWritter:158"+e);
     } */
     
     System.out.println("Truncating Table");
     String sql1 = "TRUNCATE productos;"; 
     stmntThread1.execute(sql1);
     connThread.commit();
     
     //rs.close();
     stmntThread.close();
     stmntThread1.close();
     connThread.close();
     
     Data.getInstance().setSyncFlag(true);
 }
    
}
