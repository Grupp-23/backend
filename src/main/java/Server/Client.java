package Server;

import Model.Base;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;
import java.util.ArrayList;

public class Client {

    private Session session;
    private int team;
    private String name;
    private int gold = 200;
    private Base base;

    public Client(Session session){
        this.session = session; //Assigns a specific session for this client
        base = new Base();
    }

    public void setTeam(int team) {
        this.team = team;
    }
    public int getTeam() {
        return team;
    }

    public int getGold() {
        return gold;
    }

    public void reduceGold(int amount) {
        gold -= amount;
    }

    public void increaseGold(int amount) {
        gold += amount;
    }

    public Base getBase() {
        return base;
    }

    public int getId(){

        return 0;
    }

    public Session getSession() {
        return session;
    }

    public void sendJson(String json) throws IllegalStateException {
        try {
            session.getRemote().sendString(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
