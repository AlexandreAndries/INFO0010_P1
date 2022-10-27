import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// class Message - can be a Query or an Answer
public class Message{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    private static final short HEADER_LENGTH = 12 ;
    private static final short HEADER_WIDTH = 2 ;
    private byte[] header ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Message(){
        this.header = createHeader();
    }
    /*------------------------------------------------------------------------*/
    /*- Getters  -------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public byte[] getHeader(){
        return header;
    }
    /*------------------------------------------------------------------------*/
    /*- Methods --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Header section format (RFC 1035)
    //                                 1  1  1  1  1  1
    //   0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                      ID                       |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |QR|   Opcode  |AA|TC|RD|RA|   Z    |   RCODE   |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                    QDCOUNT                    |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                    ANCOUNT                    |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                    NSCOUNT                    |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                    ARCOUNT                    |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    private static byte[] createHeader(){
        // Allocate a ByteBuffer of size 12 (for 6 * 2 bytes)
        ByteBuffer header = ByteBuffer.allocate(HEADER_LENGTH) ;

        // Generate random ID bytes for the header ID and insert them in ByteBuffer
        header.put(generateID());

        // Set option flags in header :
        //  - Query message, so QR set to 0
        //  - Standard query, so 4bit opcode set to 0
        //  - Query message, so AA not valid, set to 0
        //  - No truncation, so TC set to 0
        //  - Recursion desired, so RD set to 1
        //  - Z must be zero
        //  - RCODE must be zeros
        header.put((byte) 0x01);
        header.put((byte) 0x00);

        // Sending 1 question, so QDCOUNT set to 1
        header.put((byte) 0x00);
        header.put((byte) 0x01);

        // All remaining bits in the header kept at 0 as needed

        // Return created header
        return header.array() ;
    }//fin createHeader()
    /*------------------------------------------------------------------------*/
    private static byte[] generateID(){
        Random rand = new Random() ;
        byte[] randID = new byte[HEADER_WIDTH] ;
        rand.nextBytes(randID);

        return randID ;
    }//fin generateID()
    /*------------------------------------------------------------------------*/

    /*------------------------------------------------------------------------*/
    /*- Print ----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    static String toBits(final byte val) {
        final StringBuilder result = new StringBuilder();

        for(int i=0; i<8; i++){
            result.append((int)(val >> (8-(i+1)) & 0x0001));
        }
        return result.toString();
    }//fin toBits()

    static void print(byte[] byteArray) {
        byte[] array = byteArray;
        for(int i = 0 ; i < array.length ; i++){
            System.out.println(toBits(array[i]));
        }
    }//fin toBitArray()
    /*------------------------------------------------------------------------*/
    /*- Main -----------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public static void main(String args[]){
        Message msg = new Message();

        print(msg.getHeader());
    }//fin main
}//fin class Client
