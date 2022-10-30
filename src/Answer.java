import java.lang.* ;
import java.io.* ;
import java.nio.* ;
import java.net.* ;
import java.util.* ;

// Format of expected NS answer :
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


// class Query
public class Answer{
    /*------------------------------------------------------------------------*/
    /*- Variables ------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Class Constants
    private static final short HEADER_LENGTH = 12 ;
    private static final short HEADER_WIDTH = 2 ;
    // Class Variables
    private short ID ;
    private byte[] header ;
    private byte[] question ;
    private byte[] answer ;
    /*------------------------------------------------------------------------*/
    /*- Constructor ----------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    public Answer(byte[] ans, short id, short QSIZE){
        ID = id ;
        header = readHeader(ans);
        question = readQuestion(ans, QSIZE);
        answer = readAnswer(ans, QSIZE);
    }// Answer Object constructor
    /*------------------------------------------------------------------------*/
    /*- Getters --------------------------------------------------------------*/
    /*------------------------------------------------------------------------*/
    // Returns ID number of answer
    public short getID(){
        return ID;
    }//end getID()
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

    /*------------------------------------------------------------------------*/
    /*- Private Static Methods -----------------------------------------------*/
    /*------------------------------------------------------------------------*/
    private static byte[] readHeader(byte[] ans){
        ByteBuffer headerBfr = ByteBuffer.allocate(HEADER_LENGTH);
        for(short i = 0; i < HEADER_LENGTH ; i++){
            headerBfr.put((byte) ans[i]) ;
        }
        return headerBfr.array() ;
    }//end readHeader()
    /*------------------------------------------------------------------------*/
    private static byte[] readQuestion(byte[] ans, short QSIZE){
        ByteBuffer questionBfr = ByteBuffer.allocate(QSIZE);
        for(short i = HEADER_LENGTH; i < (HEADER_LENGTH+QSIZE) ; i++){
            questionBfr.put((byte) ans[i]) ;
        }
        return questionBfr.array() ;
    }//end readQuestion()
    /*------------------------------------------------------------------------*/
    private static byte[] readAnswer(byte[] ans, short QSIZE){
        short ansLength = (short)ans.length ;
        ByteBuffer answerBfr = ByteBuffer.allocate(ansLength - HEADER_LENGTH - QSIZE);
        for(short i = (short)(HEADER_LENGTH+QSIZE); i < ansLength ; i++){
            answerBfr.put((byte) ans[i]) ;
        }
        return answerBfr.array() ;
    }//end readAnswer()
}//end class Answer
