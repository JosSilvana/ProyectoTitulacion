package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class registrarUsuario extends AppCompatActivity {

    //Declaración de variables
    private EditText etNombres, etApellidos, etContraseña, etRContraseña, etCorreo;
    private String passwordEncriptacion = "gdsawr";
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_usuario);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Enlazar las variables con las cajas de texto
        etNombres = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellido);
        etCorreo= findViewById(R.id.etEmail);
        etContraseña = findViewById(R.id.etContrasenia);
        etRContraseña = findViewById(R.id.etRcontrasenia);
    }

    //Método para verificar las contraseña
    public boolean verificarContraseña(String contraseña, String rcontraseña) {
        if(contraseña.equals(rcontraseña)==true){
            return true;
        }else{
            Toast.makeText(getApplicationContext(),"¡Las contraseñas no coinciden!", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    //Método para validar campos de datos del usuario
    public boolean validarCampos(String nombre, String apellido,String username,String contrasenia, String  rcontrasenia){
        boolean valor= true;
        if(nombre.isEmpty()){
            etNombres.setError("No ha ingresado el nombre");
            valor=false;
        }
        if(apellido.isEmpty()){
            etApellidos.setError("No ha ingresado el apellido");
            valor=false;
        }
        if(username.isEmpty()){
            etCorreo.setError("No ha ingresado el email");
            valor=false;
        }
        if(contrasenia.isEmpty()){
            etContraseña.setError("No ha ingresado la contraseña");
            valor=false;
        }
        if(rcontrasenia.isEmpty()){
            etRContraseña.setError("No ha ingresado la confirmación de contraseña");
            valor=false;
        }
        return valor;
    }

    //Método para encriptar contraseña
    private String encriptarContraseña(String datos, String password) throws Exception{
        SecretKeySpec secretKey = generateKey(password);
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] datosEncriptadosBytes = cipher.doFinal(datos.getBytes());
        String datosEncriptadosString = Base64.encodeToString(datosEncriptadosBytes, Base64.DEFAULT);
        return datosEncriptadosString;
    }

    //Método para generar llave de encriptación
    private SecretKeySpec generateKey(String password) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = password.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        return secretKey;
    }

    //Método para limpiar campos luego del registro
    public void limpiarDatos() {
        etNombres.setText("");
        etApellidos.setText("");
        etCorreo.setText("");
        etContraseña.setText("");
        etRContraseña.setText("");
    }

    //Método para ir a la ventana de login
    public void ventanaLogin(View v){
        Intent intentEnvio = new Intent(this, login.class);
        limpiarDatos();
        startActivity(intentEnvio);
    }

    //Método registrar usuario
    public void registrar(View v) throws Exception {
        final String nombre= etNombres.getText().toString().trim();
        final String apellido= etApellidos.getText().toString().trim();
        final String correo= etCorreo.getText().toString().trim();
        final String contrasenia= etContraseña.getText().toString().trim();
        String rcontrasenia= etRContraseña.getText().toString().trim();
        if (validarCampos(nombre, apellido,correo,contrasenia, rcontrasenia)== true){
            if(verificarContraseña(contrasenia,rcontrasenia)==true){
                FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        final String newToken = instanceIdResult.getToken();
                        mAuth.createUserWithEmailAndPassword(correo,contrasenia).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()){
                                    String id = mAuth.getCurrentUser().getUid();
                                    String valContraseña= null;
                                    try {
                                        valContraseña = encriptarContraseña(contrasenia, passwordEncriptacion);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("nombre",nombre);
                                    map.put("apellido",apellido);
                                    map.put("correo", correo);
                                    map.put("contrasena", valContraseña);
                                    map.put("token", newToken);
                                    mDatabase.child("usuarios").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task2) {
                                            if(task2.isSuccessful()){
                                                Toast.makeText(getApplicationContext(),"¡Usuario registrado exitosamente!", Toast.LENGTH_LONG).show();
                                                Intent intentEnvio = new Intent(registrarUsuario.this, login.class);
                                                limpiarDatos();
                                                startActivity(intentEnvio);
                                            }else{
                                                Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }else{
                                    Toast.makeText(getApplicationContext(),"¡Ese correo ya se encuentra registradoo!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }
        }
    }
}
