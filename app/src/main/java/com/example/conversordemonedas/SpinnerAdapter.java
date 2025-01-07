package com.example.conversordemonedas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class SpinnerAdapter extends ArrayAdapter<SpinnerMonedas> {

    private final List<SpinnerMonedas> monedasList;
    public SpinnerAdapter(@NonNull Context context, @NonNull List<SpinnerMonedas> objects) {
        super(context, 0, objects);
        this.monedasList = objects; // Guarda la lista para poder modificarla.
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position, convertView, parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Mostrar solo los primeros elementos
        return initView(position, convertView, parent);
    }

    private View initView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);
        }
        TextView monedasNombre_tv = convertView.findViewById(R.id.monedas_nombre);
        ImageView monedasImagen = convertView.findViewById(R.id.monedas_imagen);
        SpinnerMonedas currentMoneda = getItem(position);
        if (currentMoneda != null) {
            monedasNombre_tv.setText(currentMoneda.getMonedasNombre());
            monedasImagen.setImageResource(currentMoneda.getMonedasImg());
        }
        return convertView;
    }



}

