package com.example.testdemo.utils;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Socket.io 单例
 */

public class SingletonSocket {

    private static Socket mSocket;

    public static Socket getSocket(){
        if(mSocket == null){
            try{
                mSocket = IO.socket("http://221.7.79.46:9002");
            }catch (URISyntaxException ex){

            }
        }
        return mSocket;
    }
}
