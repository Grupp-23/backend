package Server;

import Model.GameManager;

public class MatchHandler extends Thread {

    private GameManager gameManager; //Instance of GameManager
    private Client client0;
    private Client client1;

    public MatchHandler(Client client0, Client client1){
        this.client0 = client0;
        this.client1 = client1;

        client0.setTeam(0);
        client1.setTeam(1);

        start();
    }

    public void spawnCharacter(Client client, int character){
        boolean spawnChar = gameManager.spawnCharacter(character, client.getTeam());
        if (spawnChar){
            client0.sendJson("");
            client1.sendJson("");
        }
    }



    public void run() {
        gameManager = new GameManager();




    }
}
