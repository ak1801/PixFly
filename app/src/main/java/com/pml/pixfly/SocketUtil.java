package com.pml.pixfly;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class SocketUtil{
    Socket clientSocket = null;
    String SERVER_URL = "10.10.10.15";
    int SERVER_PORT = 7858;

    public Socket getClientSocket() {
        Socket socket = null;
        try{
            socket = new Socket(SERVER_URL, SERVER_PORT);
        } catch(UnknownHostException e) {
            Log.e("Error : ","Unknown Host Exception occurred");
        } catch(IOException e) {
            Log.e("Error : ","IO Exception occurred");
        }
        return socket;
    }

    public String getResponse(DataInputStream inputStream) {
        BufferedReader br = null;
        String serverResp = null;
        try {
            br = new BufferedReader(new InputStreamReader(inputStream));
            serverResp = br.readLine().toString();
            Log.i("Server Response : ", serverResp);
        } catch (UnknownHostException ue) {
            System.err.println("Invalid hostname");
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: hostname");
        }
        return serverResp;
    }

    public String createRequest(String cmd, DroneCodes.MODE mode, double latitude, double longitude) {
        Payload payload = new Payload();
        payload.setCommand(cmd);
        payload.setMode(mode);
        payload.setLat(latitude);
        payload.setLon(longitude);
        Gson gson = new Gson();
        return gson.toJson(payload);
    }
}
