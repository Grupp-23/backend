package Server;

import Model.GameManager;
import org.eclipse.jetty.websocket.api.Session;

import java.util.HashMap;

public class MatchHandler extends Thread {

    private GameManager gameManager; //Instance of GameManager
    private Client client;

    public MatchHandler(Client client0, Client client1){
        start();
    }

    public void assignTeam(){

    }


    public void run() {
        gameManager = new GameManager();




    }
}
