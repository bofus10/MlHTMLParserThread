/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 *
 * @author Mauro
 */
public class Configuration {
    
    private static FileInputStream config = null; 
    static Properties prop=new Properties();
    
    public Configuration() {
        
    }
    
    public static Properties getConfig() throws FileNotFoundException, IOException{
        if(config == null){
           config = new FileInputStream("./config/config.properties"); 
           prop.load(config);
        }
        return prop;
        
    }
   
}
