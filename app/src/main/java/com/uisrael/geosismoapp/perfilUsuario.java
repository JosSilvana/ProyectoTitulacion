package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class perfilUsuario extends AppCompatActivity {

    //Definiciones de variables
    private Toolbar toolbar;
    private EditText etNombre, etApellido, etCorreo;
    private String recibirIdUsuario, recibirNombre, recibirApellido, recibirEmail;

    //Creacion de instancia de servicios
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_usuario);

        //Colocar barra de opciones del menu
        toolbar = findViewById(R.id.tool);
        setTitle("Datos del Usuario");
        setSupportActionBar(toolbar);

        //Enlazar las variables con las cajas de texto
        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etCorreo = findViewById(R.id.etEmail);

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

    //Método para editar datos del usuario
    public void editarRegistro(View v){
        final String nombre= etNombre.getText().toString().trim();
        final String apellido= etApellido.getText().toString().trim();
        final String correo= etCorreo.getText().toString().trim();
        recibirIdUsuario = mAuth.getCurrentUser().getUid();
        if (validarCampos(nombre, apellido,correo)== true){
            Map<String, Object> map = new HashMap<>();
            map.put("nombre",nombre);
            map.put("apellido",apellido);
            map.put("correo", correo);
            mDatabase.child("usuarios").child(recibirIdUsuario).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.getCurrentUser().updateEmail(correo);
                    Toast.makeText(getApplicationContext(),"¡Datos actualizados correctamente!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"¡Ese correo ya se encuentra registrado!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    //Método para validar campos de datos del usuario
    public boolean validarCampos(String nombre, String apellido,String username){
        boolean valor= true;
        if(nombre.isEmpty()){
            etNombre.setError("No ha ingresado el nombre");
            valor=false;
        }
        if(apellido.isEmpty()){
            etApellido.setError("No ha ingresado el apellido");
            valor=false;
        }
        if(username.isEmpty()){
            etCorreo.setError("No ha ingresado el email");
            valor=false;
        }
        return valor;
    }

    //Metodo para ir la ventana de actualizar contraseña
    public void ventanaActualizarContraseña(View v){
        Intent intentEnvio = new Intent(this, actualizarContrasena.class);
        startActivity(intentEnvio);
    }

    //Método para obtener obtener datos del usuario
    public void obtenerDatos() throws Exception {
        recibirIdUsuario = mAuth.getCurrentUser().getUid();
        mDatabase.child("usuarios").child(recibirIdUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    recibirNombre = snapshot.child("nombre").getValue().toString();
                    recibirApellido = snapshot.child("apellido").getValue().toString();
                    recibirEmail = snapshot.child("correo").getValue().toString();
                    etNombre.setText(recibirNombre);
                    etApellido.setText(recibirApellido);
                    etCorreo.setText(recibirEmail);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
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
                    perfilUsuario.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

}
