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
    // Class Variables
    private String dnsIP = null;
    private String url = null ;
    private String qType = null ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Client(String[] args){
        boolean success = this.manageArgs(args);

        // check for errors ?? ---- to modify
        // either no url
        // or no ip
        // or no ip and no url
        // or invalid qtype
        if(!success){
            System.out.println("Input ERROR\n");
        }
    }// Client object constuctor
    /*------------------------------------------------------------------------*/
    /*- Getters --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Returns IP address of DNS to query
    public String getIP(){
        return dnsIP;
    }//end getIP()
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
    /*- Main -----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public static void main(String args[]) throws IOException{
        // Create Client object from arguments
        Client client = new Client(args) ;

        // Get NS, NAME and TYPE from built Client object
        String NS = client.getIP();
        String NAME = client.getHostname();
        String TYPE = client.getQType();

        // Print Question on stdout
        stdoutQuestion(NS, NAME, TYPE);

        // Send query to NS
        Query msg = new Query(NAME, NS, TYPE);
        // print(msg.getBytesToSend());

        // Catch NS answer
        byte[] ans = msg.query(msg.getBytesToSend()) ;

    }//end main
}//fin class Client




//-----------------------------------------------------------------------------------------------------
// public byte[] query(byte[] bytesToSend) throws IOException {
//     // Initiate a new TCP connection with a Socket
//     Socket socket = new Socket( host: "Address of the server", port: 53);
//     OutputStream out = socket.getOutputStream() ;
//     InputStream in = socket.getInputStream() ;
//
//     // Send a query in the form of a byte array
//     out.write(bytesToSend) ;
//     out.flush();
//
//     // Retrieve the response length, as described in RFC 1035 (4.2.2 TCP usage)
//     byte[] lengthBuffer = new byte[2] ;
//     in.read(lengthBuffer); // Verify it returns 2
//
//     // Convert bytes to length (data sent over the network is always big-endian)
//     int length = ((lengthBuffer[0] & 0xff) << 8) | (lengthBuffer[1] & 0xff) ;
//
//     // Retrieve the full response
//     byte[] responseBuffer = new byte[length] ;
//     in.read(responseBuffer) ; // Verify it returns the value of "length"
//
//     return responseBuffer ;
// }//fin query()
