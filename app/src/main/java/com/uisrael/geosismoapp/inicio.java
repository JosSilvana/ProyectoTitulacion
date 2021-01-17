package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class inicio extends AppCompatActivity {

    //Definicion de variables
    private Toolbar toolbar;
    private String recibirIdUsuario, recibirNombre, recibirApellido, newToken;;
    private TextView mostrarUsuario;

    //Creacion de instancia de servicios
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);

        //Enlazar las variables con las cajas de texto
        mostrarUsuario = findViewById(R.id.tvUsuario);

        //Colocar barra de opciones del menu
        toolbar = findViewById(R.id.tool);
        setTitle("Inicio");
        setSupportActionBar(toolbar);

        //Conexión con la base de datos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        try {
            obtenerDatos();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

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

    //Método para obtener obtener datos del usuario
    public void obtenerDatos() throws Exception {

        recibirIdUsuario = mAuth.getCurrentUser().getUid();
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                newToken = instanceIdResult.getToken();
                Log.i("token",newToken);
            }
        });
        mDatabase.child("usuarios").child(recibirIdUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    recibirNombre = snapshot.child("nombre").getValue().toString();
                    recibirApellido = snapshot.child("apellido").getValue().toString();
                    mostrarUsuario.setText(recibirNombre + " "+ recibirApellido);
                    Map<String, Object> map = new HashMap<>();
                    map.put("token", newToken);
                    mDatabase.child("usuarios").child(recibirIdUsuario).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
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
                    inicio.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
