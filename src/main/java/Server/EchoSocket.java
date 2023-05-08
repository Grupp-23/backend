package Server;

import org.eclipse.jetty.util.ajax.JSON;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;



@WebSocket
public class EchoSocket {

    private static ArrayList<Session> sessions = new ArrayList<Session>();

    private static HashMap<Client, MatchHandler> matches = new HashMap<>();
    private static HashMap<Session, Client> clients = new HashMap<>();


    @OnWebSocketConnect
    public void onConnect(Session session) { //When client connects to the server
        System.out.println("Client connected: " + session.getRemoteAddress().getAddress()); //Prints out the client ID
        sessions.add(session); //Adds session to a list
        System.out.println("Amount of connections: "+ sessions.size());
        // ---- Assigning a client -----
        if(sessions.size() >= 2){ // If there is two clients in the clientThreads list
            MatchHandler matchHandler = new MatchHandler(); //Start a match thread
            matches.put((Client) sessions.get(1), matchHandler);
            matches.put((Client) sessions.get(0), matchHandler);

            clients.put(sessions.get(1), new Client(sessions.get(1)));
            clients.put(sessions.get(0), new Client(sessions.get(0)));

            sessions.remove(1);
            sessions.remove(0);
            System.out.println("Clients is placed in match");
        }
    }


    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Connection closed with statusCode=" + statusCode + ", reason=" + reason);

    }

    @OnWebSocketMessage
    public void onMessage(String message, Session session) {

        

        System.out.println("Received message: " + message);
    }
}