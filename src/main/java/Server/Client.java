package Server;

import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Client {

    private Session session;
    private int team;
    public Client(Session session){
        this.session = session; //Assigns a specific session for this client
    }

    public void setTeam(int team) {
        this.team = team;
    }
    public int getTeam() {
        return team;
    }

    public int getId(){

        return 0;
    }



    public void sendJson(String json){
        try {
            session.getRemote().sendString(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
