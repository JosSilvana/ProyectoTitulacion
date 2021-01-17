package com.uisrael.geosismoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

public class listaSismosReportados extends AppCompatActivity {

    //Declaracion de variables
    private String recibirIdUsuario;
    private ListView listSismosReportados;
    private ArrayList<String> listaSismoReportado = new ArrayList<>();
    private ArrayAdapter<String> listaSismosReportadosAdapter;
    private Toolbar toolbar;

    //Variables opcionales para desloguear de google tambien
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;

    //Creacion de instancia de servicios
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_sismos_reportados);

        //Colocar barra de opciones del menu
        toolbar = findViewById(R.id.tool);
        setTitle("Lista de sismos reportados");
        setSupportActionBar(toolbar);

        //Enlazar la lista con la vista
        listSismosReportados = findViewById(R.id.listaSismoRerpotados);

        //Conexión con la base de datos
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        //Configurar las gso para google signIn con el fin de luego desloguear de google
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        recibirIdUsuario = mAuth.getCurrentUser().getUid();
        listaSismosReportados();
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
                    listaSismosReportados.this.finish();
                }else{
                    Toast.makeText(getApplicationContext(), "No se pudo cerrar sesión con google",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    //Método para obtener la lista de sismos reportados por el usuario
    public void listaSismosReportados(){
        mDatabase.child("usuarios").child(recibirIdUsuario).child("reporteSismo").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for (DataSnapshot s: snapshot.getChildren()){
                            listaSismoReportado.add("IdSismo: "+s.getKey() +"\n"+"fecha del Reporte: "+s.child("fechaHora").getValue().toString()
                                    +"\n"+"Edificación: "+s.child("edificacion").getValue().toString()+"\n"+"Tipo sismo: "+s.child("tipoSismo").getValue().toString()+"\n");
                    }
                    listaSismosReportadosAdapter = new ArrayAdapter<String>(listaSismosReportados.this,android.R.layout.simple_list_item_1, listaSismoReportado);
                    listSismosReportados.setAdapter(listaSismosReportadosAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
