package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity{

    //Declaración de variables
    EditText etContraseña, etCorreo;
    private String user, password;

    //Constante
    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignIn";

    //Creacion de instancia de servicios
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Enlazar las variables con las cajas de texto
        etCorreo = findViewById(R.id.etEmail);
        etContraseña= findViewById(R.id.etContraseña);

        // Configurar Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Crear un GoogleSignInClient con las opciones especificadas por gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        //Conexión con firebase
        mAuth = FirebaseAuth.getInstance();
    }

    //Metodo para iniciar sesion con Google
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Resultado devuelto al iniciar el Intent de GoogleSignInApi.getSignInIntent (...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            if(task.isSuccessful()){
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = task.getResult(ApiException.class);
                    Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                    firebaseAuthWithGoogle(account.getIdToken());
                } catch (ApiException e) {
                    // Google Sign In fallido, actualizar GUI
                    Log.w(TAG, "Google sign in failed", e);
                }
            }else{
                Log.d(TAG, "Error, login no exitoso:" + task.getException().toString());
                Toast.makeText(this, "Ocurrio un error. "+task.getException().toString(),
                        Toast.LENGTH_LONG).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            // Iniciar DASHBOARD u otra actividad luego del SigIn Exitoso
                            Intent intentEnvio = new Intent(login.this, inicioGoogle.class);
                            startActivity(intentEnvio);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                        }
                    }
                });
    }

    //Método para validar los campos
    public boolean validarCampos(String emailUsuario, String contraseñaUsuario){
        boolean valor= true;
        if(emailUsuario.isEmpty()){
            etCorreo.setError("No ha ingresado el email");
            valor=false;
        }
        if(contraseñaUsuario.isEmpty()){
            etContraseña.setError("No ha ingresado  la contraseña");
            valor=false;
        }
        return valor;
    }

    //Método para iniciar sesión
    public void IniciarSesion(View v) throws Exception {
        user = etCorreo.getText().toString().trim();
        password = etContraseña.getText().toString().trim();
        if (validarCampos(user, password)== true){
            obtenerDatos();
        }
    }

    //Método para obtener los datos de inicio de sesión
    public void obtenerDatos() throws Exception {
        user = etCorreo.getText().toString().trim();
        password = etContraseña.getText().toString().trim();
        mAuth.signInWithEmailAndPassword(user,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intentEnvio = new Intent(getApplicationContext(), inicio.class);
                    startActivity(intentEnvio);
                }else{
                    Toast.makeText(getApplicationContext(),"¡Sus credenciales son Incorrectas!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Método para pasar de ventana
    public void ventanaRegistrar(View v){
        Intent intentEnvio = new Intent(getApplicationContext(), registrarUsuario.class);
        startActivity(intentEnvio);
    }

    //Método para mantener iniciado sesión mientras no presione el botón cerrar sesión
    @Override
    protected void onStart(){
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            Intent intentEnvio = new Intent(getApplicationContext(), inicio.class);
            startActivity(intentEnvio);
        }
    }

    //Método para iniciar sesión con Google
    public void iniciarConGoogle(View v){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
}
