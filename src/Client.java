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

 /** Class description :
  *
  * Client class is used as the main program. The program manages the user inputs,
  * forms a query from those inputs (or ends and returns an error if the inputs
  * are not adequate) and sends it through a socket. The program then parses the
  * received answers and output to stdout the gathered data. If the Name Server
  * takes too long to answer, the program shuts down after a 5 seconds timeout.
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
    private static final short ARGS_UPLIM = 3 ;     // Upper limit of nbr of inputs
    private static final short ARGS_DOWNLIM = 2 ;   // Lower limit of nbr of inputs
    private static final short HEADER_WIDTH = 2 ;   // Length of 2 bytes used by data in header
    // Class Variables
    private String dnsIP = null;                    // NS IP address
    private String url = null ;                     // hostname to query
    private String qType = null ;                   // Type of query
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
            throw new MessageException("Query error : Format error. Please check your program inputs and retry. Only A and TXT qtypes are supported by this client");
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
    // Manage the inputs to the program
    private boolean manageArgs(String[] args){
        // Send help to stdout if asked
        if(args.length == 1 && (args[0].equals("h") || args[0].equals("help"))){
            printHelp(); // prints help and ends program
        }

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
    // Check if input option is valid (TXT or A)
    public static boolean isValidOption(String str){
        if(str.equals("TXT") || str.equals("A")){
            return true;
        }else{
            return false;
        }
    }//end isValidOption()
    /*------------------------------------------------------------------------*/
    // Check if input IP is valid (format : IPv4: [0:255].[0:255].[0:255].[0:255])
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
    // Set 5 seconds timeout in case DNS takes too long to answer query
    static public Timer setTimeout() {
        TimerTask task = new TimerTask() {
          public void run() {
            System.out.println("DNS server took too long to respond (>5sec). Exiting program.\n");
            System.exit(1);
          }
        };
        Timer timer = new Timer("Timer");
        long delay = 5000L;
        timer.schedule(task, delay);
        return timer;
    }//end setTimeout()
    /*------------------------------------------------------------------------*/
    /*- Print ----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Print help !
    static void printHelp(){
        System.out.println("HELP: \n\n");
        System.out.println("This program takes 2 mandatory arguments, and 1 optional argument :");
        System.out.println("\t - <name server IP>");
        System.out.println("\t - <domain name to query>");
        System.out.println("\t - <question type> [optional, default: A]");
        System.out.println("\n Inputs order does not matter. Question type can be TXT or A only.\n");
        System.exit(0) ;
    }//end printHelp()
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
    static void stdoutAnswer(OutputData[] out, short ancount){
        for(short i = 0; i < ancount ; i++){
            if(out[i] != null){
                out[i].printOutputData();
            }
        }
    }//end stdoutAnswer()
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

        // Catch NS answer
        Timer timeout = setTimeout();
        byte[] ans = msg.query(msg.getBytesToSend()) ;
        timeout.cancel();

        // Check whether query ID matches answer ID (if not, end program
        // and return error message)
        short QID = msg.getID() ;
        if(!checkID(ans, QID)){
            throw new MessageException("DNS error : Non-matching query and answer.");
        };

        // Get QSIZE from query, and parse received answer
        short QSIZE = msg.getQSIZE() ;
        Answer answer = new Answer(ans, QID, QSIZE);

        // Print gathered data on stdout
        stdoutAnswer(answer.getOutput(), answer.getANCOUNT());
    }//end main
}//fin class Client
