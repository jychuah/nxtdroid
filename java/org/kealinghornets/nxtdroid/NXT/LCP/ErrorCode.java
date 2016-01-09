package org.kealinghornets.nxtdroid.NXT.LCP;

/**
 * Error code constants, specified by the NXT Direct Command reference.
 */
public class ErrorCode {
    public static final byte PENDING_COMMUNICATION = (byte)0x20;
    public static final byte SPECIFIED_MAILBOX_EMPTY = (byte) 0x40;
    public static final byte REQUEST_FAILED = (byte)0xBD;
    public static final byte UNKNOWN_COMMAND_OPCODE =  (byte)0xBE;
    public static final byte INSANE_PACKET = (byte) 0xBF;
    public static final byte DATA_VALUES = (byte) 0xC0;
    public static final byte COMMUNICATION_BUS =  (byte)0xDD;
    public static final byte COMMUNICATION_BUFFER = (byte)0xDE;
    public static final byte CONNECTION_NOT_VALID = (byte)0xDF;
    public static final byte CONNECTION_NOT_CONFIGURED = (byte)0xE0;
    public static final byte NO_ACTIVE_PROGRAM = (byte)0xEC;
    public static final byte ILLEGAL_SIZE_SPECIFIED = (byte)0xED;
    public static final byte ILLEGAL_MAILBOX_ID_SPECIFIED = (byte)0xEE;
    public static final byte INVALID_FIELD = (byte)0xEF;
    public static final byte BAD_INPUT_OUTPUT = (byte)0xF0;
    public static final byte INSUFFICIENT_MEMORY = (byte)0xFB;
    public static final byte BAD_ARGUMENTS = (byte)0xFF;
    public static final byte UNKNOWN = (byte)-1;
    public static final byte OK = (byte)0;

    /**
     * Gets the message corresponding to a given error code
     * @param code The error code to translate
     * @return A String with the long error code description
     */
    public static String toString(byte code) {
        switch(code) {
            case PENDING_COMMUNICATION : return "Pending communication transaction in progress";
            case SPECIFIED_MAILBOX_EMPTY : return "Specified mailbox queue is empty";
            case REQUEST_FAILED : return "Request failed (i.e. specified file not found)";
            case UNKNOWN_COMMAND_OPCODE : return "Unknown command opcode";
            case INSANE_PACKET : return "Insane packet";
            case DATA_VALUES : return "Data contains out of range values";
            case COMMUNICATION_BUS : return "Communication bus error";
            case COMMUNICATION_BUFFER : return "No free memory in communication buffer";
            case CONNECTION_NOT_VALID : return "Specified channel/connection is not valid";
            case CONNECTION_NOT_CONFIGURED : return "Specified channel/connection not configured or busy";
            case NO_ACTIVE_PROGRAM : return "No active program";
            case ILLEGAL_SIZE_SPECIFIED : return "Illegal size specified";
            case ILLEGAL_MAILBOX_ID_SPECIFIED : return "Illegal mailbox queue ID specified";
            case INVALID_FIELD : return "Attempted to access invalid field of structure";
            case BAD_INPUT_OUTPUT : return "Bad input or output specified";
            case INSUFFICIENT_MEMORY : return "Insufficient memory available";
            case BAD_ARGUMENTS : return "Bad arguments";
            case OK : return "OK";
            default : return "UNKNOWN";
        }
    }
}
