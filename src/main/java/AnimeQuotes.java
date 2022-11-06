import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
//Ah what the heck...
import java.net.*;
import java.io.*;

public class AnimeQuotes {
    public static void main(String[] args) throws IOException {
        //Queue of requests from clients and the port number
        int qlen = 6;
        int port = 5050;
        //Creating a new ServerSocket to bind our normal socket to.
        ServerSocket serverSocket = new ServerSocket(port, qlen);
        Socket sock;
        System.out.println("Dan Wright's Anime Quote Server is running at port 5050. Give it a try!");
        while(true) {
            //Accepting the connection and creating a new thread of the WebServer; we're making a new worker thread.
            sock = serverSocket.accept();
            new WebServerWorker(sock).start();
        }
    }
}
