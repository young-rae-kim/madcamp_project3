package com.example.libraryapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class BarcodeActivity extends AppCompatActivity {
    private Bundle savedBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        savedBundle = savedInstanceState;

        if (savedInstanceState == null) {
            IntentIntegrator integrator = new IntentIntegrator(this);
            integrator.setOrientationLocked(false);
            integrator.setPrompt("Scan a barcode of book to add.");
            integrator.setBeepEnabled(false);
            integrator.setBarcodeImageEnabled(true);
            integrator.initiateScan();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && result != null && result.getContents() != null) {
            Toast.makeText(this, "Scanned ISBN : " + result.getContents(), Toast.LENGTH_LONG).show();
            searchBook(result.getContents());
            savedBundle.putInt("token", 0);
        }
    }

    public void searchBook(final String ISBN) {
        final String clientId = "ANdtuRVWpm_4fbzP2T_V";
        final String clientSecret = "8KJe8lEitY";
        final int display = 1;

        new Thread() {
            @Override
            public void run() {
                try {
                    String apiURL = "https://openapi.naver.com/v1/search/book_adv.xml?d_isbn=" + ISBN + "&display=" + display + "&"; // json 결과
                    URL url = new URL(apiURL);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("GET");
                    con.setRequestProperty("X-Naver-Client-Id", clientId);
                    con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                    con.connect();

                    BufferedReader br;
                    int responseCode = con.getResponseCode();
                    if(responseCode == 200) {
                        InputStream inputStream = con.getInputStream();
                        BarcodeXmlParser parser = new BarcodeXmlParser();
                        br = new BufferedReader(new InputStreamReader(inputStream));
                        ArrayList<BookItem> resultList = parser.parse(inputStream);
                        Intent intent = new Intent();
                        intent.putExtra("thumbnail", resultList.get(0).getThumbnail());
                        intent.putExtra("title", resultList.get(0).getTitle());
                        intent.putExtra("author", resultList.get(0).getAuthor());
                        intent.putExtra("publisher", resultList.get(0).getPublisher());
                        intent.putExtra("pubdate", resultList.get(0).getPubdate());
                        br.close();
                        con.disconnect();
                        setResult(RESULT_OK, intent);
                        finish();
                        return;
                    } else {
                        br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                        StringBuilder searchResult = new StringBuilder();
                        String inputLine;
                        while ((inputLine = br.readLine()) != null) {
                            searchResult.append(inputLine + "\n");
                        }
                        Log.e("Search Book", "Error : " + responseCode + ", " + searchResult.toString());
                        br.close();
                        con.disconnect();
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
