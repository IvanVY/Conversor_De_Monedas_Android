package com.example.conversordemonedas;

import static com.example.conversordemonedas.R.id.resultText;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Spinner spin, spin2;
    ArrayList<SpinnerMonedas> monedasList;
    SpinnerAdapter adapter, adapter2;
    boolean isUpdatingFromSecondSpinner = false; // Control para evitar bucles infinitos
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

        // Inicializa la lista de monedas
        initList();

        // Configura los adaptadores para los Spinners
        adapter = new SpinnerAdapter(this, monedasList);
        adapter2 = new SpinnerAdapter(this, new ArrayList<>()); // Vacío inicialmente

        spin.setAdapter(adapter);
        spin2.setAdapter(adapter2);

        // Configura los listeners
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Cuando la moneda del spinner 1 cambia, actualizamos el spinner 2
                updateSecondSpinner();
                realizarConversion();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spin2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (!isUpdatingFromSecondSpinner) {
                    updateFirstSpinner(spin2, spin);
                    realizarConversion();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Configura las opciones iniciales del Spinner secundario
        updateSecondSpinner();
    }

    private void initList() {
        monedasList = new ArrayList<>();
        monedasList.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
        monedasList.add(new SpinnerMonedas("ARS", R.drawable.argentina_icon));
        monedasList.add(new SpinnerMonedas("BRL", R.drawable.brasil_icon));
        monedasList.add(new SpinnerMonedas("COP", R.drawable.colombia_icon));
    }

    private void updateSecondSpinner() {
        // Aseguramos que el segundo spinner tenga todas las monedas disponibles
        adapter2.clear();
        adapter2.addAll(monedasList);  // Usamos monedasList que ya tiene todas las monedas
        adapter2.notifyDataSetChanged();

        // No necesitamos modificar el primer valor aquí, ya que estamos manejando el intercambio
        // pero podemos seleccionar la primera moneda si queremos inicializar la selección.
        spin2.setSelection(0);  // Esto es opcional si deseas que siempre el primer valor esté seleccionado en el segundo spinner.
    }

    private void updateFirstSpinner(Spinner sourceSpinner, Spinner targetSpinner) {
        SpinnerMonedas selectedMoneda = (SpinnerMonedas) sourceSpinner.getSelectedItem();
        ArrayList<SpinnerMonedas> newOptions = new ArrayList<>();

        if (selectedMoneda != null) {
            switch (selectedMoneda.getMonedasNombre()) {
                case "ARS":
                    newOptions.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
                    break;
                case "BRL":
                    newOptions.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
                    break;
                case "COP":
                    newOptions.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
                    break;
                case "USD":
                    newOptions.add(new SpinnerMonedas("ARS", R.drawable.argentina_icon));
                    newOptions.add(new SpinnerMonedas("BRL", R.drawable.brasil_icon));
                    newOptions.add(new SpinnerMonedas("COP", R.drawable.colombia_icon));
                    break;
            }
        }

        // Actualiza el contenido del Spinner de destino
        SpinnerAdapter targetAdapter = (SpinnerAdapter) targetSpinner.getAdapter();
        targetAdapter.clear();
        targetAdapter.addAll(newOptions);
        targetAdapter.notifyDataSetChanged();

        // Establece la selección al primer elemento de la lista actualizada
        if (!newOptions.isEmpty()) {
            targetSpinner.setSelection(0);
        }
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

        // Llama al AsyncTask para obtener la tasa de conversión
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
