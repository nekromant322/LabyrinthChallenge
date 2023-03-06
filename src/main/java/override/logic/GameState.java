package override.logic;

public class GameState {
    private int[][] map;
    private int roundsToEnd;
    private int team1Score;
    private int team2Score;
    public final static int WIDTH = 15;
    public final static int HEIGHT = 15;
    public final static int WALL_NUMBER = -16;
    public final static int MAX_TIME_FOR_STEP_MILLISECONDS = 500;

    public GameState(int roundsToEnd, int[][] map) {
        this.roundsToEnd = roundsToEnd;
        this.team1Score = 0;
        this.team2Score = 0;
        this.map = map;
    }

    /**
     * @return Двумерный массив с игровым полем
     */
    public int[][] getMap() {
        return this.map.clone();
    }

    /**
     *
     * @return Кол-во тиков до конца игры
     */
    public int getRoundsToEnd() {
        return roundsToEnd;
    }

    /**
     * Кол-во очков у игрока, помеченного -1
     * @return
     */
    public int getTeam1Score() {
        return team1Score;
    }

    /**
     * Кол-во очков у игрока, помеченного -2
     * @return
     */
    public int getTeam2Score() {
        return team2Score;
    }
}
