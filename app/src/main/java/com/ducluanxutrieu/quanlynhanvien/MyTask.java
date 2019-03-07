package com.ducluanxutrieu.quanlynhanvien;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ducluanxutrieu.quanlynhanvien.Activity.EditUserActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MyTask extends AsyncTask<String, Void, String> {
    private Context context;

    public MyTask(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        String BASE_URL = strings[0];
        String query = strings[1];
        StringBuilder result = new StringBuilder();
        try {
            URL url = new URL(BASE_URL);
            HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
            httpCon.setDoOutput(true);
            httpCon.setDoInput(true);
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Accept", "application/json");
            httpCon.setRequestProperty("Content-Type", "application/json");

            OutputStream os = httpCon.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os, StandardCharsets.UTF_8);
            osw.write(query);
            osw.flush();
            osw.close();
            os.close();  //don't forget to close the OutputStream
            httpCon.connect();

            //read the inputstream and print it
            InputStream inputStream = httpCon.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            //send message result
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            System.out.println(result.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        if (s.startsWith("created")) {
            EditUserActivity.addAccountToDatabase(s);
            Toast.makeText(context, context.getString(R.string.add_new_member_complete), Toast.LENGTH_SHORT).show();
        }else if (s.startsWith("update")){
            EditUserActivity.updateUserToDatabase();
            Toast.makeText(context, context.getString(R.string.update_user_successful), Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }
}
