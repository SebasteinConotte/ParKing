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

public class Signin extends AsyncTask<String, Void, String> {
    private Context context;
    private ProgressDialog dialog;

    public Signin(Context context) {
        this.context = context;
    }

    /**
     * On récupère les données entrées par l'utilisateur
     * Et on envoie à la page php qui va vérifier
     * Celles-ci dans la base de donnée
     * @param params
     * @return
     */

    @Override
    protected String doInBackground(String... params) {
        try{
            String username = (String)params[0];
            String password = (String)params[1];

            String link = "https://balatttton.000webhostapp.com/index.php";
            String data = URLEncoder.encode("user_name", "UTF-8") + "=" +
                    URLEncoder.encode(username, "UTF-8");
            data += "&" + URLEncoder.encode("user_pass", "UTF-8") + "=" +
                    URLEncoder.encode(password, "UTF-8");

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

    /**
     * Dialogue qui prévient à l'utilisateur que l'opération
     * Est en cours
     */

    @Override
    protected void onPreExecute() {
        this.dialog = new ProgressDialog(this.context);
        dialog.setMessage("Processing...");
        dialog.show();
    }

    /**
     * Une fois l'opération terminée
     * On vérifie l'opération reçue par la page PHP
     * @param result
     */

    @Override
    protected void onPostExecute(String result) {
        this.dialog.dismiss();
        Toast.makeText(this.context, result, Toast.LENGTH_SHORT).show();
        if (result.equals("LOGIN SUCCESSFUL")) {
            Intent intent = new Intent(this.context, MenuActivity.class);
            this.context.startActivity(intent);
        }
    }
}
