package com.utn.mobile.keepapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.utn.mobile.keepapp.domain.Ejercicio;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Joel on 28/7/2017.
 */

public class EjerciciosAdapter extends BaseAdapter implements ListAdapter {
    private List<Ejercicio> lista = new ArrayList<Ejercicio>();
    private Context contexto;
    TextView textview_ejercicio;
    TextView textview_record;
    TextView textview_fecha;
    ImageView imageview_ejercicio;

    public EjerciciosAdapter(List<Ejercicio> list, Context context) {
        this.lista = list;
        this.contexto = context;
    }

    @Override
    public int getCount() {
        return lista.size();
    }

    @Override
    public Object getItem(int pos) {
        return lista.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        //return list.get(pos).getId();
        //just return 0 if your list items do not have an Id variable.
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_record, null);
        }

        //Handle TextView and display string from your list
        textview_ejercicio = (TextView)view.findViewById(R.id.texto_ejercicio_item_record);
        textview_ejercicio.setText(lista.get(position).getNombre());

        textview_record = (TextView)view.findViewById(R.id.texto_record_item_record);
        textview_record.setText("Mejor record: " + lista.get(position).getValor() + " " + lista.get(position).getUnidad());

        textview_fecha = (TextView)view.findViewById(R.id.texto_fecha_item_record);

        textview_fecha.setText(lista.get(position).getFecha());

        imageview_ejercicio = (ImageView) view.findViewById(R.id.imagen_item_record);
        if(lista.get(position).getImagen() != null) {
            Picasso.with(this.contexto).load(lista.get(position).getImagen()).into(imageview_ejercicio);
        }
        return view;
    }


}
