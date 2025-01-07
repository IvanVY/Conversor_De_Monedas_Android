package com.example.conversordemonedas;

import static com.example.conversordemonedas.R.id.resultText;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spin, spin2;
    ArrayList<SpinnerMonedas> monedasList;
    SpinnerAdapter adapter;
    EditText cantidadInput;  // Campo de entrada para la cantidad
    TextView resultText;     // Campo para mostrar el resultado de la conversión

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spin = findViewById(R.id.spinnerMonedas);
        spin2 = findViewById(R.id.spinnerMonedas2);
        cantidadInput = findViewById(R.id.cantidadInput);  // Asegúrate de que este campo esté en tu layout XML
        resultText = findViewById(R.id.resultText);        // Asegúrate de que este campo esté en tu layout XML
        ImageButton btnSwap = findViewById(R.id.btnSwap);

        // Inicializa la lista de monedas
        initList();

        // Configura los adaptadores para los Spinners
        adapter = new SpinnerAdapter(this, monedasList);

        spin.setAdapter(adapter);
        spin2.setAdapter(adapter);

        // Configura los listeners
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realizarConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                realizarConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnSwap.setOnClickListener(v -> {
            int pos1 = spin.getSelectedItemPosition();
            int pos2 = spin2.getSelectedItemPosition();
            spin.setSelection(pos2);
            spin2.setSelection(pos1);
        });
    }

    private void initList() {
        monedasList = new ArrayList<>();
        monedasList.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
        monedasList.add(new SpinnerMonedas("ARS", R.drawable.argentina_icon));
        monedasList.add(new SpinnerMonedas("BRL", R.drawable.brasil_icon));
        monedasList.add(new SpinnerMonedas("COP", R.drawable.colombia_icon));
    }

    private void realizarConversion() {
        SpinnerMonedas monedaOrigen = (SpinnerMonedas) spin.getSelectedItem();
        SpinnerMonedas monedaDestino = (SpinnerMonedas) spin2.getSelectedItem();

        if (monedaOrigen == null || monedaDestino == null) {
            resultText.setText("Seleccione ambas monedas");
            return;
        }

        String cantidadStr = cantidadInput.getText().toString();
        if (cantidadStr.isEmpty()) {
            resultText.setText("Ingrese una cantidad válida");
            return;
        }

        double cantidad = Double.parseDouble(cantidadStr);

        new ConsultarMonedas.ConsultaMonedaAsync(new ConsultarMonedas.ConsultaMonedaAsync.ConversionListener() {
            @Override
            public void onConversionComplete(Monedas monedas) {
                double tasaDeConversion = monedas.getConversionRate();
                double resultado = cantidad * tasaDeConversion;
                resultText.setText(String.format("%.2f %s = %.2f %s", cantidad, monedaOrigen.getMonedasNombre(), resultado, monedaDestino.getMonedasNombre()));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }).execute(monedaOrigen.getMonedasNombre(), monedaDestino.getMonedasNombre());
    }
}
