package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;
import java.util.Map;

public class inicioGoogle extends AppCompatActivity  {


    //Declaración de variables
    private TextView nombre, correo, apellido, idUser;

    //Creacion de instancia de servicios
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_google);

        nombre = findViewById(R.id.tvIdEvento);
        correo = findViewById(R.id.tvEmail);
        apellido = findViewById(R.id.tvApellido);
        idUser = findViewById(R.id.tvIdUsuario);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        registrarCredenciales();
    }

    //Método para registrar usuario si no se encuentra en la base de datos
    public void registrarCredenciales(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        correo.setText(currentUser.getEmail());
        String nombres = currentUser.getDisplayName();
        String[] NombreApellido = nombres.split(" ");
        nombre.setText(NombreApellido[0]);
        apellido.setText(NombreApellido[1]);
        idUser.setText(currentUser.getUid());
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(this, new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                final String newToken = instanceIdResult.getToken();
        mDatabase.child("usuarios").orderByChild("correo").equalTo(correo.getText().toString()).limitToFirst(1).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists()){
                        Intent intentEnvio = new Intent(inicioGoogle.this, inicio.class);
                        startActivity(intentEnvio);
                    }else{
                        Map<String, Object> map = new HashMap<>();
                        map.put("nombre",nombre.getText().toString());
                        map.put("apellido",apellido.getText().toString());
                        map.put("correo", correo.getText().toString());
                        map.put("contrasena", "");
                        map.put("token", newToken);
                        mDatabase.child("usuarios").child(idUser.getText().toString()).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task2) {
                                if(task2.isSuccessful()){
                                    Toast.makeText(getApplicationContext(),"¡Usuario registrado exitosamente!", Toast.LENGTH_LONG).show();
                                    Intent intentEnvio = new Intent(inicioGoogle.this, inicio.class);
                                    startActivity(intentEnvio);
                                }else{
                                    Toast.makeText(getApplicationContext(),"¡No se registraron los datos en la base de datos!", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

            }
        });
    }
}
