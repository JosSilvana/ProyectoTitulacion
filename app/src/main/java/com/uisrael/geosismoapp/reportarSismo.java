package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class reportarSismo extends AppCompatActivity implements OnMapReadyCallback {

    //Declaracion de variables
    private Bundle datoRes;
    private TextView idEvento, horaLocal, magnitud, ciudadMasCercana;
    private Spinner spinner1, spinner2;
    private String keySismo, edificacion, tipoSismo, idUsuario, nombreUsuario, apellidoUsuario, profundidad;
    private int ps1, ps2;
    private Toolbar toolbar;
    private LatLng miUbicacion;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    //Creacion de instancia de servicios
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private ShareDialog shareDialog;
    private CallbackManager callbackManager;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_reportar_sismo);

        //Colocar barra de opciones del menu
        toolbar = findViewById(R.id.tool);
        setTitle("Reportar sismo");
        setSupportActionBar(toolbar);

        //Enlazar variables con las cajas de la interfaz
        idEvento = findViewById(R.id.tvIdEvento);
        horaLocal = findViewById(R.id.tvHoraLocal);
        magnitud = findViewById(R.id.tvMagnitud);
        ciudadMasCercana = findViewById(R.id.tvCiudadMasCercana);
        spinner1 =  findViewById(R.id.spinner);
        spinner2 =  findViewById(R.id.spinner2);

        //Traer el keySismo seleccionado
        datoRes=getIntent().getExtras();
        keySismo=datoRes.getString("keySismo");

        //Inicialziacion para compartir en redes sociales
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        //Conexión con la base de datos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        //Obtener los datos del sismo seleccionado
        obtenerSismo();

        //Obtener Soporte de fragmento de google
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.maps);
        supportMapFragment.getMapAsync(this);

        //Obtener los permisos de ubicacion
        getLocalizacion();

        //LLenar el snipper para edificacion
        String[] valores = {"Seleccionar","Dentro de una edificación","Fuera de una edificación"};
        spinner1.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valores));
        spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                edificacion = (String) adapterView.getItemAtPosition(position);
                ps1 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        //Llenar el snipper para como sintio el sismo
        String[] valores1 = {"Seleccionar","Leve (Apenas percibido)","Moderado (Hubo susto, salió hacia el exterior)", "Fuerte (Corrió hacia el exterior)", "Muy fuerte (Pánico general, perdida de estabilidad)"};
        spinner2.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, valores1));
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id)
            {
                tipoSismo = (String) adapterView.getItemAtPosition(position);
                ps2 = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        try {
            obtenerDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Método para crear menu de la aplicación
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    //Método para seleccionar las opciones del menu
    public boolean onOptionsItemSelected(MenuItem menuItem){
        switch (menuItem.getItemId()){
            case R.id.inicio:
                Intent irInicio = new Intent(this, inicio.class);
                startActivity(irInicio);
                break;
            case R.id.usuario:
                Intent irPefilUsuario = new Intent(this, perfilUsuario.class);
                startActivity(irPefilUsuario);
                break;
            case R.id.listaSismos:
                Intent irListaSismo = new Intent(this, listaSismos.class);
                startActivity(irListaSismo);
                break;
            case R.id.listaSismosReportados:
                Intent irListaSismoReportados = new Intent(this, listaSismosReportados.class);
                startActivity(irListaSismoReportados);
                break;
            case R.id.cerrarSesion:
                mAuth.signOut();
                cerrarSesionGoogle();
                Intent irLogin = new Intent(this, login.class);
                startActivity(irLogin);
                break;
        }
        return true;
    }

    //Metodo para cerrar sesion con Google
    public void cerrarSesionGoogle(){
        //Cerrar sesión con google tambien: Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Abrir MainActivity con SigIn button
                if(task.isSuccessful()){
                    Intent mainActivity = new Intent(getApplicationContext(), login.class);
                    startActivity(mainActivity);
                    reportarSismo.this.finish();
                }else{
                }
            }
        });                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google", Toast.LENGTH_LONG).show();

    }

    //Metodo para obtener datos de un sismo
    public void obtenerSismo(){
        mDatabase.child("sismos").child(keySismo).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    //idEvento.setText(snapshot.child("idEvento").getValue().toString());
                    horaLocal.setText(snapshot.child("horaLocal").getValue().toString());
                    magnitud.setText(snapshot.child("magnitud").getValue().toString());
                    ciudadMasCercana.setText(snapshot.child("ciudadMasCercana").getValue().toString());
                    profundidad = snapshot.child("profundidad").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Metodo para compartir sismo en WhatsApp
    public void compartirWhatsApp(View v){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.whatsapp");
        intent.putExtra(Intent.EXTRA_TEXT, "*SOLO ES UNA PRUEBA SISMO* \n"+"*Hora local:* "+horaLocal.getText().toString()+"\n"
                +"*Magnitud:* "+magnitud.getText().toString()+"\n"+"*Ciudad mas cercana:* "+ciudadMasCercana.getText().toString()
                +"\n"+"*Profundidad:* "+profundidad);
        try {
            startActivity(intent);
            Map<String, Object> map = new HashMap<>();
            map.put("idUsuario", idUsuario);
            map.put("idSismo",keySismo);
            mDatabase.child("redes sociales").child("whatsapp").child(mDatabase.push().getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                    }else{
                        Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Snackbar.make(v, "El dispositivo no tiene instalado WhatsApp", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    //Metodo para compartir sismo en Twitter
    public void compartirTwitter(View v){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.setPackage("com.twitter.android");
        intent.putExtra(Intent.EXTRA_TEXT, "*SOLO ES UNA PRUEBA SISMO* \n"+"Hora local: "+horaLocal.getText().toString()+"\n"
                +"Magnitud: "+magnitud.getText().toString()+"\n"+"Ciudad mas cercana: "+ciudadMasCercana.getText().toString()
                +"\n"+"Profundidad: "+profundidad);
        try {
            startActivity(intent);
            Map<String, Object> map = new HashMap<>();
            map.put("idUsuario", idUsuario);
            map.put("idSismo",keySismo);
            mDatabase.child("redes sociales").child("twitter").child(mDatabase.push().getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                    }else{
                        Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        } catch (android.content.ActivityNotFoundException ex) {
            ex.printStackTrace();
            Snackbar.make(v, "El dispositivo no tiene instalado Twitter", Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    //Metodo para compartir sismo en Facebook
    public void compartirFacebook(View v){
        ShareLinkContent linkContent = new ShareLinkContent.Builder()
                .setQuote("SOLO ES UNA PRUEBA SISMO: \n"+"Hora local: "+horaLocal.getText().toString()+"\n"
                        +"Magnitud: "+magnitud.getText().toString()+"\n"+"Ciudad mas cercana: "+ciudadMasCercana.getText().toString()
                        +"\n"+"Profundidad: "+profundidad)
                .setContentUrl(Uri.parse("https://developers.facebook.com"))
                .build();
        if(ShareDialog.canShow(ShareLinkContent.class)){
            shareDialog.show(linkContent);
            Map<String, Object> map = new HashMap<>();
            map.put("idUsuario", idUsuario);
            map.put("idSismo",keySismo);
            mDatabase.child("redes sociales").child("facebook").child(mDatabase.push().getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                    }else{
                        Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //Metodo para obtener permisos para acceder a la ubicacion actual del usuario
    private void getLocalizacion() {
        int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if(permiso == PackageManager.PERMISSION_DENIED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    //Metodo para situar la ubicacion actual de usuario con un marcador
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);

        mMap.getUiSettings().setMyLocationButtonEnabled(false);

        LocationManager locationManager = (LocationManager) reportarSismo.this.getSystemService(Context.LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                miUbicacion = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.addMarker(new MarkerOptions().position(miUbicacion).title("Mi ubicación actual"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(miUbicacion)
                        .zoom(14)
                        .bearing(90)
                        .tilt(45)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


    }

    //Método para reportar sismo
    public void reportarSismo(View v) {
         idUsuario = mAuth.getCurrentUser().getUid();
        //Registro en nodo usuario
        mDatabase.child("usuarios").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nombreUsuario = snapshot.child("nombre").getValue().toString();
                    apellidoUsuario = snapshot.child("apellido").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        String fechaHora = dateFormat.format(date);
        Map<String, Object> map = new HashMap<>();
        map.put("fechaHora",fechaHora);
        map.put("localizacion",miUbicacion);
        map.put("edificacion", edificacion);
        map.put("tipoSismo", tipoSismo);
        if(ps1 == 0 || ps2 == 0){
            Toast.makeText(getApplicationContext(),"Favor selecciona una opción en Cómo y donde sentiste el sismo", Toast.LENGTH_LONG).show();
        }else{
            mDatabase.child("usuarios").child(idUsuario).child("reporteSismo").child(keySismo).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"¡Registro exitoso!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                    }
                }
            });
            //Registro en nodo sismo
            Map<String, Object> map1 = new HashMap<>();
            map1.put("nombre", nombreUsuario);
            map1.put("apellido",apellidoUsuario);
            mDatabase.child("sismos").child(keySismo).child("reporteUsuario").child(idUsuario).updateChildren(map1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task2) {
                    if(task2.isSuccessful()){
                        Toast.makeText(getApplicationContext(),"¡Registro exitoso!", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    //Método para obtener obtener datos del usuario
    public void obtenerDatos() throws Exception {
        idUsuario = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(idUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    nombreUsuario = snapshot.child("nombre").getValue().toString();
                    apellidoUsuario = snapshot.child("apellido").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
