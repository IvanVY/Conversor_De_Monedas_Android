package com.example.conversordemonedas;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Spinner spin;

    ArrayList<SpinnerMonedas> monedasList;
    SpinnerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        //Crear pares
        Map<String, String> paresMonedas = new HashMap<>();
        paresMonedas.put("ARS","USD ");

        spin = findViewById(R.id.spinnerMonedas);
        initList();
        adapter = new SpinnerAdapter(this,monedasList);
        spin.setAdapter(adapter);
        spin.setOnItemSelectedListener(this);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,monedas_list);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //spin.setAdapter(adapter);
        //spin.setOnItemSelectedListener(this);
    }

    private void initList() {
        monedasList = new ArrayList<>();
        monedasList.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
        monedasList.add(new SpinnerMonedas("ARS", R.drawable.argentina_icon));
        monedasList.add(new SpinnerMonedas("BRL", R.drawable.brasil_icon));
        monedasList.add(new SpinnerMonedas("COP", R.drawable.colombia_icon));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (position != 0){// Solo mueve si no está ya en la primera posición
            adapter.moveItemToFirst(position); // Llama al metodo para mover el item
            spin.setSelection(0);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // No hace nada si no hay selección
    }
}