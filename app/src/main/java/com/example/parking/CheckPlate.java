package com.example.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class CheckPlate extends AsyncTask<String, Void, String> {

    private Context context;
    private ProgressDialog dialog;

    public CheckPlate(Context context) {
        this.context = context;
    }

    /**
     * On vérifie si la plaque se trouve dans la base de donnée
     * @param params
     * @return
     */

    @Override
    protected String doInBackground(String... params) {
        try{
            String licensePlate = (String)params[0];

            String link = "https://balatttton.000webhostapp.com/checkPlate.php";
            String data = URLEncoder.encode("plate", "UTF-8") + "=" +
                    URLEncoder.encode(licensePlate, "UTF-8");

            URL url = new URL(link);
            URLConnection urlConnection = url.openConnection();

            urlConnection.setDoOutput(true);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(urlConnection.getOutputStream());

            outputStreamWriter.write(data);
            outputStreamWriter.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            StringBuilder stringBuilder = new StringBuilder();
            String line = null;

            // On lit la réponse du serveur
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                break;
            }

            return stringBuilder.toString();
        } catch(Exception e){
            return new String("Exception: " + e.getMessage());
        }
    }

    @Override
    protected void onPreExecute() {
        this.dialog = new ProgressDialog(this.context);
        dialog.setMessage("Processing...");
        dialog.show();
    }

    /**
     * Réponse en fonction de si la plaque se trouve
     * Dans la base de donnée ou non
     * @param result
     */

    @Override
    protected void onPostExecute(String result) {
        this.dialog.dismiss();
        Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
        if (result.equals("PLATE VERIFIED")) {
            Intent intent = new Intent(this.context, MenuActivity.class);
            this.context.startActivity(intent);
        }
    }
}
