package com.example.conversordemonedas;

import static com.example.conversordemonedas.R.id.resultText;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Spinner spin, spin2;
    ArrayList<SpinnerMonedas> monedasList;
    SpinnerAdapter adapter, adapter2;
    TextInputEditText cantidadInput;  // Campo de entrada para la cantidad
    TextView resultText;     // Campo para mostrar el resultado de la conversión

    private static final long TYPING_DELAY = 200; // 200ms de retraso
    private Handler handler = new Handler(Looper.getMainLooper());
    private Runnable typingRunnable = new Runnable() {
        @Override
        public void run() {
            realizarConversion();  // Llamamos a la conversión después del retraso
        }
    };

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
        spin.setAdapter(adapter);// Establece el adaptador para el Spinner de origen
        adapter2 = new SpinnerAdapter(this, new ArrayList<>(monedasList)); // Copia de la lista completa
        spin2.setAdapter(adapter2);

        // Seleccionamos valores iniciales diferentes
        spin.setSelection(0); // USD
        spin2.setSelection(1); // ARS


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

        cantidadInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                handler.removeCallbacks(typingRunnable); // Eliminar el retraso anterior
                handler.postDelayed(typingRunnable, TYPING_DELAY); // Esperar 500ms antes de hacer la conversión
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnSwap.setOnClickListener(v -> {
            if (spin.getSelectedItem() != null && spin2.getSelectedItem() != null) {
                int pos1 = spin.getSelectedItemPosition();
                int pos2 = spin2.getSelectedItemPosition();
                spin.setSelection(pos2);
                spin2.setSelection(pos1);
                realizarConversion();
            } else {
                Toast.makeText(this, "Seleccione ambas monedas primero", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initList() {
        monedasList = new ArrayList<>();
        monedasList.add(new SpinnerMonedas("USD", R.drawable.usa_icon));
        monedasList.add(new SpinnerMonedas("ARS", R.drawable.argentina_icon));
        monedasList.add(new SpinnerMonedas("BRL", R.drawable.brasil_icon));
        monedasList.add(new SpinnerMonedas("COP", R.drawable.colombia_icon));
        monedasList.add(new SpinnerMonedas("AED", R.drawable.united_arabe_mirates_icon));
        monedasList.add(new SpinnerMonedas("AFN", R.drawable.afghanistan_icon));
        monedasList.add(new SpinnerMonedas("ALL", R.drawable.albania_icon));
        monedasList.add(new SpinnerMonedas("AMD", R.drawable.armenia_icon));
        monedasList.add(new SpinnerMonedas("ANG", R.drawable.netherlands_antilles_icon));
        monedasList.add(new SpinnerMonedas("AOA", R.drawable.angola_icon));
        monedasList.add(new SpinnerMonedas("AUD", R.drawable.australia_icon));
        monedasList.add(new SpinnerMonedas("AWG", R.drawable.aruba_icon));
        monedasList.add(new SpinnerMonedas("AZN", R.drawable.azerbaijan_icon));
        monedasList.add(new SpinnerMonedas("BAM", R.drawable.bosnia_and_herzegovina_icon));
        monedasList.add(new SpinnerMonedas("BBD", R.drawable.barbados_icon));
        monedasList.add(new SpinnerMonedas("BDT", R.drawable.bangladesh_icon));
        monedasList.add(new SpinnerMonedas("BGN", R.drawable.bulgaria_icon));
        monedasList.add(new SpinnerMonedas("BHD", R.drawable.bahrain_icon));
        monedasList.add(new SpinnerMonedas("BIF", R.drawable.burundi_icon));
        monedasList.add(new SpinnerMonedas("BMD", R.drawable.bermuda_icon));
        monedasList.add(new SpinnerMonedas("BND", R.drawable.brunei_icon));
    }

    private void realizarConversion() {
        SpinnerMonedas monedaOrigen = (SpinnerMonedas) spin.getSelectedItem();
        SpinnerMonedas monedaDestino = (SpinnerMonedas) spin2.getSelectedItem();

        if (monedaOrigen == null || monedaDestino == null) {
            resultText.setText("Seleccione ambas monedas");
            return;
        }

        String cantidadStr = cantidadInput.getText().toString().trim();
        if (cantidadStr.isEmpty() || !cantidadStr.matches("\\d+(\\.\\d+)?")) {
            resultText.setText("Ingrese una cantidad válida");
            return;
        }

        double cantidad = Double.parseDouble(cantidadStr);

        new ConsultarMonedas.ConsultaMonedaAsync(new ConsultarMonedas.ConsultaMonedaAsync.ConversionListener() {
            @Override
            public void onConversionComplete(Monedas monedas) {
                if (monedas == null || monedas.getConversionRate() <= 0) {
                    Toast.makeText(MainActivity.this, "Error al obtener la tasa de conversión", Toast.LENGTH_SHORT).show();
                    return;
                }
                double tasaDeConversion = monedas.getConversionRate();
                double resultado = cantidad * tasaDeConversion;
                resultText.setText(String.format("%.2f %s = %.2f %s",
                        cantidad, monedaOrigen.getMonedasNombre(),
                        resultado, monedaDestino.getMonedasNombre()));
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        }).execute(monedaOrigen.getMonedasNombre(), monedaDestino.getMonedasNombre());
    }
}
