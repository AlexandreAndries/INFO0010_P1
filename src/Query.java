import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// class Query
public class Query{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Class Constants
    private static final short HEADER_LENGTH = 12 ;
    private static final short HEADER_WIDTH = 2 ;
    private static final short QEND_BYTE = 1 ;
    private static final short QSIZE_BYTES = 2 ;
    // Class Variables
    private short ID ;
    private int QSIZE = 0;
    private String dnsIP ;
    private String url ;
    private byte[] bytesToSend ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Query(String url, String dns, String qtype){
        this.dnsIP = dns ;
        this.url = url ;
        byte[] header = createHeader(this);
        byte[] question = createQuestion(url, qtype);
        int qsize = header.length + question.length ;
        this.QSIZE = qsize ;

        ByteBuffer buffer = ByteBuffer.allocate(qsize + (short)QSIZE_BYTES) ;
        buffer.putShort((short)qsize);
        buffer.put(header);
        buffer.put(question);
        this.bytesToSend = buffer.array();
    }// Query Object constructor
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
    // Returns query size
    public short getQSIZE(){
        return (short)QSIZE;
    }//end getQSIZE()
    /*------------------------------------------------------------------------*/
    // Returns Query ID
    public short getID(){
        return ID;
    }//end getID()
    /*------------------------------------------------------------------------*/
    // Returns bytes (array) to transmit to DNS
    public byte[] getBytesToSend(){
        return bytesToSend;
    }//end getBytesToSend()
    /*------------------------------------------------------------------------*/
    /*- Public Methods -------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public byte[] query(byte[] bytesToSend) throws IOException {
        // Initiate a new TCP connection with a Socket
        Socket socket = new Socket(this.dnsIP,53);
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
    }//end query()
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
    private static byte[] createHeader(Query query){
        // Allocate a ByteBuffer of size 12 (for 6 * 2 bytes)
        ByteBuffer header = ByteBuffer.allocate(HEADER_LENGTH) ;

        // Generate random ID bytes for the header ID,
        // save ID to object var (as int) and insert bytes in ByteBuffer
        byte[] idBytes = generateID() ;
        ByteBuffer idBB = ByteBuffer.wrap(idBytes);
        query.ID = idBB.getShort() ;
        header.put(idBytes);

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
    }//end createHeader()
    /*------------------------------------------------------------------------*/
    private static byte[] generateID(){
        Random rand = new Random() ;
        byte[] randID = new byte[HEADER_WIDTH] ;
        rand.nextBytes(randID);

        return randID ;
    }//end generateID()
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

        // Compute appropriate length of required ByteBuffe
        int bufferLength = computeBufferLength(urlSections);
        // Create ByteBuffer of appropriate length
        ByteBuffer question = ByteBuffer.allocate(bufferLength);

        // Fill buffer with length of section followed by char value on bytes
        // for each section of the url.
        for(int i = 0; i < urlSections.length ; i++){
            question.put((byte) urlSections[i].length());
            for(int j = 0; j < urlSections[i].length(); j++){
                question.put((byte) ((int) urlSections[i].charAt(j)));
            }
        }

        // Add zero byte to end QNAME
        question.put((byte) 0x00);

        // Get QTYPE value corresponding to type argument.
        byte[] QTYPE = computeQTYPE(type) ;
        // Copy QTYPE in the question buffer.
        question.put(QTYPE);

        // QCLASS is IN for internet, so QCLASS set to 1.
        question.put((byte) 0x00);
        question.put((byte) 0x01);

        // Return created question
        return question.array() ;
    }//end createQuestion()
    /*------------------------------------------------------------------------*/
    // Compute QTYPE value
    private static byte[] computeQTYPE(String type){
        ByteBuffer TYPE = ByteBuffer.allocate(QSIZE_BYTES);

        // Encode bytes according to the question type (either A or TXT). By
        // default, A is selected.
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
    }//end computeQTYPE()
    /*------------------------------------------------------------------------*/
    // Compute length of ByteBuffer
    private static int computeBufferLength(String[] urlSections){
        // Compute number of sections in the url (eg: www.uliege.be has 3 sections)
        int nbrOfSections = urlSections.length ;

        // Compute overall number of characters in the url
        int nbrOfChar = 0;
        for(int i = 0; i < nbrOfSections ; i++){
            for(int j = 0; j < urlSections[i].length() ; j++){
                nbrOfChar++;
            }
        }

        // Compute appropriate length of required ByteBuffer
        int bufferLength = nbrOfChar+nbrOfSections+QEND_BYTE+2*HEADER_WIDTH;

        return bufferLength ;
    }//end computeBufferLength()
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
    }//end toBits()
    /*------------------------------------------------------------------------*/
    static void print(byte[] byteArray) {
        byte[] array = byteArray;
        for(int i = 0 ; i < array.length ; i++){
            System.out.println(toBits(array[i]));
        }
    }//end toBitArray()
//-----------------------------------------------------------------------------------------------------------
}//end class Query
