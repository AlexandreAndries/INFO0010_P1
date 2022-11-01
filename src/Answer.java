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
    private static final short HEADER_LENGTH = 12 ;
    private static final short HEADER_WIDTH = 2 ;
    private static final short QRBIT = 16 ;
    private static final short RCODE_START = 31 ;
    private static final short RCODE_STOP = 27 ;
    private static final short ANCOUNT_B1 = 6 ;
    private static final short ANCOUNT_B2 = 7 ;
    private static final short SKIP_2BYTES = 2 ;
    // Class Variables
    private short ID ;
    private short ANCOUNT ;
    private byte[] header ;
    private byte[] question ;
    private byte[] answer ;
    private OutputData[] outputIPs ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // COMMENT !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    public Answer(byte[] ans, short id, short QSIZE) throws MessageException{
        short ansLength = (short)ans.length ;

        ID = id ;
        header = extractData(ans, (short)HEADER_LENGTH, (short)0, (short)HEADER_LENGTH);
        question = extractData(ans, (short)(QSIZE-HEADER_LENGTH), (short)HEADER_LENGTH, (short)QSIZE);
        answer = extractData(ans, (short)(ansLength-QSIZE), (short)QSIZE, (short)ansLength);

        ANCOUNT = readHeader(header) ;
        if(ANCOUNT == 0){
            throw new MessageException("Answer error : No answer received from DNS.");
        }

        outputIPs = readAnswer(ANCOUNT, answer) ;
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
    // Returns the IP extracted from the answer, to be printed to stdout
    public OutputData[] getOutputIPs(){
        return outputIPs;
    }//end getOutputIPs()
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
    /*- Public Methods -------------------------------------------------------*/
    /*------------------------------------------------------------------------*/

    // HERE do something to send back the necessay answer data to output on stdout !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

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
        // (Check above for RCODE position in Header) !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! bitsToInt func ??
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
    // get bit at specific position in byte array
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
    // ---------------------------------------------------------------------!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    private static OutputData[] readAnswer(short ANCOUNT, byte[] answer) throws MessageException{
        // Initialise position-index
        short pos = SKIP_2BYTES ;

        // Create array of ANCOUNT OutputData objects (corresponds to the
        // multiple answers received from DNS server)
        OutputData[] out = new OutputData[ANCOUNT];
        // Temporary variables to hold data extracted from the answers
        short tmpType ;
        short RDLength ;
        int ttl;
        String ip ;
        String type ;

        // Loop over the ANCOUNT answers sent by the DNS
        for(short i = 0 ; i < ANCOUNT ; i++){
            // Retrieve TYPE related data
            byte[] tmpB = {(byte) answer[pos], (byte) answer[pos+1]};
            ByteBuffer tmpBB = ByteBuffer.wrap(tmpB);
            tmpType = tmpBB.getShort() ;

            // Increase position-idx by 4 to index to TTL-related bytes
            pos += 4 ;

            // Retrieve TTL related data
            byte[] tmpT = {(byte) answer[pos],
                           (byte) answer[pos+1],
                           (byte) answer[pos+2],
                           (byte) answer[pos+3]};
            ByteBuffer tmpTT = ByteBuffer.wrap(tmpT);
            ttl = tmpTT.getInt() ;
            // Increase position-idx by 4 to index to RDLENGTH-related bytes
            pos += 4 ;

            // Retrieve length of RDATA
            byte[] tmpR = {(byte) answer[pos], (byte) answer[pos+1]};
            ByteBuffer tmpRR = ByteBuffer.wrap(tmpR);
            RDLength = tmpRR.getShort() ;

            // Increase position-idx by 4 to index to RDATA-related bytes
            pos += 2 ;

            // Retrieve RDATA
            switch(tmpType){
                case 1 :
                    type = "A" ;
                    byte[] tmpP = {(byte) answer[pos],
                                   (byte) answer[pos+1],
                                   (byte) answer[pos+2],
                                   (byte) answer[pos+3]};
                    ip = retIPTypeA(tmpP);
                    // set pos to next first byte of next answer for next iteration
                    pos += RDLength+2 ;
                    break ;
                case 16 :
                    type = "TXT";
                    pos++ ;
                    ByteBuffer bfr = ByteBuffer.allocate(RDLength);
                    for(short j = pos; j < pos+RDLength-1 ; j++){
                        bfr.put((byte) answer[j]) ;
                    }
                    tmpP = bfr.array() ;
                    ip = retIPTypeTXT(tmpP);
                    pos += RDLength+1 ;
                    break ;
                default :
                    type = null ;
                    ip = null ;
                    break ;
            }

            // ---> Create Output object from gathered data.
            out[i] = new OutputData(ttl, ip, type);
        }

        // Return OutputData array containing data to print to stdout
        return out;
    }//end readAnswer()
    /*------------------------------------------------------------------------*/
    // Translate RDATA to IP address for type A
    private static String retIPTypeA(byte[] rdata){
        String ip = String.valueOf(rdata[0] & 0xff);
        for (short i = 1; i < rdata.length; i++) {
            ip = ip + "." + String.valueOf(rdata[i] & 0xff);
        }
        return ip;
    }//end retIPTypeA()
    /*------------------------------------------------------------------------*/
    // Translate RDATA to IP address for type TXT
    private static String retIPTypeTXT(byte[] rdata){
        String str = Character.toString(rdata[0]);
        for(short i = 1; i < rdata.length ; i++){
            str = str + (char)rdata[i] ;
        }
        return str ;
    }//end retIPTypeTXT()
}//end class Answer
