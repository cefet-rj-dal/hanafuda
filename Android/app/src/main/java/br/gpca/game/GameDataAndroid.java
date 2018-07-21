package br.gpca.game;

import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eogasawara on 14/03/15.
 */
public class GameDataAndroid {

    public static GameData createSurvey(Activity act) {
        GameData gameData = new GameData();
        try {
            String appName = act.getApplicationContext().getPackageName();
            PackageInfo pInfo = act.getPackageManager().getPackageInfo(appName, 0);
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            gameData.androidVersion = sdkVersion + " (" + release + ")";
            gameData.androidLanguage = Locale.getDefault().getISO3Language();
            gameData.deviceInfo = Build.MODEL + " (" + Build.PRODUCT + ")";
            gameData.appVersion = pInfo.versionName;
            gameData.day = cal.get(Calendar.DAY_OF_MONTH);
            gameData.month = cal.get(Calendar.MONTH) + 1;
            gameData.year = cal.get(Calendar.YEAR);
            gameData.hour = cal.get(Calendar.HOUR_OF_DAY);
            gameData.minute = cal.get(Calendar.MINUTE);
            gameData.playerName = Build.SERIAL;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameData;
    }
}
