package override.logic;

import java.lang.reflect.Field;
import java.util.Random;

import static override.logic.Direction.*;


public class Game {

    private GameState gameState;
    private LabyrinthPlayer team1;
    private LabyrinthPlayer team2;
    private int sum1;
    private int sum2;
    private boolean ended = false;
    private final int MIN_WALLS_AT_HALF = 5;
    private final int MIN_WALL_LENGTH = 3;
    private final int MAX_WALLS_AT_HALF = 15;
    private final int MAX_WALL_LENGTH = 7;
    private final int MAX_CELL_VALUE = 50;
    private final int PLAYER_ONE = -1;
    private final int PLAYER_TWO = -2;
//    protected ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());


    public Game(LabyrinthPlayer team1, LabyrinthPlayer team2, int roundsToEnd) {
        this.gameState = new GameState(roundsToEnd, generateMap());
        this.team1 = team1;
        this.team2 = team2;
    }

    private int[][] generateMap() {
        Random rnd = new Random();
        int[][] map = new int[GameState.WIDTH][GameState.HEIGHT];
        for (int i = 0; i < GameState.HEIGHT; i++) {
            for (int j = 0; j < GameState.WIDTH - i; j++) {
                map[i][j] = 0;
                if (rnd.nextInt(4) == 0) {
                    int cellValue = rnd.nextInt(MAX_CELL_VALUE);
                    map[i][j] = cellValue;
                    map[GameState.HEIGHT - i - 1][GameState.WIDTH - j - 1] = cellValue;
                }
            }
        }
        int teamNumber = PLAYER_ONE;
        map[0][0] = teamNumber;
        map[GameState.HEIGHT - 1][GameState.WIDTH - 1] = PLAYER_TWO;
        generateWalls(map);
        for (int i = 0; i < GameState.HEIGHT; i++) {
            for (int j = 0; j < GameState.WIDTH; j++) {
                if (!(map[i][j] == -2 || map[i][j] == -1)) {
                    if (map[i][j] != map[GameState.HEIGHT - i - 1][GameState.WIDTH - j - 1]) {

                        map[i][j] = 0;
                        map[GameState.HEIGHT - i - 1][GameState.WIDTH - j - 1] = 0;
                    }
                }
            }
        }
        return map;
    }

    /**
     * Генерация препятствий
     *
     * @param map карта
     */
    public void generateWalls(int[][] map) {
        Random random = new Random();
        int wallsNumber = MIN_WALLS_AT_HALF + random.nextInt(MAX_WALLS_AT_HALF - MIN_WALLS_AT_HALF);
        for (int k = 0; k < wallsNumber; k++) {
            int wallLength = MIN_WALL_LENGTH + random.nextInt(MAX_WALL_LENGTH - MIN_WALL_LENGTH);
            int randomI = random.nextInt(((GameState.HEIGHT) - 2) + 2);
            int randomJ = random.nextInt(((GameState.WIDTH / 2) - 2) + 2);
            boolean horizontal = random.nextBoolean();
            for (int l = 0; l < wallLength; l++) {
                Position wallNumberPosition = new Position(randomI, randomJ);
                if (horizontal) {
                    wallNumberPosition.j = wallNumberPosition.j - l;
                } else {
                    wallNumberPosition.i = wallNumberPosition.i - l;
                }
                if (!checkForWall(wallNumberPosition)) {
                    break;
                }
                map[wallNumberPosition.i][wallNumberPosition.j] = GameState.WALL_NUMBER;
                map[GameState.WIDTH - wallNumberPosition.i - 1][GameState.HEIGHT - wallNumberPosition.j - 1] = GameState.WALL_NUMBER;
            }
        }

    }

    /**
     * Проверяет что точка не касается границ карты
     */
    private boolean checkForWall(Position position) {
        if (position.i <= 0 || position.j <= 0) {
            return false;
        }
        return position.i < GameState.WIDTH && position.j < GameState.HEIGHT;
    }

    public void tick() throws NoSuchFieldException, IllegalAccessException {
        //раздаем номера ботам
        giveTeamNumbers();

        int[][] actualMap;
        Field mapField = gameState.getClass().getDeclaredField("map");
        mapField.setAccessible(true);
        actualMap = (int[][]) mapField.get(gameState);
        Position position1Team = findNumberPos(actualMap, PLAYER_ONE);
        Position position2Team = findNumberPos(actualMap, PLAYER_TWO);
        Direction dir1 = team1.step(gameState);
//        Direction dir1 = Direction.NONE;
//        Future<Direction> future = executorService.submit(() ->  team1.step(gameState));
//        try {
//            dir1 = future.get(10, TimeUnit.MILLISECONDS);
//        } catch (TimeoutException timeoutException) {
//            future.cancel(true);
//
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }

        switch (dir1) {
            case LEFT:
                if (position1Team.j == 0) {
                    throw new RuntimeException("Выход за " + LEFT.name() + "границу массива командой " + team1.getTelegramNick());
                }
                if (position1Team.j - 1 == position2Team.j && position1Team.i == position2Team.i) {
                    throw new RuntimeException("Попытка наезда от " + team1.getTelegramNick());
                }
                sum1 += actualMap[position1Team.i][position1Team.j - 1];
                actualMap[position1Team.i][position1Team.j] = 0;
                actualMap[position1Team.i][position1Team.j - 1] = PLAYER_ONE;
                break;
            case UP:
                if (position1Team.i == 0) {
                    throw new RuntimeException("Выход за " + UP.name() + "границу массива командой " + team1.getTelegramNick());
                }
                if (position1Team.i - 1 == position2Team.i && position1Team.j == position2Team.j) {
                    throw new RuntimeException("Попытка наезда от " + team1.getTelegramNick());
                }
                sum1 += actualMap[position1Team.i - 1][position1Team.j];
                actualMap[position1Team.i][position1Team.j] = 0;
                actualMap[position1Team.i - 1][position1Team.j] = PLAYER_ONE;
                break;
            case BOTTOM:
                if (position1Team.i == GameState.HEIGHT - 1) {
                    throw new RuntimeException("Выход за " + BOTTOM.name() + "границу массива командой " + team1.getTelegramNick());
                }
                if (position1Team.i + 1 == position2Team.i && position1Team.j == position2Team.j) {
                    throw new RuntimeException("Попытка наезда от " + team1.getTelegramNick());
                }
                sum1 += actualMap[position1Team.i + 1][position1Team.j];
                actualMap[position1Team.i][position1Team.j] = 0;
                actualMap[position1Team.i + 1][position1Team.j] = PLAYER_ONE;
                break;
            case RIGHT:
                if (position1Team.j == GameState.WIDTH - 1) {
                    throw new RuntimeException("Выход за " + RIGHT.name() + "границу массива командой " + team1.getTelegramNick());
                }
                if (position1Team.j + 1 == position2Team.j && position1Team.i == position2Team.i) {
                    throw new RuntimeException("Попытка наезда от " + team1.getTelegramNick());
                }
                sum1 += actualMap[position1Team.i][position1Team.j + 1];
                actualMap[position1Team.i][position1Team.j] = 0;
                actualMap[position1Team.i][position1Team.j + 1] = PLAYER_ONE;
                break;
            case NONE:
                break;

        }
        position1Team = findNumberPos(actualMap, PLAYER_ONE);
        position2Team = findNumberPos(actualMap, PLAYER_TWO);
        Direction dir2 = team2.step(gameState);


//        Direction dir2 = Direction.NONE;
//        Future<Direction> future2 = executorService.submit(() ->  team2.step(gameState));
//        try {
//            dir2 = future2.get(10, TimeUnit.MILLISECONDS);
//        } catch (TimeoutException timeoutException) {
//            future2.cancel(true);
//
//        } catch (ExecutionException | InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        switch (dir2) {
            case LEFT:
                if (position2Team.j == 0) {
                    throw new RuntimeException("Выход за " + LEFT.name() + "границу массива командой " + team2.getTelegramNick());
                }
                if (position2Team.j - 1 == position1Team.j && position1Team.i == position2Team.i) {
                    throw new RuntimeException("Попытка наезда от " + team2.getTelegramNick());
                }
                sum2 += actualMap[position2Team.i][position2Team.j - 1];
                actualMap[position2Team.i][position2Team.j] = 0;
                actualMap[position2Team.i][position2Team.j - 1] = PLAYER_TWO;
                break;
            case UP:
                if (position2Team.i == 0) {
                    throw new RuntimeException("Выход за " + UP.name() + "границу массива командой " + team2.getTelegramNick());
                }
                if (position2Team.i - 1 == position1Team.i && position1Team.j == position2Team.j) {
                    throw new RuntimeException("Попытка наезда от " + team2.getTelegramNick());
                }
                sum2 += actualMap[position2Team.i - 1][position2Team.j];
                actualMap[position2Team.i][position2Team.j] = 0;
                actualMap[position2Team.i - 1][position2Team.j] = PLAYER_TWO;
                break;
            case BOTTOM:
                if (position2Team.i == GameState.HEIGHT - 1) {
                    throw new RuntimeException("Выход за " + BOTTOM.name() + "границу массива командой " + team2.getTelegramNick());
                }
                if (position2Team.i + 1 == position1Team.i && position1Team.j == position2Team.j) {
                    throw new RuntimeException("Попытка наезда от " + team2.getTelegramNick());
                }
                sum2 += actualMap[position2Team.i + 1][position2Team.j];
                actualMap[position2Team.i][position2Team.j] = 0;
                actualMap[position2Team.i + 1][position2Team.j] = PLAYER_TWO;
                break;
            case RIGHT:
                if (position2Team.j == GameState.WIDTH - 1) {
                    throw new RuntimeException("Выход за " + RIGHT.name() + "границу массива командой " + team2.getTelegramNick());
                }
                if (position2Team.j + 1 == position1Team.j && position1Team.i == position2Team.i) {
                    throw new RuntimeException("Попытка наезда от " + team2.getTelegramNick());
                }
                sum2 += actualMap[position2Team.i][position2Team.j + 1];
                actualMap[position2Team.i][position2Team.j] = 0;
                actualMap[position2Team.i][position2Team.j + 1] = PLAYER_TWO;
                break;
            case NONE:
                break;

        }
        Field fieldWithScore1 = gameState.getClass().getDeclaredField("team1Score");
        fieldWithScore1.setAccessible(true);
        fieldWithScore1.set(gameState, sum1);
        Field fieldWithScore2 = gameState.getClass().getDeclaredField("team2Score");
        fieldWithScore2.setAccessible(true);
        fieldWithScore2.set(gameState, sum2);
        timerTick();
        if (gameState.getRoundsToEnd() == 0) {
            ended = true;
        }
    }

    private boolean checkReflection() {
        return false;
    }

    private Position findNumberPos(int[][] map, int numberToFind) {
        for (int i = 0; i < GameState.HEIGHT; i++) {
            for (int j = 0; j < GameState.WIDTH; j++) {
                if (map[i][j] == numberToFind) {
                    return new Position(i, j);
                }
            }

        }
        throw new IllegalStateException("Couldn't find number at the map");
    }

    private void timerTick() throws NoSuchFieldException, IllegalAccessException {
        Field field = gameState.getClass().getDeclaredField("roundsToEnd");
        field.setAccessible(true);
        field.set(gameState, (int) field.get(gameState) - 1);
    }

    private void giveTeamNumbers() {
        team1.takeYourNumber(PLAYER_ONE);
        team2.takeYourNumber(PLAYER_TWO);
    }

    public GameState getGameState() {
        return gameState;
    }

    public LabyrinthPlayer getTeam1() {
        return team1;
    }

    public LabyrinthPlayer getTeam2() {
        return team2;
    }

    public int getSum1() {
        return sum1;
    }

    public int getSum2() {
        return sum2;
    }

    public boolean isEnded() {
        return ended;
    }
}
