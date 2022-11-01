/**
 * \file Answer.java
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
  * Answer class is used to represent the answer received from the DNS.
  * An answer object is built according to the RFC1035 standards.
  *
  */
import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// Format of expected NS answer (RFC 1035):
//
//    +---------------------+
//    |        Header       |
//    +---------------------+
//    |       Question      | the question for the name server
//    +---------------------+
//    |        Answer       | RRs answering the question
//    +---------------------+
//    |      Authority      | RRs pointing toward an authority
//    +---------------------+
//    |      Additional     | RRs holding additional information
//    +---------------------+
//
// With following format for the different sections :
//
// Header section format (RFC 1035):
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
//
// Question section format (RFC 1035):
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
//
// Answer section format (RFC 1035):
//                                 1  1  1  1  1  1
//   0  1  2  3  4  5  6  7  8  9  0  1  2  3  4  5
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
// |                                               |
// /                                               /
// /                      NAME                     /
// |                                               |
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
// |                      TYPE                     |
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
// |                     CLASS                     |
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
// |                      TTL                      |
// |                                               |
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+
// |                   RDLENGTH                    |
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--|
// /                     RDATA                     /
// /                                               /
// +--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+--+

// class Answer
public class Answer{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Class Constants
    private static final short HEADER_LENGTH = 12 ;  // Length in bytes of header section
    private static final short HEADER_WIDTH = 2 ;    // Length of 2 bytes used by data in header
    private static final short QRBIT = 16 ;          // Position of QR bit in the header
    private static final short RCODE_START = 31 ;    // Position of first RCODE bit in the header
    private static final short RCODE_STOP = 27 ;     // Position of last RCODE bit in the header
    private static final short ANCOUNT_B1 = 6 ;      // Position of first ANCOUNT byte in the header
    private static final short ANCOUNT_B2 = 7 ;      // Position of last ANCOUNT byte in the header
    private static final short SKIP_2BYTES = 2 ;     // Position diff to skip 2 bytes
    private static final short SKIP_4BYTES = 4 ;     // Position diff to skip 4 bytes
    // Class Variables
    private short ID ;                               // ID of the answer (short value)
    private short ANCOUNT ;                          // Answer count sent by answer
    private byte[] header ;                          // byte array of the header section from the answer
    private byte[] question ;                        // byte array of the question section from the answer
    private byte[] answer ;                          // byte array of the proper answer section from the answer
    private OutputData[] output ;                    // output created from parsed data
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Create an answer object from the byte array received from the DNS.
    // The answer is split in its different sections (header, question, answer)
    // These are then parsed to extract the necessary data.
    public Answer(byte[] ans, short id, short QSIZE) throws MessageException{
        short ansLength = (short)ans.length ;

        // Retrieve ans ID and split sections
        ID = id ;
        header = extractData(ans, (short)HEADER_LENGTH, (short)0, (short)HEADER_LENGTH);
        question = extractData(ans, (short)(QSIZE-HEADER_LENGTH), (short)HEADER_LENGTH, (short)QSIZE);
        answer = extractData(ans, (short)(ansLength-QSIZE), (short)QSIZE, (short)ansLength);

        // Read header data - if ANCOUNT = 0 : no valid answer has been received
        ANCOUNT = readHeader(header) ;
        if(ANCOUNT == 0){
            throw new MessageException("Answer error : No answer received from DNS.");
        }

        // Read answer section and extract the data to output
        output = readAnswer(ANCOUNT, answer) ;
    }// Answer Object constructor
    /*------------------------------------------------------------------------*/
    /*- Getters --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Returns ID number of answer
    public short getID(){
        return ID;
    }//end getID()
    /*------------------------------------------------------------------------*/
    // Returns ANCOUNT number of answer
    public short getANCOUNT(){
        return ANCOUNT;
    }//end getANCOUNT()
    /*------------------------------------------------------------------------*/
    // Returns the data extracted from the answer, to be printed to stdout
    public OutputData[] getOutput(){
        return output;
    }//end getOutput()
    /*------------------------------------------------------------------------*/
    // Returns header section of answer
    public byte[] getHeader(){
        return header;
    }//end getHeader()
    /*------------------------------------------------------------------------*/
    // Returns question section of answer
    public byte[] getQuestion(){
        return question;
    }//end getQuestion()
    /*------------------------------------------------------------------------*/
    // Returns answer section of answer
    public byte[] getAnswer(){
        return answer;
    }//end getAnswer()
    /*------------------------------------------------------------------------*/
    /*- Private Static Methods -----------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // extract bytes from specific sections
    private static byte[] extractData(byte[] ans, short size, short start, short end){
        ByteBuffer bfr = ByteBuffer.allocate(size);
        for(short i = start; i < end ; i++){
            bfr.put((byte) ans[i]) ;
        }
        return bfr.array() ;
    }//end extractData()
    /*------------------------------------------------------------------------*/
    // Asserts that the answer is in fact an answer and not a query, returns the
    // value of ANCOUNT
    private static short readHeader(byte[] header) throws MessageException{
        // Check QR value. If QR != 1 : the answer is not an answer,
        // so return an error
        // (Check above for QR position in Header)
        if(getBit(header, QRBIT) != 1){
            throw new MessageException("Answer error : Received message is not an answer.");
        }

        // Check RCODE value. If RCODE != 0 : return the corresponding error.
        // (Check above for RCODE position in Header)
        short RCODE = 0;
        short pow = 0;
        for(short i = RCODE_START ; i < RCODE_STOP ; i--){
            RCODE += (short)Math.pow((double)getBit(header, i), (double)pow) ;
            pow++;
        }

        switch(RCODE){
            case 0 :
                break;
            case 1 :
                throw new MessageException("Answer error (RCODE=1): Format error.");
            case 2 :
                throw new MessageException("Answer error (RCODE=2): Server Failure.");
            case 3 :
                throw new MessageException("Answer error (RCODE=3): Name Error.");
            case 4 :
                throw new MessageException("Answer error (RCODE=4): Not implemented.");
            case 5 :
                throw new MessageException("Answer error (RCODE=5): Refused.");
            default :
                throw new MessageException("Answer error (RCODE=6-15): Reserved.");
        }

        // Return the ANCOUNT of RR in the answer section.
        // (Check above for ANCOUNT position in Header)
        byte[] ancount = {(byte) header[ANCOUNT_B1], (byte) header[ANCOUNT_B2]};
        ByteBuffer ancountBB = ByteBuffer.wrap(ancount);
        return ancountBB.getShort() ;
    }//end readHeader()
    /*------------------------------------------------------------------------*/
    // Get bit at specific position in byte array
    private static int getBit(byte[] byteArray, int idx) {
        // get byte to which idx belongs to in the byte array
        int idxByte = idx/8;
        // get bit position to which idx corresponds in byte
        int idxBit = idx%8;
        // extract byte at position idx from byte array
        byte valByte = byteArray[idxByte];
        // get wanted bit in the extracted byte
        int bit = valByte>>(8-(idxBit+1)) & 0x0001;

        return bit;
    }//end getBit()
    /*------------------------------------------------------------------------*/
    // Read answer section and extarct the data sent by the name server.
    // Only A and TXT type answers are taken into account, the rest is
    // purposedly ignored.
    // The process of parsing the answer is repeated ANCOUNT times in a loop.
    private static OutputData[] readAnswer(short ANCOUNT, byte[] answer) throws MessageException{
        // Initialise position-index
        short pos = SKIP_2BYTES ;

        // Create array of ANCOUNT OutputData objects (corresponds to the
        // multiple answers received from DNS server)
        OutputData[] out = new OutputData[ANCOUNT];
        // Temporary variables to hold data extracted from the answers
        short tmpTYPE ;
        short RDLength ;
        int ttl;
        String data ;
        String type ;

        // Loop over the ANCOUNT answers sent by the DNS
        for(short i = 0 ; i < ANCOUNT ; i++){
            // Retrieve TYPE related data
            byte[] tmpTp = {(byte) answer[pos],
                            (byte) answer[pos+1]};
            tmpTYPE = toShort(tmpTp) ;

            // Increase position-idx by 4 to index to TTL-related bytes
            pos += SKIP_4BYTES ;

            // Retrieve TTL related data
            byte[] tmpTTL = {(byte) answer[pos],
                             (byte) answer[pos+1],
                             (byte) answer[pos+2],
                             (byte) answer[pos+3]};
            ttl = toInt(tmpTTL) ;
            // Increase position-idx by 4 to index to RDLENGTH-related bytes
            pos += SKIP_4BYTES ;

            // Retrieve length of RDATA
            byte[] tmpRDL = {(byte) answer[pos],
                             (byte) answer[pos+1]};
            RDLength = toShort(tmpRDL) ;

            // Increase position-idx by 4 to index to RDATA-related bytes
            pos += SKIP_2BYTES ;

            // Retrieve RDATA (either type TXT or type A)
            switch(tmpTYPE){
                case 1 :
                    type = "A" ;
                    byte[] tmpP = {(byte) answer[pos],
                                   (byte) answer[pos+1],
                                   (byte) answer[pos+2],
                                   (byte) answer[pos+3]};
                    data = retDataTypeA(tmpP);
                    // set pos to next first byte of next answer for next iteration
                    pos += RDLength+2 ;
                    break ;
                case 16 :
                    type = "TXT";
                    pos++ ; // ignore first byte of RDATA
                    ByteBuffer bfr = ByteBuffer.allocate(RDLength);
                    for(short j = pos; j < pos+RDLength-1 ; j++){
                        bfr.put((byte) answer[j]) ;
                    }
                    tmpP = bfr.array() ;
                    data = retDataTypeTXT(tmpP);
                    // set pos to next first byte of next answer for next iteration
                    pos += RDLength+1 ;
                    break ;
                default :
                    type = null ;
                    data = null ;
                    break ;
            }

            if(type == null || data == null){
                // Ignore non-supported qtype
                out[i] = null ;
            }else{
                // ---> Create Output object from gathered data.
                out[i] = new OutputData(ttl, data, type);
            }
        }

        // Return OutputData array containing data to print to stdout
        return out;
    }//end readAnswer()
    /*------------------------------------------------------------------------*/
    // Convert byte array of size 4 to its int value
    private static int toInt(byte[] array) throws MessageException{
        // an Integer is written on 4 bytes
        if(array.length != 4){
            throw new MessageException("Answer Error : wrong byte array length.");
        }
        ByteBuffer tmpBB = ByteBuffer.wrap(array);
        return tmpBB.getInt() ;
    }//end toInt()
    /*------------------------------------------------------------------------*/
    // Convert byte array of size 2 to its short value
    private static short toShort(byte[] array) throws MessageException{
        // a Short is written on 2 bytes
        if(array.length != 2){
            throw new MessageException("Answer Error : wrong byte array length.");
        }
        ByteBuffer tmpBB = ByteBuffer.wrap(array);
        return tmpBB.getShort() ;
    }//end toShort()
    /*------------------------------------------------------------------------*/
    // Translate RDATA for type A
    private static String retDataTypeA(byte[] rdata){
        String ip = String.valueOf(rdata[0] & 0xff);
        for (short i = 1; i < rdata.length; i++) {
            ip = ip + "." + String.valueOf(rdata[i] & 0xff);
        }
        return ip;
    }//end retDataTypeA()
    /*------------------------------------------------------------------------*/
    // Translate RDATA for type TXT
    private static String retDataTypeTXT(byte[] rdata){
        String str = Character.toString(rdata[0]);
        for(short i = 1; i < rdata.length ; i++){
            str = str + (char)rdata[i] ;
        }
        return str ;
    }//end retDataTypeTXT()
}//end class Answer
