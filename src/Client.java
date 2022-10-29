import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// class Client - main program
public class Client{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    private String dnsIP ;
    private String url ;
    private String qType ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // public Client(String[] args){
    //     this.manageArgs(args);
    // }
    // /*------------------------------------------------------------------------*/
    // /*- Private Static Methods -----------------------------------------------*/
    // /*------------------------------------------------------------------------*/
    // private static void manageArgs(String[] args){
    //     //Hh
    // }

    /*------------------------------------------------------------------------*/
    /*- Print ----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    static void stdoutQuestion(String dnsIP, String url, String qType){
        System.out.println("Question (NS=" + dnsIP
                                           + ", NAME="
                                           + url
                                           + ", TYPE="
                                           + qType
                                           +")");
    }
    /*------------------------------------------------------------------------*/
    /*- Main -----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public static void main(String args[]) throws IOException{
        // Need to iterate on main args ?
        // Client client = new Client(args) ;
        Query msg = new Query("ddi.uliege.be", "139.165.99.199", "A");

        // print(msg.getBytesToSend());
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
