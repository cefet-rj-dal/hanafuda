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

import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Scanner;

import br.gpca.game.GameData;
import br.gpca.game.GameDataAndroid;
import br.gpca.hanafuda.kernel.GameController;


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
        TextView position = (TextView) findViewById(R.id.position);

        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                String playerPoints = params.getString("scorep");
                scoreplayer.setText(playerPoints);

                String computerPoints = params.getString("scorec");
                scorecomputer.setText(computerPoints);


                String vencedor = params.getString("winner");
                winner.setText(vencedor);

                ArrayList playerCombos = params.getIntegerArrayList("pcombos");
                ArrayList computerCombos = params.getIntegerArrayList("ccombos");

                String pos = sendResults(MainActivity.playerStarted, Integer.parseInt(playerPoints), playerCombos, MainActivity.oppType.toString(), Integer.parseInt(computerPoints), computerCombos);
                String txt;
                if(pos.equals("1")){
                    txt = "You are the "+ pos +"st in the ranking!";
                }else if(pos.equals("2")){
                    txt = "You are the "+ pos +"nd in the ranking!";
                }else if(pos.equals("3")){
                    txt = "You are the "+ pos +"rd in the ranking!";
                }else{
                    txt = "You are the "+ pos +"th in the ranking!";
                }

                position.setText(txt);

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
                if (randomImageSW == 6) {
                    image.setImageResource(R.drawable.chibimaker7);
                }
                if (randomImageSW == 7) {
                    image.setImageResource(R.drawable.chibimaker8);
                }
                if (randomImageSW == 8) {
                    image.setImageResource(R.drawable.chibimaker9);
                }
                if (randomImageSW == 9) {
                    image.setImageResource(R.drawable.chibimaker10);
                }
            }
        }
    }

    protected String getCombo(ArrayList combos) {
        String cmb = "";
        for(int i = 0; i< combos.size();i++){
            String s = combos.get(i).toString();
            if (Integer.parseInt(s) > 0) {
                if (cmb.length() > 0) {
                    cmb = cmb + ",";
                }
                cmb = cmb + GameController.getComboName(i);
            }
        }
        return cmb;
    }

    protected String sendResults(int playerStarted, int playerPoints, ArrayList playerCombos, String computerAlgorithm, int computerPoints, ArrayList computerCombos) {
        GameData gameData = GameDataAndroid.createSurvey(this);
        gameData.playerStarted = playerStarted;
        gameData.playerPoints = playerPoints;
        gameData.playerCombos = getCombo(playerCombos);
        gameData.computerAlgorithm = computerAlgorithm;
        gameData.computerPoints = computerPoints;
        gameData.computerCombos = getCombo(computerCombos);
        String json = GameData.convert(gameData);
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);

            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = "https://albali.eic.cefet-rj.br/tomcat/amews/survey.spring?json=" + URLEncoder.encode(json);
            HttpGet httpGet = new HttpGet(url);
            CloseableHttpResponse response = httpclient.execute(httpGet);
            InputStream content = response.getEntity().getContent();
            String value = new Scanner(content, "UTF-8").useDelimiter("\\A").next();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
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
