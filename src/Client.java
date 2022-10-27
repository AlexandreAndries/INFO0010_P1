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

    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------*/
    /*- Methods --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public byte[] query(byte[] bytesToSend) throws IOException {
        // Initiate a new TCP connection with a Socket
        Socket socket = new Socket( host: "Address of the server", port: 53);
        OutputStream out = socket.getOutputStream() ;
        InputStream in = socket.getInputStream() ;

        // Send a query in the form of a byte array
        out.write(bytesToSend) ;
        out.flush();

        // Retrieve the response length, as described in RFC 1035 (4.2.2 TCP usage)
        byte[] lengthBuffer = new byte[2] ;
        in.read(lengthBuffer); // Verify it returns 2

        // Convert bytes to length (data sent over the network is always big-endian)
        int length = ((lengthBuffer[0] & 0xff) << 8) | (lengthBuffer[1] & 0xff) ;

        // Retrieve the full response
        byte[] responseBuffer = new byte[length] ;
        in.read(responseBuffer) ; // Verify it returns the value of "length"

        return responseBuffer ;
    }//fin query()
    /*------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------*/
    /*- Print ----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------*/
    /*- Main -----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // public static void main(String args[]){
    //
    // }
}//fin class Client
