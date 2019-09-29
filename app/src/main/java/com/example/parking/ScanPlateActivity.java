package com.example.parking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScanPlateActivity extends AppCompatActivity {

    private CameraSource cameraSource;
    private SurfaceView cameraView;
    private TextView textView;
    private Button boutonValider;
    private EditText licensePlate;
    final int requestPermissionID = 1001;

    // RegExp correspondant à la structure d'une plaque ordinaire

    public static final Pattern LICENSE_PLATE = Pattern.compile("[1-9]-[A-Z]{3}-[0-9]{3}");

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case requestPermissionID: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_plate);

        startCameraSource();
    }

    /**
     * Méthode principale pour faire fonctionner le
     * Reconnaisseur de texte
     */

    private void startCameraSource() {

        this.cameraView = findViewById(R.id.maSurfaceView);
        this.textView = findViewById(R.id.welcomeText);
        this.boutonValider = findViewById(R.id.detectButton);
        this.licensePlate = findViewById(R.id.licensePlateEditText);


        // On crée le TextRecognizer
        final TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!textRecognizer.isOperational()) {
            Log.w("MainActivity", "Detector dependencies not loaded yet");
        } else {

            // On initialise la camera pour une bonne résolution et en activant l'auto focus
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280, 1024)
                    .setAutoFocusEnabled(true)
                    .setRequestedFps(2.0f)
                    .build();

            /**
             * Callback pour la surface view et on check si on a l'autorisation pour la camera.
             * Si oui on peut démarrer la camera et la lier à la surfaceView
             */

            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {

                        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                            ActivityCompat.requestPermissions(ScanPlateActivity.this,
                                    new String[]{Manifest.permission.CAMERA},
                                    requestPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();
                }
            });

            //On met en place le systeme du TextRecognizer
            textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
                @Override
                public void release() {
                }

                /**
                 * On detecte tout le texte de la camera en utilisant un systeme de TextBlock
                 * Et on construit un stringBuilder avec
                 * Qu'on afficher ensuite à l'utilisateur
                 * */
                @Override
                public void receiveDetections(Detector.Detections<TextBlock> detections) {
                    final SparseArray<TextBlock> items = detections.getDetectedItems();
                    if (items.size() != 0 ){

                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                StringBuilder stringBuilder = new StringBuilder();
                                for(int i=0;i<items.size();i++){
                                    TextBlock item = items.valueAt(i);
                                    stringBuilder.append(item.getValue());
                                    stringBuilder.append("\n");
                                }

                                // On vérifie que le texte correspond au RegExp
                                // Si oui on l'affiche à l'utilisateur
                                String plate = stringBuilder.toString().trim().replace(" ", "");
                                if (LICENSE_PLATE.matcher(plate).matches()) {
                                    textView.setText(plate.replace("-", " - "));
                                }
                            }
                        });
                    }
                }
            });
        }
    }

    public void verifyPlate(View view) {

        String licensePlateScan = this.textView.getText().toString();
        String licensePlateText = this.licensePlate.getText().toString();

        if (licensePlateText.length() > 0) {

            new CheckPlate(this).execute(licensePlateText);
        }
        else if (licensePlateScan.length() > 0) {

            new CheckPlate(this).execute(licensePlateScan);
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "NOTHING TO VERIFY",
                    Toast.LENGTH_SHORT);

            toast.show();
        }


    }
}
