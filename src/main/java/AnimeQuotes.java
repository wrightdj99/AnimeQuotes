import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class AnimeQuotes {

    public static void main(String[] args) throws IOException {
        int qlen = 6;
        int port = 5050;
        ServerSocket serverSocket = new ServerSocket(port, qlen);
        Socket sock;
        /*try {
            sock = serverSocket.accept();
            new WebServerWorker(sock).start();
            System.out.println("Connected.");
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        System.out.println("Dan Wright's Anime Quote Server is running at port 5050");
        while(true) {
            sock = serverSocket.accept();
            new WebServerWorker(sock).start();
        }
    }
}
