package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class actualizarContrasena extends AppCompatActivity {

    //Definiciones de variables
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private EditText etContraseñaUser, etNuevaContraseñaUser, etConfirmarContraseñaUser;
    private String passwordEncriptacion = "gdsawr";
    private String recibirIdUsuario, contrasenaUser;

    //Creacion de instancia de servicios
    private DatabaseReference mDatabase;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_contrasena);

        //Colocar barra de opciones del menu
        toolbar = findViewById(R.id.tool);
        setTitle("Actualizar Contraseña");
        setSupportActionBar(toolbar);

        //Enlazar las variables a las cajas de texto
        etContraseñaUser= findViewById(R.id.etContraseña);
        etNuevaContraseñaUser= findViewById(R.id.etNuevaContrasenia);
        etConfirmarContraseñaUser= findViewById(R.id.etNuevaContraseniaC);

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
        mDatabase.child("usuarios").child(recibirIdUsuario).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    contrasenaUser = snapshot.child("contrasena").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    //Métodp para encriptar la contrasseña
    private String encriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;
    }

    //Método para desencriptar la contraseña al momento de realizar la comparación para la actualización.
    private String desencriptar(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] datosDescoficados = Base64.decode(datos, Base64.DEFAULT);
        byte[] datosDesencriptadosByte = cipher.doFinal(datosDescoficados);
        String datosDesencriptadosString = new String(datosDesencriptadosByte);
        return datosDesencriptadosString;
    }

    //Método para generar una llava para encriptar la contraseña
    private SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }

    //Método para validar que ingrese los datos correctos
    public boolean validarCampos(String contraseña, String ncontraseña, String vcontraseña){
        boolean valor= true;
        if(contraseña.isEmpty()){
            etContraseñaUser.setError("No ha ingresado la contraseña");
            valor=false;
        }
        if(ncontraseña.isEmpty()){
            etNuevaContraseñaUser.setError("No ha ingresado  la contraseña");
            valor=false;
        }
        if(vcontraseña.isEmpty()){
            etConfirmarContraseñaUser.setError("No ha ingresado  la contraseña");
            valor=false;
        }
        return valor;
    }

    //Método para actualizar la contraseña
    public void actualizarContraseña(View v) throws Exception {
        String contraseña = etContraseñaUser.getText().toString().trim();
        String ncontraseña = etNuevaContraseñaUser.getText().toString().trim();
        String vcontraseña = etConfirmarContraseñaUser.getText().toString().trim();
        if (validarCampos(contraseña, ncontraseña,vcontraseña)== true) {
            String Descontrasenia = desencriptar(contrasenaUser, passwordEncriptacion);
            if (verificarContraseña(ncontraseña, vcontraseña) == true) {
                VerificarCrendenciales(contraseña, Descontrasenia, ncontraseña);
            }
        }
    }

    //Método para verificar las contraseñas ingresadas nuevo y antigua
    public void VerificarCrendenciales(String contraseña, String contraseñaUser, final String nuevaContraseña) throws Exception {
        if(contraseña.equals(contraseñaUser)==true ){
            String contraseniaEncriptada = encriptar(nuevaContraseña,passwordEncriptacion);
            Map<String, Object> map = new HashMap<>();
            map.put("contrasena",contraseniaEncriptada);
            mDatabase.child("usuarios").child(recibirIdUsuario).updateChildren(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.getCurrentUser().updatePassword(nuevaContraseña);
                    Toast.makeText(getApplicationContext(),"¡Contraseña actualizada!", Toast.LENGTH_LONG).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),"¡Error al actualizar la contraseña!", Toast.LENGTH_LONG).show();
                }
            });
            Intent intentEnvio = new Intent(this, login.class);
            startActivity(intentEnvio);

        }else{
            Toast.makeText(getApplicationContext(),"¡La contraseña indicada no coincide con los registros!", Toast.LENGTH_LONG).show();
        }
    }

    //Método para verificar que ingresa la contraseña correcta
    public boolean verificarContraseña(String contraseña, String rcontraseña) {
        if(contraseña.equals(rcontraseña)==true){
            return true;
        }else{
            Toast.makeText(getApplicationContext(),"¡Las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
            return false;
        }
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
                    actualizarContrasena.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
