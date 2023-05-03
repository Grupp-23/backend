package Model;

public class GameManager {

    //Lägg in spelarna i en lista (väntar svar på oliver från hans kod)

    private boolean gameWinner;

    /**
     * Starts the game when two clients are ready
     */
    public void startGame(){

    }

    /**
     * Ends game when client disconects or the base of one client/player is destoryed.
     */
    public void endGame(){

    }


    public void updateGameState(){
        //få info från gameState i matchtråden

    }

    /**
     * Gets position of character from gameThread
     */
    public void getPosition(){

    }

    /**
     * Gets health of base and if base is destoryed or not
     */
    public void getBaseStatus(){

    }

    /**
     * Checks so a player has sufficent amount of gold before buying a character
     * @return returns if player can buy character or not
     */
    public boolean checkGold(){
        return player.getMoney() >= character;
    }

    /**
     * Sends gold for characters killed to the player that killed them.
     */
    public void getReward(){

    }


}
