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

 /** Class description :
  *
  * OutputData class is used to print the data gathered from the answer received
  * from the DNS.
  * The required fields are printed on stdout.
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
    // Class Variables
    private int TTL ;                             // TTL value
    private String DATA ;                         // Data to print
    private String TYPE ;                         // answer type
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public OutputData(int ttl, String data, String type){
        TTL = ttl ;
        DATA = data ;
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
    // Returns this output's DATA
    public String getDATA(){
        return DATA;
    }//end getDATA()
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
                                           + DATA
                                           +"\")");
    }//end printOutputData()
}//end class OutputData
