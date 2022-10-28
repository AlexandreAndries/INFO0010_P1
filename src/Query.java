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
    private static final short QSIZE_BYTES = 2 ;
    private static int QuerySize = 0;
    private byte[] header ;
    private byte[] question ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Query(String url, String qtype){
        this.header = createHeader();
        this.question = createQuestion(url, qtype);
        this.QuerySize = header.length + question.length + QSIZE_BYTES ;
    }// Query Object constructor
    /*------------------------------------------------------------------------*/
    /*- Getters  -------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public byte[] getHeader(){
        return header;
    }//fin getHeader()
    /*------------------------------------------------------------------------*/
    public byte[] getQuestion(){
        return question;
    }//fin getQuestion()
    /*------------------------------------------------------------------------*/
    public short getQuerySize(){
        return (short)QuerySize;
    }//fin getQuerySize()
    /*------------------------------------------------------------------------*/
    /*- Public Methods -------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    static byte[] buildQuery(Query query){
        short qsize = query.getQuerySize();
        ByteBuffer bytesToSend = ByteBuffer.allocate(qsize) ;
        bytesToSend.putShort((short)qsize);
        bytesToSend.put(query.getHeader());
        bytesToSend.put(query.getQuestion());

        return bytesToSend.array() ;
    }// COMMENT
    /*------------------------------------------------------------------------*/
    static byte[] query(byte[] bytesToSend) throws IOException {
        // Initiate a new TCP connection with a Socket
        Socket socket = new Socket("139.165.99.199",53);
        OutputStream out = socket.getOutputStream() ;
        InputStream in = socket.getInputStream() ;

        // Send a query in the form of a byte array
        out.write(bytesToSend) ;
        out.flush();

        // Retrieve the response length, as described in RFC 1035 (4.2.2 TCP usage)
        byte[] lengthBuffer = new byte[2] ;
        System.out.println(in.read(lengthBuffer)); // Verify it returns 2

        // Convert bytes to length (data sent over the network is always big-endian)
        int length = ((lengthBuffer[0] & 0xff) << 8) | (lengthBuffer[1] & 0xff) ;

        // Retrieve the full response
        byte[] responseBuffer = new byte[length] ;
        in.read(responseBuffer) ; // Verify it returns the value of "length"

        return responseBuffer ;
    }//fin query()
    /*------------------------------------------------------------------------*/
    /*- Private Static Methods -----------------------------------------------*/
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
    private static byte[] createQuestion(String url, String type){
        // First, split the url in its different sections.
        // This is necessary to compute the nbr of bytes QNAME will contain.
        String[] urlSections = url.split("\\.", 0);

        // Compute number of sections in the url (eg: www.uliege.be has 3 sections)
        int nbrOfSections = urlSections.length ;

        // Compute number of characters in the url
        int nbrOfChar = 0;
        for(int i = 0; i < nbrOfSections ; i++){
            for(int j = 0; j < urlSections[i].length() ; j++){
                nbrOfChar++;
            }
        }

        // Create ByteBuffer of appropriate length
        int bufferLength = nbrOfChar+nbrOfSections+QEND_BYTE+2*HEADER_WIDTH;
        ByteBuffer question = ByteBuffer.allocate(bufferLength);

        // Fill buffer with length of section followed by char value on bytes
        // for each section of the url.
        for(int i = 0; i < nbrOfSections ; i++){
            question.put((byte) urlSections[i].length());
            for(int j = 0; j < urlSections[i].length(); j++){
                question.put((byte) ((int) urlSections[i].charAt(j)));
            }
        }

        // Add zero byte to end QNAME
        question.put((byte) 0x00);

        // Get QTYPE value corresponding to type argument.
        byte[] QTYPE = getQType(type) ;
        // Copy QTYPE in the question buffer.
        question.put(QTYPE);

        // QCLASS is IN for internet, so QCLASS set to 1.
        question.put((byte) 0x00);
        question.put((byte) 0x01);

        // Return created question
        return question.array() ;
    }//fin createQuestion()
    /*------------------------------------------------------------------------*/
    // fonc QTYPE
    private static byte[] getQType(String type){ //--> doit renvoyer le byte[] de qtype
        ByteBuffer TYPE = ByteBuffer.allocate(QSIZE_BYTES);
        switch(type){
            case "A" :
                TYPE.put((byte) 0x00);
                TYPE.put((byte) 0x01);
                break;

            case "TXT" :
                TYPE.put((byte) 0x00);
                TYPE.put((byte) 0x10);
                break;

            default :
                TYPE.put((byte) 0x00);
                TYPE.put((byte) 0x01);
                System.out.println("Non-conform QTYPE. Default : A\n");
                break;
        }

        return TYPE.array();
    }
    // fonc buffer length --> calcule la taille du buffer pour la question
    //etc


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
    public static void main(String args[]) throws IOException{
        Query msg = new Query("ddi.uliege.be","A");
        byte[] q = buildQuery(msg);
        // print(msg.getHeader());
        // print(msg.getQuestion());
        // print(buildQuery(msg));
        byte[] ans = query(q) ;


    }//fin main
//-----------------------------------------------------------------------------------------------------------
}//fin class Query
