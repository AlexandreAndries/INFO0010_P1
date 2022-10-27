import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// class Query - can be a Query or an Answer
public class Query{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    private static final short HEADER_LENGTH = 12 ;
    private static final short HEADER_WIDTH = 2 ;
    private static final short QEND_BYTE = 1 ;
    private byte[] header ;
    private byte[] question ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Query(/*String url*/){
        this.header = createHeader();
        // this.question = createQuestion(url);
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
    // Question section format (RFC 1035)
    //                                 1  1  1  1  1  1
    //   0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                                               |
    // /                     QNAME                     /
    // /                                               /
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                     QTYPE                     |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    // |                     QCLASS                    |
    // +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
    private static byte[] createQuestion(String url){
        // First, split the url in its different sections.
        // This is necessary to compute the nbr of bytes QNAME will contain.
        String[] urlSections = url.split(".", 0);

        // Compute number of sections in the url (eg: www.uliege.be has 3 sections)
        int nbrOfSections = urlSections.length ;

        // Compute number of characters in the url
        int nbrOfChar = 0;
        for(int i = 0; i < nbrOfSections ; i++){
            for(int j = 0; j < urlSections[i].length ; j++){
                nbrOfChar++;
            }
        }

        // Create ByteBuffer of appropriate length
        int bufferLength = nbrOfChar+nbrOfSections+QEND_BYTE+2*HEADER_WIDTH;
        ByteBuffer question = ByteBuffer.allocate(bufferLength);

        // Fill buffer -------------------------------!!!!!!!

        // Return created question
        return question.array() ;
    }//fin createQuestion()
    /*------------------------------------------------------------------------*/




//-----------------------------------------------------------------------------------------------------------
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
    /*------------------------------------------------------------------------*/
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
        Query msg = new Query();

        print(msg.getHeader());
    }//fin main
//-----------------------------------------------------------------------------------------------------------
}//fin class Query
