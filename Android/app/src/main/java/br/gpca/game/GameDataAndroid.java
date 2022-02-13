package br.gpca.game;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eogasawara on 14/03/15.
 */
public class GameDataAndroid {

    protected static String filename = "hanafuda.txt";

    protected static boolean fileExists(Context context, String filename) {
        File file = context.getFileStreamPath(filename);
        if(file == null || !file.exists()) {
            return false;
        }
        return true;
    }

    protected static void writeID(Context context, String id) {
        try (FileOutputStream fos = context.openFileOutput(filename, Context.MODE_PRIVATE)) {
            fos.write(id.getBytes(StandardCharsets.UTF_8));
        }
        catch(Exception e) {
            Log.v("Hanafuda", e.toString());
        }
    }

    protected static String readID(Context context, String filename) {
        String id = "";
        StringBuilder stringBuilder = new StringBuilder();
        try {
            FileInputStream fis = context.openFileInput(filename);
            InputStreamReader inputStreamReader =
                    new InputStreamReader(fis, StandardCharsets.UTF_8);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                stringBuilder.append(line);
                line = reader.readLine();
            }
        } catch (Exception e) {
            // Error occurred when opening raw file for reading.
        } finally {
            id = stringBuilder.toString();
        }
        return(id);
    }

    protected static String getAlphaNumericString(int n)
    {

        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    protected static String getDeviceID(Context context) {
        String id = "";
        if (fileExists(context, filename)) {
            id = readID(context, filename);
        }
        if (id.length() == 0) {
            id = getAlphaNumericString(20);
            writeID(context, id);
        }
        return(id);
    }

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
            gameData.playerName = getDeviceID(act.getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gameData;
    }
}
