package com.example.conversordemonedas;

import android.os.AsyncTask;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

// Clase para consultar la tasa de conversión
public class ConsultarMonedas {

    public static class ConsultaMonedaAsync extends AsyncTask<String, Void, Monedas> {
        private final ConversionListener listener;

        public ConsultaMonedaAsync(ConversionListener listener) {
            this.listener = listener;
        }

        @Override
        protected Monedas doInBackground(String... params) {
            String monedaActual = params[0];
            String monedaConvertir = params[1];
            String urlString = "https://v6.exchangerate-api.com/v6/1a5e16cb06b45bbffd2d116d/pair/" + monedaActual + "/" + monedaConvertir;

            HttpURLConnection connection = null;

            try {
                // Configurar conexión HTTP
                URL url = new URL(urlString);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000); // Tiempo máximo de conexión
                connection.setReadTimeout(5000);    // Tiempo máximo de lectura

                // Verificar respuesta
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) { // Código 200
                    InputStream inputStream = connection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    reader.close();
                    inputStream.close();

                    // Convertir respuesta JSON a un objeto Monedas
                    return new Gson().fromJson(response.toString(), Monedas.class);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
            return null; // En caso de error
        }

        @Override
        protected void onPostExecute(Monedas monedas) {
            if (monedas != null) {
                listener.onConversionComplete(monedas);
            } else {
                listener.onError("No se pudo obtener la tasa de conversión");
            }
        }

        public interface ConversionListener {
            void onConversionComplete(Monedas monedas);
            void onError(String error);
        }
    }
}
