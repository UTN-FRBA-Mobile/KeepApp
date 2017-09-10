package com.utn.mobile.keepapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.utn.mobile.keepapp.Ejercicio;

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
        textview_record.setText("Mejor record: - ");

        return view;
    }


}
