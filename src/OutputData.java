/**
 * \file OutputData.java
 *
 *
 * \brief INFO0010 Projet 1
 * \author Andries Alexandre s196948
 * \version 0.1
 * \date 30/10/2022
 *
 */

import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// class OutputData
public class OutputData{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Class Constants

    // Class Variables
    private int TTL ;
    private String IP ;
    private String TYPE ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public OutputData(int ttl, String ip, String type){
        TTL = ttl ;
        IP = ip ;
        TYPE = type ;
    }// Answer Object constructor
    /*------------------------------------------------------------------------*/
    /*- Getters --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Returns this output's TTL
    public int getTTL(){
        return TTL;
    }//end getTTL()
    /*------------------------------------------------------------------------*/
    // Returns this output's IP
    public String getIP(){
        return IP;
    }//end getIP()
    /*------------------------------------------------------------------------*/
    // Returns this output's TYPE
    public String getTYPE(){
        return TYPE;
    }//end getTYPE()
    /*------------------------------------------------------------------------*/
    /*- Public Methods -------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public void printOutputData(){
        System.out.println("Answer (TYPE=" + TYPE
                                           + ", TTL="
                                           + TTL
                                           + ", DATA=\""
                                           + IP
                                           +"\")");
    }//end printOutputData()
}//end class OutputData
