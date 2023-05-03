package Model;

public class GameManager {

    //Lägg in spelarna i en lista (väntar svar på oliver från hans kod)

    private boolean gameWinner;

    public void updateGameState(){
        //få info från gameState i matchtråden

    }

    public void getPosition(){

    }

    public void getBaseStatus(){

    }

    public boolean checkGold(){ //Checks so a player has sufficent amount of gold before buying a character
        if(player.getMoney() >= character)
        return true;
    }
}
