package override.logic;

public interface LabyrinthPlayer {


    /**
     * В начала игры у бота вызывается этот метод, чтобы передать число, которым он будет обозначен на карте
     * @param number -1 или -2
     */
    void takeYourNumber(int number);

    /**
     * @param gameState состояние игры после предыдущего тика
     * @return Направление, в котором двинется бот на следующем тике
     */
    Direction step(GameState gameState);

    /**
     * @return свой ник в tg ака "@Marandyuk_Anatolii
     * ЕСЛИ НЕ УКАЗАТЬ, ТО ОСТАНЕШЬСЯ БЕЗ ПРИЗА -________________________-
     */
    String getTelegramNick();
}
