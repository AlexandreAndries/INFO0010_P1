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
    private static final short ANCOUNT_B1 = 6;
    private static final short ANCOUNT_B2 = 7;
    // Class Variables
    private short ID ;
    private short ANCOUNT ;
    private byte[] header ;
    private byte[] question ;
    private byte[] answer ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Answer(byte[] ans, short id, short QSIZE) throws AnswerException{
        short ansLength = (short)ans.length ;

        ID = id ;
        header = extractData(ans, (short)HEADER_LENGTH, (short)0, (short)HEADER_LENGTH);
        question = extractData(ans, (short)QSIZE, (short)HEADER_LENGTH, (short)(HEADER_LENGTH+QSIZE));
        answer = extractData(ans, (short)(ansLength-HEADER_LENGTH-QSIZE), (short)(HEADER_LENGTH+QSIZE), (short)ansLength);

        ANCOUNT = readHeader(header) ;

        // Now read data from the != fields and translate it
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

    // HERE do something to send back the necessay answer data to output on stdout

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
    private static short readHeader(byte[] header) throws AnswerException{
        // Check QR value. If QR != 1 : the answer is not an answer,
        // so return an error
        // (Check above for QR position in Header)
        if(getBit(header, QRBIT) != 1){
            throw new AnswerException("Answer : received message is not an answer.");
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
                throw new AnswerException("Answer error (RCODE=1): Format error.");
            case 2 :
                throw new AnswerException("Answer error (RCODE=2): Server Failure.");
            case 3 :
                throw new AnswerException("Answer error (RCODE=3): Name Error.");
            case 4 :
                throw new AnswerException("Answer error (RCODE=4): Not implemented.");
            case 5 :
                throw new AnswerException("Answer error (RCODE=5): Refused.");
            default :
                throw new AnswerException("Answer error (RCODE=6-15): Reserved.");
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

}//end class Answer
