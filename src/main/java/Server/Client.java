package Server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Client {

    private Session session;

    public Client(Session session){
        this.session = session; //Assigns a specific session for this client
    }

    public void sendJson(String json){
        try {
            session.getRemote().sendString(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
