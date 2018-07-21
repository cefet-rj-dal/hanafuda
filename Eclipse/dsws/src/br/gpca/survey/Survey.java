package br.gpca.survey;

/**
 * Created by eogasawara on 13/03/15.
 */

import com.google.gson.Gson;

public class Survey {
    public String androidVersion;
    public String androidLanguage;
    public String deviceInfo;
    public String appVersion;
    public int day;
    public int month;
    public int year;
    public int hour;
    public int minute;
    public String playerName;
    public int playerPoints;
    public String playerCombos;
    public String computerAlgorithm;
    public int computerPoints;
    public String computerCombos;

    public static String convert(Survey survey) {
        Gson gson = new Gson();
        String json = gson.toJson(survey);
        return json;
    }
}

