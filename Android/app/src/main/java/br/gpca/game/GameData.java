package br.gpca.game;

/**
 * Created by eduar on 20/07/2018.
 */

import com.google.gson.Gson;

public class GameData {
    public String androidVersion;
    public String androidLanguage;
    public String deviceInfo;
    public String appVersion;
    public int day;
    public int month;
    public int year;
    public int hour;
    public int minute;
    public int playerStarted;
    public String playerName;
    public int playerPoints;
    public String playerCombos;
    public String computerAlgorithm;
    public int computerPoints;
    public String computerCombos;

    public static String convert(GameData gameData) {
        Gson gson = new Gson();
        String json = gson.toJson(gameData);
        return json;
    }
}
