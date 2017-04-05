package com.pml.pixfly.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by aksmahaj on 12/4/2016.
 */
public class WorkerRunnable implements Runnable {

    protected Socket clientSocket = null;
    protected String request = null;
    DataOutputStream os = null;
    DataInputStream is = null;

    public WorkerRunnable(Socket clientSocket, String request) {
        this.clientSocket = clientSocket;
        this.request = request;
    }

    public void run() {

        try{Thread.sleep(2000);}catch(InterruptedException e){System.out.println(e);}
        //Toast.makeText(getApplicationContext(), "Your Location is - \nLat: " + latitude + "\nLong: " + longitude, Toast.LENGTH_LONG).show();
        try {
            os = new DataOutputStream(clientSocket.getOutputStream());
            is = new DataInputStream(clientSocket.getInputStream());

            if (clientSocket != null && os != null && is != null) {
                os.writeBytes(request.length() + "|");
                os.writeBytes(request);
                clientSocket.shutdownOutput();
                    BufferedReader br = null;
                    String serverResp = null;
                    try {
                        br = new BufferedReader(new InputStreamReader(is));
                        serverResp = br.readLine().toString();
                        Log.i("Server Response : ", serverResp);
                    } catch (UnknownHostException ue) {
                        System.err.println("Invalid hostname");
                    } catch (IOException e) {
                        System.err.println("Couldn't get I/O for the connection to: hostname");
                    }

                os.close();
                is.close();
                clientSocket.close();
            }

        } catch (UnknownHostException ue) {
            System.err.println("Don't know about host: hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}