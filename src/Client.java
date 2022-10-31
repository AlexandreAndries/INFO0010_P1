/**
 * \file Client.java
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
import java.util.regex.*;

// class Client - main program
public class Client{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Class Constants
    private static final short ARGS_UPLIM = 3 ;
    private static final short ARGS_DOWNLIM = 2 ;
    private static final short HEADER_WIDTH = 2 ;
    // Class Variables
    private String dnsIP = null;
    private String url = null ;
    private String qType = null ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Client(String[] args) throws MessageException{
        boolean success = this.manageArgs(args);

        // Either no url
        // or no ip
        // or no ip and no url
        // or invalid qtype
        if(!success){
            throw new MessageException("Query error : Format error. Please check your program inputs and retry.");
        }
    }// Client object constuctor
    /*------------------------------------------------------------------------*/
    /*- Getters --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Returns IP address of DNS to query
    public String getNSIP(){
        return dnsIP;
    }//end getNSIP()
    /*------------------------------------------------------------------------*/
    // Returns url to transmit to DNS server
    public String getHostname(){
        return url;
    }//end getHostname()
    /*------------------------------------------------------------------------*/
    // Returns question type
    public String getQType(){
        return qType;
    }//end getQType()
    /*------------------------------------------------------------------------*/
    /*- Private Static Methods -----------------------------------------------*/
    /*------------------------------------------------------------------------*/
    private boolean manageArgs(String[] args){
        // Program only accepts either 2 or 3 arguments :
        //    - an IP for the server and a url (hostname) and no option. (2)
        //    - or an IP for the server and a url (hostname) and an option. (3)
        // If not enough arguments (or too many) are provided, the program should
        // detect it, return and error and end.
        if(args == null || args.length > 3 || args.length < 2){
            return false;
        }

        // Compute the number of arguments entered
        short argsLim = (args.length == ARGS_UPLIM) ? ARGS_UPLIM : ARGS_DOWNLIM ;

        // for each argument entered to the program, we must place it in its
        // respective variable.
        for(short i=0 ; i < argsLim ; i++){
            if(dnsIP == null && isValidIP(args[i])){
                // if the arg is a valid IP and the dnsIP field has not
                // been attributed, we place the argument in said field.
                dnsIP = args[i] ;
            }else if(qType == null && isValidOption(args[i])){
                // if the arg is a valid option and the qType field has not
                // been attributed, we place the argument in said field.
                qType = args[i] ;
            }else if(url == null){
                // if the url field has not been attributed, we place the
                // argument in said field.
                url = args[i] ;
            }else{
                // if all fields have been filled, or the argument is neither
                // an IP nor an option, and the url has been field, the extra
                // argument is an error and the program has to end.
                return false ;
            }
        }

        // Check that the mandatory fields have been filled.
        if(dnsIP == null || url == null){
            return false ;
        }

        // If no option has been selected, by default the qType is set to A.
        if(qType == null){
            qType = "A" ;
        }

        return true ;
    }//end manageArgs()
    /*------------------------------------------------------------------------*/
    public static boolean isValidOption(String str){
        if(str.equals("TXT") || str.equals("A")){
            return true;
        }else{
            return false;
        }
    }//end isValidOption()
    /*------------------------------------------------------------------------*/
    public static boolean isValidIP(String str){
        if(str==null){
            return false;
        }

        String zeroTo255Regex = "(\\d{1,2}|(0|1)\\" + "d{2}|2[0-4]\\d|25[0-5])";
        String ipv4Regex = zeroTo255Regex + "\\."
                                        + zeroTo255Regex
                                        + "\\."
                                        + zeroTo255Regex
                                        + "\\."
                                        + zeroTo255Regex ;

        Pattern ipv4Pattern = Pattern.compile(ipv4Regex);
        Matcher ipv4Matcher = ipv4Pattern.matcher(str) ;

        return ipv4Matcher.matches();
    }//end isValidIP()
    /*------------------------------------------------------------------------*/
    // check if answerID and queryID match
    public static boolean checkID(byte[] ans, short queryID){
        ByteBuffer ansIDBB = ByteBuffer.allocate(HEADER_WIDTH);
        for(short i = 0; i < HEADER_WIDTH ; i++){
            ansIDBB.put((byte) ans[i]) ;
        }
        ansIDBB.position(0);
        short ansID = ansIDBB.getShort() ;

        return (ansID == queryID) ;
    }//end checkID()
    /*------------------------------------------------------------------------*/
    static public Timer setTimeout() {
        TimerTask task = new TimerTask() {
          public void run() {
            System.out.println("DNS server took too long to respond (>5s). Exiting program.");
            System.exit(1);
          }
        };
        Timer timer = new Timer("Timer");
        long delay = 5000L;
        timer.schedule(task, delay);
        return timer;
    }
    /*------------------------------------------------------------------------*/
    /*- Print ----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Print query to std out
    static void stdoutQuestion(String dnsIP, String url, String qType){
        System.out.println("Question (NS=" + dnsIP
                                           + ", NAME="
                                           + url
                                           + ", TYPE="
                                           + qType
                                           +")");
    }//end stdoutQuestion()
    /*------------------------------------------------------------------------*/
    // Print answer to std out
    static void stdoutAnswer(String resType, String secTTL, String ansData){
        System.out.println("Answer (TYPE=" + resType
                                           + ", TTL="
                                           + secTTL
                                           + ", DATA="
                                           + ansData
                                           +")");
    }//end stdoutAnswer()
    /*------------------------------------------------------------------------*/
    /*- Test print -----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    static String toBits(final byte val) {
        final StringBuilder result = new StringBuilder();

        for(int i=0; i<8; i++){
            result.append((int)(val >> (8-(i+1)) & 0x0001));
        }
        return result.toString();
    }//end toBits()
    /*------------------------------------------------------------------------*/
    static void print(byte[] byteArray) {
        byte[] array = byteArray;
        for(int i = 0 ; i < array.length ; i++){
            System.out.println(toBits(array[i]));
        }
    }//end toBitArray()
    /*------------------------------------------------------------------------*/
    /*- Main -----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public static void main(String args[]) throws IOException, MessageException{
        // Create Client object from arguments
        Client client = new Client(args) ;

        // Get NS, NAME and TYPE from built Client object
        String NS = client.getNSIP();
        String NAME = client.getHostname();
        String TYPE = client.getQType();

        // Print Question on stdout
        stdoutQuestion(NS, NAME, TYPE);

        // Send query to NS
        Query msg = new Query(NAME, NS, TYPE);
        // print(msg.getBytesToSend()); // --- TEST
        // System.out.println("\n\n"); // --- TEST

        // Catch NS answer
        Timer timeout = setTimeout();
        byte[] ans = msg.query(msg.getBytesToSend()) ;
        timeout.cancel();
        // print(ans); // --- TEST

        // Check whether query ID matches answer ID (if not, end program
        // and return error message)
        short QID = msg.getID() ;
        if(!checkID(ans, QID)){
            throw new MessageException("DNS error : Non-matching query and answer.");
        };

        // ============================ TEST ZONE ==============================
        short QSIZE = msg.getQSIZE() ;
        // System.out.println("\n\n"); --- TEST
        Answer answer = new Answer(ans, QID, QSIZE);
        // print(answer.getHeader()); // --- TEST
        // print(answer.getQuestion()); // --- TEST
        // print(answer.getAnswer()); // --- TEST
        // ============================ TEST ZONE ==============================
    }//end main
}//fin class Client
