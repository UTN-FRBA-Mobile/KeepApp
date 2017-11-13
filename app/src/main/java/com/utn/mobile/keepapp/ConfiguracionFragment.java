package com.utn.mobile.keepapp;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;

import com.utn.mobile.keepapp.domain.Ejercicio;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Joel on 11/11/2017.
 */

public class ConfiguracionFragment extends Fragment {

    Spinner spinner_ordenar;
    Switch switch_vibrar;
    Switch switch_sonar;

    public static boolean vibrar = true;
    public static boolean sonar = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_configuracion, container,false);

        getActivity().setTitle("Configuración");

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinner_ordenar = (Spinner) getView().findViewById(R.id.spinner_orden);
        spinner_ordenar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Ejercicio.orden = spinner_ordenar.getSelectedItemPosition();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        switch_vibrar = (Switch)getView().findViewById(R.id.switch_vibrar);
        switch_vibrar.setChecked(vibrar);
        switch_vibrar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                vibrar = isChecked;
            }
        });

        switch_sonar = (Switch)getView().findViewById(R.id.switch_sonar);
        switch_sonar.setChecked(sonar);
        switch_sonar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                sonar = isChecked;
            }
        });

        cargarSpinnerOrdenar();
    }

    private void cargarSpinnerOrdenar()
    {
        List<String> spinnerArray =  new ArrayList<String>();
        spinnerArray.add("Alfabeticamente");
        spinnerArray.add("Fecha de carga");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                ConfiguracionFragment.this.getContext(),android.R.layout.simple_spinner_item, spinnerArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_ordenar.setAdapter(adapter);

        spinner_ordenar.setSelection(Ejercicio.orden);
    }

}
