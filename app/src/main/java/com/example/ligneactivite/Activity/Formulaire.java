package com.example.ligneactivite.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ligneactivite.BuildConfig;
import com.example.ligneactivite.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import cz.msebera.android.httpclient.entity.StringEntity;


public class Formulaire extends AppCompatActivity {
    EditText name, email, phone;
    ImageView imageImport;
    TextView show_location_view;
    TelephonyManager telephonyManager;
    String stringPhone;
    static final int RESULT_LOAD_IMG = 1;
    private  int chooseMode =0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TelephonyManager telephonyManager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        this.telephonyManager = telephonyManager;
        setContentView(R.layout.activity_formulaire);
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.activity_main_bottom_navigation_contact);

        //Initialisation des variable
        name = findViewById(R.id.nom_inscript);
        email = findViewById(R.id.mail_inscript);
        phone = findViewById(R.id.phone_number_inscript);
        imageImport = findViewById(R.id.imageView);

        Button importButton = findViewById(R.id.import_img);
        Button takeButton = findViewById(R.id.take_picture);

        Button submit = findViewById(R.id.button_submit);
        stringPhone = RecupPhoneNumber();
        phone.setText(stringPhone);
        submit.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (name.getText().toString().isEmpty() || email.getText().toString().isEmpty() || phone.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Formulaire incomplet", Toast.LENGTH_SHORT).show();
                } else {
                    initialiserLocation();
                }
                stringPhone = phone.getText().toString();
                String stringName = name.getText().toString();
                String stringEmail = email.getText().toString();
                Log.d("Nom", "Nom :" + name.getText().toString());
                Log.d("Numéro", "Numéro de tel :" + stringPhone);
                initialiserLocation();
                show_location_view = findViewById(R.id.number_phone);
                show_location_view.setText("Longitude : " + longitude + "\nLatitude : " + latitude + "\nNom : " + stringName + "\nEmail : " + stringEmail + "\nNuméro de téléphone : " + stringPhone);
                try {
                    DataJson(stringName, stringEmail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        importButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMode = 1;
                Intent photoPick = new Intent(Intent.ACTION_PICK);
                photoPick.setType("image/*");
                startActivityForResult(photoPick, RESULT_LOAD_IMG);
            }
        });
        takeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMode = 2;
              dispatchTakePictureIntent();
            }
        });


        //Navigation
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_accueil:
                        Intent otherActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(otherActivity);
                        finish();
                        break;
                    case R.id.action_logo:

                        clicked_button("https://www.google.fr/maps");
                        break;
                    case R.id.action_landscape:
                        clicked_button("http://www.google.fr");
                        break;
                    case R.id.action_android:
                        Intent contact = new Intent(getApplicationContext(), Contact.class);
                        startActivity(contact);
                        finish();
                        break;
                }
                return true;
            }
        });
    }

    public void clicked_button(String url) {

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
    }


    private String fournisseur;
    private double longitude = 0;
    private double latitude = 0;

    private void initialiserLocation() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        ArrayList<LocationProvider> providers = new ArrayList<LocationProvider>();
        ArrayList<String> names = (ArrayList<String>) locationManager.getProviders(true);
        int i = 0;
        for (String name : names) {
            providers.add(locationManager.getProvider(name));
            Log.d("TEST", String.valueOf(providers.get(i)));
            i++;

        }


        Criteria criteres = new Criteria();

        // la précision  : (ACCURACY_FINE pour une haute précision ou ACCURACY_COARSE pour une moins bonne précision)
        criteres.setAccuracy(Criteria.ACCURACY_FINE);

        // l'altitude
        criteres.setAltitudeRequired(true);

        // la direction
        criteres.setBearingRequired(true);

        // la vitesse
        criteres.setSpeedRequired(true);

        // la consommation d'énergie demandée
        criteres.setCostAllowed(true);
        criteres.setPowerRequirement(Criteria.POWER_HIGH);

        //fournisseur = locationManager.getBestProvider(criteres, true);
        fournisseur = LocationManager.NETWORK_PROVIDER;


        Log.d("GPS", "fournisseur : " + fournisseur);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(fournisseur);
        if (location == null) {
            Log.d("ERREUR", "Location vide");
        } else {
            Log.d("GPS", "Location :" + location.getLatitude() + "," + location.getLongitude());
            longitude = location.getLongitude();
            latitude = location.getLatitude();

        }
    }

    private String RecupPhoneNumber() {
        String numberphone = "";


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED) {
            return numberphone;
        } else {
            numberphone = telephonyManager.getLine1Number();
            return numberphone;
        }

    }

    private void DataJson(String stringName, String stringEmail) throws JSONException {

        JSONObject object = new JSONObject();
        String stringLatitude = String.valueOf(latitude);
        String stringLongitude = String.valueOf(longitude);
        String stringLocalisation = stringLatitude + " " + stringLongitude;
        Log.d("Encode ",encodeImage);

        object.put("Prenom", stringName);
        object.put("Phone", stringPhone);
        object.put("Email", stringEmail);
        object.put("localisation", stringLocalisation);
        object.put("Image", encodeImage);

        String data = object.toString();

            String url = "https://app.cmrp.net/cityc/thorailles/inser_user.php";
        try {
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {


                        HttpURLConnection urlConnection;
                        urlConnection = (HttpURLConnection) ((new URL(url).openConnection()));
                        urlConnection.setDoOutput(true);
                        urlConnection.setRequestProperty("Content-Type", "php://input");
                        urlConnection.setRequestProperty("Accept", "php://input");
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        //Write
                        OutputStream outputStream = urlConnection.getOutputStream();

                        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                        Log.d("DATA",data);
                        writer.write(data);
                        writer.close();
                        outputStream.close();

                        Log.d("Etat connection ", String.valueOf(urlConnection.getResponseCode()));


                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    } catch (ProtocolException e) {
                        e.printStackTrace();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            });
            thread.start();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int rescode, int resultCode, Intent data) {
        super.onActivityResult(rescode, resultCode, data);
        imageImport.setRotation(90);
        if (resultCode == RESULT_OK && chooseMode==2) {
           // Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath);

            try {

                final InputStream imageStream = getContentResolver().openInputStream(uri);

                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                imageImport.setImageBitmap(selectedImage);
                Log.d("CHEMIN ACCES",uri.getPath());
                String stringImage = ConvertImageToFile(uri);
                Log.d("STRING_IMAGE", stringImage);
                //Log.d("STRING_IMAGE", stringImage);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

            if (chooseMode == 1) {

            if (resultCode == RESULT_OK) {
                try {
                    final Uri imageUri = data.getData();

                    // Log.d("PATH", "imageUri: " + imageUri.toString());
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);

                    final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);

                    imageImport.setImageBitmap(selectedImage);
                    String stringImage = ConvertImageToFile(imageUri);
                    Log.d("STRING_IMAGE", stringImage);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Une erreur s'est produite", Toast.LENGTH_LONG).show();

                }

            } else {
                Toast.makeText(getApplicationContext(), "Vous n'avez pas choisi d'image", Toast.LENGTH_LONG).show();

            }
        }
    }


    private String encodeImage = null;

    public String ConvertImageToFile(Uri imageUri) throws FileNotFoundException {
        byte[] buffer = null;
        int buffersize = 1 * 1024 * 1024;
        Bitmap bitmap;
        File file = null;
        String imagePath = null;
     /* if(chooseMode ==1) {
          imagePath = getFilePathForN(imageUri,Formulaire.this);
          file = new File(imagePath);
          Log.d("PATH", "Chemin :" + imagePath);
          if (!file.isFile()) Log.e("uploadFile", "Source File not exist :" + imagePath);
          else {
              FileInputStream fileInputStream = new FileInputStream(file);
              buffer = new byte[buffersize];
              bitmap = BitmapFactory.decodeStream(fileInputStream);
              ByteArrayOutputStream baos = new ByteArrayOutputStream();
              bitmap.compress(Bitmap.CompressFormat.PNG, 90, baos);
              buffer = baos.toByteArray();

          }
      }*/

              imagePath = getFilePathForN(imageUri,Formulaire.this);
              file = new File(imagePath);
             Log.d("PATH ",file.getPath()) ;
              if (!file.exists())

                  Log.e("uploadFile", "Source File not exist :" + imageUri.getPath());
              else {
                  FileInputStream fileInputStream = new FileInputStream(file);
                  buffer = new byte[buffersize];
                  bitmap = BitmapFactory.decodeStream(fileInputStream);
                  ByteArrayOutputStream baos = new ByteArrayOutputStream();
                  bitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
                  buffer = baos.toByteArray();


              }



          try {
              encodeImage = Base64.encodeToString(buffer, Base64.NO_WRAP);
              Log.i("MINION", "image conversted to Base 64 string");

              //Converting encodedImage to String Entity
              //StringEntity se = new StringEntity(encodedImage);
              Log.i("MINION", "encodedImage to StringEntity");
              Log.i("ENCODE", encodeImage);
          }
          catch (Exception e)
          {

          }



        return encodeImage;

    }


    private static String getFilePathForN(Uri uri, Context context) {
        Uri returnUri = uri;
        Cursor returnCursor = context.getContentResolver().query(returnUri, null, null, null, null);
        /*
         * Get the column indexes of the data in the Cursor,
         *     * move to the first row in the Cursor, get the data,
         *     * and display it.
         * */
        int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
        returnCursor.moveToFirst();
        String name = (returnCursor.getString(nameIndex));
        String size = (Long.toString(returnCursor.getLong(sizeIndex)));
        File file = new File(context.getFilesDir(), name);
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            FileOutputStream outputStream = new FileOutputStream(file);
            int read = 0;
            int maxBufferSize = 1 * 1024 * 1024;
            int bytesAvailable = inputStream.available();

            //int bufferSize = 1024;
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);

            final byte[] buffers = new byte[bufferSize];
            while ((read = inputStream.read(buffers)) != -1) {
                outputStream.write(buffers, 0, read);
            }
            Log.e("File Size", "Size " + file.length());
            inputStream.close();
            outputStream.close();
            Log.e("File Path", "Path " + file.getPath());
            Log.e("File Size", "Size " + file.length());
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
        }
        return file.getPath();
    }



    static final int REQUEST_IMAGE_CAPTURE = 1;
    Uri uri = null;
    //Permet de prendre une photo avec son appareil
    static final int REQUEST_TAKE_PHOTO = 1;

  private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent

        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
           uri  =FileProvider.getUriForFile(this,
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
               takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);


            }
        }
    }



    String currentPhotoPath= null;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents

            currentPhotoPath = image.getAbsolutePath();




        return image;
    }


}