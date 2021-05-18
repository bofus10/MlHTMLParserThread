/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;

/**
 *
 * @author Mauro
 */
public class Data {
    
     private LinkedBlockingQueue<String> colaParser = new LinkedBlockingQueue<String>();
     private LinkedBlockingQueue<String> colaLog = new LinkedBlockingQueue<String>();
     private LinkedBlockingQueue<String> colaBot = new LinkedBlockingQueue<String>();

     private static ArrayList<String> tiendas = new ArrayList<String>();
     private static HashMap<String, String> VendorID = new HashMap<String, String>();
     private static BiMap<String, String> VendorDisplay = HashBiMap.create();
     
     private boolean dbFlag = false;
     private boolean OSFlag = false;
     private boolean SyncFlag = false;
     private boolean LogFlag = false;
     private boolean LockFlag = false;
     private boolean WDLockFlag = false;    
     
     
     //Telgram Start
     private boolean StartToken = false;
    
    
    private static Data instance = null;
    
    
    public Data() {
      
    }
    
    public static Data getInstance(){
       if(instance==null){
       instance = new Data();
      }
      return instance;

    }

    public LinkedBlockingQueue<String> getColaParser() {
        return colaParser;
    }

    public static ArrayList<String> getTiendas() {
        return tiendas;
    }

    public static void setTiendas(ArrayList<String> tiendas) {
        Data.tiendas = tiendas;
    }

    public boolean isDbFlag() {
        return dbFlag;
    }

    public boolean isOSFlag() {
        return OSFlag;
    }

    public void setOSFlag(boolean OSFlag) {
        this.OSFlag = OSFlag;
    }

    public boolean isSyncFlag() {
        return SyncFlag;
    }

    public void setSyncFlag(boolean SyncFlag) {
        this.SyncFlag = SyncFlag;
    }

    public boolean isLogFlag() {
        return LogFlag;
    }

    public void setLogFlag(boolean LogFlag) {
        this.LogFlag = LogFlag;
    }

    public void setDbFlag(boolean dbFlag) {
        this.dbFlag = dbFlag;
    }

    public boolean isLockFlag() {
        return LockFlag;
    }

    public void setLockFlag(boolean LockFlag) {
        this.LockFlag = LockFlag;
    }
    
    public boolean isWDLockFlag() {
        return WDLockFlag;
    }

    public void setWDLockFlag(boolean WDLockFlag) {
        this.WDLockFlag = WDLockFlag;
    }

    public static HashMap<String, String> getVendorID() {
        return VendorID;
    }
    
    public static BiMap<String, String> getVendorDisplay() {
        return VendorDisplay;
    }


    public static void setVendorID(HashMap<String, String> VendorID) {
        Data.VendorID = VendorID;
    }
    
    public void setColaLog(String colaLog) {
        this.colaLog.add(colaLog);
    }
    
    public LinkedBlockingQueue<String> getColaLog() {
        return colaLog;
    }
    
    public void setColaBot(String colaBot) {
        this.colaBot.add(colaBot);
    }
    
    public LinkedBlockingQueue<String> getColaBot() {
        return colaBot;
    }
    
    public void setColaParser(String colaParser) {
        this.colaParser.add(colaParser);
    }

    public void setColaParser(LinkedBlockingQueue<String> colaParser) {
        this.colaParser = colaParser;
    }

}

