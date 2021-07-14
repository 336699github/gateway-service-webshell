package cn.objectspace.webshell.service.impl;

public class UnsupportedOpException extends Exception
{
    public UnsupportedOpException(String errMessage)
    {
        super(errMessage);
    }
}