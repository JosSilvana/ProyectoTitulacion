package com.uisrael.geosismoapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class listAdapterSismos extends ArrayAdapter<sismos> {

    //Declarion de variables
    private List<sismos> listaSismos;
    private Context mcontext;
    private int resourceLayout;

    //Constructor con parametros
    public listAdapterSismos(@NonNull Context context, int resource, @NonNull List<sismos> listaProductos) {
        super(context, resource, listaProductos);
        this.listaSismos = listaProductos;
        this.mcontext = context;
        this.resourceLayout = resource;
    }

    //Método para colocar los datos de sismos en una listView
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null)
            view = LayoutInflater.from(mcontext).inflate(resourceLayout, null);

        sismos sismosL = listaSismos.get(position);

        TextView horaLocal = view.findViewById(R.id.ltvHoraLocal);
        horaLocal.setText(sismosL.getHoraLocal());

        TextView magnitud = view.findViewById(R.id.ltvMagnitud);
        magnitud.setText(sismosL.getMagnitud());

        TextView ciudadMasCercana = view.findViewById(R.id.ltvCiudadCercana);
        ciudadMasCercana.setText(sismosL.getCiudadMasCercana());

        TextView profundidad = view.findViewById(R.id.ltvProfundidad);
        profundidad.setText(sismosL.getProfundidad());

        return view;
    }
}
