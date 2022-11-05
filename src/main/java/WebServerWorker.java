import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.io.*;
import java.net.*;

public class WebServerWorker extends Thread{
    Socket sock;
    WebServerWorker(Socket s){sock = s;}
    StringBuilder htmlString = new StringBuilder();
    PrintStream out = null;
    BufferedReader in = null;
    JSONParser parser = new JSONParser();
    JSONObject jo = new JSONObject();
    String htmlResponse = "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<title>" +
            "Anime Library" +
            "</title>" +
            "</head>" +
            "<body>" +
            "<h1>This is the Anime Quote Library! Press the button below to get a random quote from it!</h1>" +
            "<form method=\"get\" action=\"http://localhost:5050/getQuote\">" +
            "<input type=\"submit\" value=\"Submit\"><br>" +
            "</form>";

    public void run(){
        String inputRequest;
        htmlString.append(htmlResponse);
        try{
            out = new PrintStream(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out.println("HTTP/1.1 200 OK");
            out.println("Connection: close"); // Can fool with this.
            out.println("Content-Length: 1000"); // Lazy, so set high. Calculate later.
            out.println("Content-Type: text/html \r\n\r\n");
            inputRequest = in.readLine();
            if(inputRequest != null && inputRequest.contains("getQuote")) {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://anime-quotes1.p.rapidapi.com/api/random"))
                        .header("X-RapidAPI-Key", "cc1d38da1fmsh73b309d9673daccp1c4109jsnf02b4cfb0a4e")
                        .header("X-RapidAPI-Host", "anime-quotes1.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                jo.put("HTTPResponse", response.body());
                System.out.println(jo.get("HTTPResponse"));
                System.out.println("TRYING PARSING: \n\n");
                String parsedString = jo.get("HTTPResponse").toString();
                String[] res = parsedString.split("[,::]", 6);
                for(String r : res){
                    System.out.println(r + "\n");
                }
                res[5] = res[5].substring(0, res[5].length() - 1);
                htmlString.append("<p>ANIME: " + res[1] + "</p>");
                htmlString.append("<p>CHARACTER: " + res[3] + "</p>");
                htmlString.append("<p>QUOTE: " + res[5] + "</p>");
                FileWriter fileWriter = new FileWriter("src/main/savedTitles.json");
                fileWriter.write(jo.toJSONString());
                fileWriter.close();
            }
            htmlString.append("</body>");
            htmlString.append("<html");
            out.println(htmlString);
            sock.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
