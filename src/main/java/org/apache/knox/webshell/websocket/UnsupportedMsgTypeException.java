package org.apache.knox.webshell.websocket;


public class UnsupportedMsgTypeException extends Exception
{
    public UnsupportedMsgTypeException(String errMessage)
    {
        super(errMessage);
    }
}