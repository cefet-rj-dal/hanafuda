package br.gpca.hanafuda.android;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URLEncoder;

import br.gpca.survey.Survey;
import br.gpca.survey.SurveyAndroid;


/*
*/

public class ShowWinner extends Activity {

    public static int key = 1;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.show_winner);

        TextView scorecomputer = (TextView) findViewById(R.id.scorepc);
        TextView scoreplayer = (TextView) findViewById(R.id.scplay);
        TextView winner = (TextView) findViewById(R.id.winner);
        ImageView image = (ImageView) findViewById(R.id.AIandroid);


        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                String sc = params.getString("scorec");
                scorecomputer.setText(sc);

                String sp = params.getString("scorep");
                scoreplayer.setText(sp);

                String vencedor = params.getString("winner");
                winner.setText(vencedor);



                sendResults(sc, sp, MainActivity.oppType.toString());

                int randomImageSW = params.getInt("randomImageSW");

                if (randomImageSW == 0) {
                    image.setImageResource(R.drawable.chibimaker1);
                }
                if (randomImageSW == 1) {
                    image.setImageResource(R.drawable.chibimaker2);
                }
                if (randomImageSW == 2) {
                    image.setImageResource(R.drawable.chibimaker3);
                }
                if (randomImageSW == 3) {
                    image.setImageResource(R.drawable.chibimaker4);
                }
                if (randomImageSW == 4) {
                    image.setImageResource(R.drawable.chibimaker5);
                }
                if (randomImageSW == 5) {
                    image.setImageResource(R.drawable.chibimaker6);
                }
            }
        }
    }




    protected void sendResults(String sc, String sp, String player) {
        Survey survey = SurveyAndroid.createSurvey(this);
        String strkey = ""+ key++;
        survey.addItem(strkey, "Player_Points", sp);
        survey.addItem(strkey, "Computer_Points", sc);
        survey.addItem(strkey, "Computer_Algorithm", player);
        String json = Survey.convert(survey);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CloseableHttpClient httpclient = HttpClients.createDefault();

            String url = "http://eic.cefet-rj.br/app/dsws/survey.spring?json=" + URLEncoder.encode(json);
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response1 = httpclient.execute(httpGet);
            String value = "" + response1.getStatusLine();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void continueGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void exitGame(View view) {
        finish();
    }
}
