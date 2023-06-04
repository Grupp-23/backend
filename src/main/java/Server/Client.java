package Server;

import Model.Base;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

public class Client {

    private Session session;
    private int team;
    private String name;
    private int gold = 0;
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

        sendGoldInfo(gold);
    }

    public void increaseGold(int amount) {
        gold += amount;

        sendGoldInfo(gold);
    }

    public void sendGoldInfo(int amount) {
        JsonObject object = new JsonObject();
        object.addProperty("method", "gold");
        object.addProperty("amount", amount);

        Gson gson = new Gson();
        String json = gson.toJson(object);
        sendJson(json);
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

    public void sendJson(String json) {
        try {
            session.getRemote().sendString(json);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (IllegalStateException e) {
            throw new IllegalStateException(e);
        }
    }

}
