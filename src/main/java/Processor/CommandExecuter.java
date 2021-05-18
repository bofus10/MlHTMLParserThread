/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Processor;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Mauro
 */

public class CommandExecuter {

public static String executeCommand(String command) {

    StringBuffer output = new StringBuffer();

    Process p;
    try {
        p = Runtime.getRuntime().exec(command);
        p.waitFor();
        BufferedReader reader = 
                        new BufferedReader(new InputStreamReader(p.getInputStream()));

        String line = "";           
        while ((line = reader.readLine())!= null) {
            output.append(line + "\n");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return output.toString();

}

}
