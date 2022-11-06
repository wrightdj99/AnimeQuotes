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
    //Declaring a socket/class level variables
    Socket sock;
    WebServerWorker(Socket s){sock = s;}
    //String builder to create the new HTML form
    StringBuilder htmlString = new StringBuilder();
    //These two things won't do much. The buffered reader will read in the URL being sent
    //by the HTML UI, but the request will always be the same.
    //The print stream will output the anime quote information to the end-user. More on that later.
    PrintStream out = null;
    BufferedReader in = null;
    //JSON object to hold the response body from the anime quotes API
    JSONObject jo = new JSONObject();
    //We will respond with this each time the user hits submit.
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
        //Starting the HTML response...
        htmlString.append(htmlResponse);
        try{
            out = new PrintStream(sock.getOutputStream());
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            //Method to send out HTTP Headers
            sendHeaders();
            //Reading in the request from the HTML page
            inputRequest = in.readLine();
            //Will always be true, but setting this up as an if statement if I ever want to add more functionality than
            //just returning a random quote
            if(inputRequest != null && inputRequest.contains("getQuote")) {
                //Code from RapidAPI that this program is subscribed to. Fairly cut and dry
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://anime-quotes1.p.rapidapi.com/api/random"))
                        .header("X-RapidAPI-Key", "cc1d38da1fmsh73b309d9673daccp1c4109jsnf02b4cfb0a4e")
                        .header("X-RapidAPI-Host", "anime-quotes1.p.rapidapi.com")
                        .method("GET", HttpRequest.BodyPublishers.noBody())
                        .build();
                HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                //Putting the single HTTP Response from the API into this JSON object. We'll be parsing it in a sec.
                jo.put("HTTPResponse", response.body());
                //For testing functionality:
                /*System.out.println(jo.get("HTTPResponse"));
                System.out.println("TRYING PARSING: \n\n");*/
                //PARSING THE STRING by colons and commas to extract each bit of information. May seem a bit
                //cumbersome, but for this application our payload from the API will generally be pretty small. It'll be fine.
                String parsedString = jo.get("HTTPResponse").toString();
                String[] res = parsedString.split("[,::]", 6);
                //Because I'm using a piecemeal method to pick this JSON apart, I'll need to take off the pesky closing curly brace.
                res[5] = res[5].substring(0, res[5].length() - 1);
                //Appending onto the HTML string
                htmlString.append("<p>ANIME: " + res[1] + "</p>");
                htmlString.append("<p>CHARACTER: " + res[3] + "</p>");
                htmlString.append("<p>QUOTE: " + res[5] + "</p>");
                //Serializing the API response by saving it to JSON.
                FileWriter fileWriter = new FileWriter("src/main/savedTitles.json");
                fileWriter.append(jo.toJSONString());
                fileWriter.close();
            }
            //Closing off the HTML string/document and sending it off
            htmlString.append("</body>");
            htmlString.append("</html>");
            out.println(htmlString);
            //Close the socket and be done!
            sock.close();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendHeaders(){
        out.println("HTTP/1.1 200 OK");
        out.println("Connection: close"); //Standard HTTP header
        out.println("Content-Length: 1000"); // Setting high for long quotes
        out.println("Content-Type: text/html \r\n\r\n");
    }
}
