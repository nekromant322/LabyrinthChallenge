
package override.example;

import override.logic.LabyrinthPlayer;
import override.logic.Direction;
import override.logic.GameState;

public class PassiveLabyrinthBot implements LabyrinthPlayer {

    private int mynumber;

    @Override
    public void takeYourNumber(int number) {
        mynumber = number;
    }

    @Override
    public Direction step(GameState gameState) {
       return Direction.NONE;
    }


    @Override
    public String getTelegramNick() {
        return "@Marandyuk_Anatolii";
    }
}

