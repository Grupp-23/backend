package Server;

import Model.GameManager;

public class MatchHandler extends Thread {

    private GameManager gameManager; //Instance of GameManager
    public MatchHandler(){
        start();
    }


    public void run() {
        gameManager = new GameManager();




    }
}
