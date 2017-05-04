package br.gpca.survey;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.pm.PackageInfo;
import android.os.Build;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by eogasawara on 14/03/15.
 */
public class SurveyAndroid {

    public static Survey createSurvey(Activity act) {
        Survey survey = new Survey();
        try {
            survey.appName = act.getApplicationContext().getPackageName();
            PackageInfo pInfo = act.getPackageManager().getPackageInfo(survey.appName, 0);
            survey.appVersion = pInfo.versionName;
            survey.androidlanguage = Locale.getDefault().getISO3Language();
            String release = Build.VERSION.RELEASE;
            int sdkVersion = Build.VERSION.SDK_INT;
            survey.androidVersion = sdkVersion + " (" + release + ")";
            survey.deviceInfo = Build.MODEL + " (" + Build.PRODUCT + ")";
            Date date = new Date(); // your date
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            survey.day = cal.get(Calendar.DAY_OF_MONTH);
            survey.month = cal.get(Calendar.MONTH) + 1;
            survey.year = cal.get(Calendar.YEAR);

            survey.hour = cal.get(Calendar.HOUR_OF_DAY);
            survey.minute = cal.get(Calendar.MINUTE);


            AccountManager manager = (AccountManager) act.getSystemService(act.ACCOUNT_SERVICE);
            Account[] list = manager.getAccounts();
            if (list.length > 0) {
                survey.userEmail = list[0].name;
                survey.userName = list[0].name;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return survey;
    }

}
