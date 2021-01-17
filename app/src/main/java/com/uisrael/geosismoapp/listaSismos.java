package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class listaSismos extends AppCompatActivity {

    //Declaración de variables
    private Toolbar toolbar;
    private ListView listaSismos;
    private List<sismos> listaSismo = new ArrayList<>();
    private listAdapterSismos listaSismosAdapter;

    //Creacion de instancia de servicios
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sismos);

        //Colocar barra de opciones del menu
        toolbar = findViewById(R.id.tool);
        setTitle("Lista de sismos");
        setSupportActionBar(toolbar);


        //Conexión con la base de datos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Enlazar la variables con el elemento de la vista
        listaSismos = findViewById(R.id.listaSismos);
        listarSismos();

        //Método que nos lleva a reporte sismo luego de seleccionar un sismo de la listView
        listaSismos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent irReporteSismo = new Intent(listaSismos.getContext(), reportarSismo.class);
                irReporteSismo.putExtra("keySismo",listaSismo.get(position).getKeySismo());
                startActivity(irReporteSismo);

            }
        });

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
                    listaSismos.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Método para obtener la lista de los sismos de firebase
    public void listarSismos(){
        mDatabase.child("sismos").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot s: snapshot.getChildren()){
                        listaSismo.add(new sismos(s.getKey(),s.child("idEvento").getValue().toString(),s.child("horaLocal").getValue().toString(),s.child("magnitud").getValue().toString(),s.child("ciudadMasCercana").getValue().toString(),s.child("profundidad").getValue().toString()));
                    }
                    listaSismosAdapter = new listAdapterSismos(listaSismos.this,R.layout.sismos,listaSismo);
                    listaSismos.setAdapter(listaSismosAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
