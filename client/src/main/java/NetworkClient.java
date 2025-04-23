package com.example.javafxapp.network;

import com.example.javafxapp.Message;
import com.example.javafxapp.MessageType;
import java.io.*;
import java.net.Socket;

public class NetworkClient {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public NetworkClient(String serverIP, int serverPort) throws IOException {
        socket = new Socket(serverIP, serverPort);

        // Important: ObjectOutputStream must be created before ObjectInputStream!
        out = new ObjectOutputStream(socket.getOutputStream());
        out.flush();
        in = new ObjectInputStream(socket.getInputStream());
    }

    // Send a message to the server
    public void sendMessage(Message message) throws IOException {
        out.writeObject(message);
        out.flush();
    }

    // Receive a message from the server
    public Message readMessage() throws IOException, ClassNotFoundException {
        return (Message) in.readObject();
    }

    // Close the connection
    public void close() throws IOException {
        in.close();
        out.close();
        socket.close();
    }
}

