package test.fp.tiendalab;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    // Emulador Android: 10.0.2.2 reenvía al localhost del ordenador.
    private static final String API = "http://10.0.2.2:3000";
    // Cadena señuelo detectable con MobSF; no es una credencial válida.
    private static final String DEMO_API_KEY = "DEMO_ONLY_NOT_A_SECRET_12345";
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private String token = "";
    private TextView output;
    private LinearLayout buttons;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        ScrollView scroll = new ScrollView(this);
        LinearLayout page = new LinearLayout(this); page.setPadding(28, 28, 28, 28); page.setOrientation(LinearLayout.VERTICAL);
        TextView title = new TextView(this); title.setText("🛍️ Tienda Lab\nCuenta ficticia: ana@aula.test\nClave: Aula-demo-2026!"); title.setTextSize(20); page.addView(title);
        output = new TextView(this); output.setText("API local: " + API + "\n" + DEMO_API_KEY + "\nPulsa un botón para empezar."); page.addView(output);
        buttons = new LinearLayout(this); buttons.setOrientation(LinearLayout.VERTICAL); page.addView(buttons);
        button("1. Crear cuenta e iniciar sesión", this::login);
        button("2. Ver catálogo", this::catalog);
        button("3. Comprar Taza Pixel (8,50 €)", () -> buy("mug-01", 850));
        button("4. Prueba de manipulación: enviar 0,01 €", () -> buy("mug-01", 1));
        scroll.addView(page); setContentView(scroll);
    }

    private void button(String text, Runnable action) {
        Button button = new Button(this); button.setText(text); buttons.addView(button, new ViewGroup.LayoutParams(-1, -2));
        button.setOnClickListener(v -> action.run());
    }
    private void show(String message) { runOnUiThread(() -> output.setText(message)); }
    private void login() { request("POST", "/api/auth/login", "{\"email\":\"ana@aula.test\",\"password\":\"Aula-demo-2026!\"}", true); }
    private void catalog() { request("GET", "/api/shop/products", null, false); }
    private void buy(String product, int price) { request("POST", "/api/shop/purchases", "{\"productId\":\"" + product + "\",\"priceCents\":" + price + "}", false); }

    private void request(String method, String path, String body, boolean register) {
        worker.execute(() -> {
            try {
                if (register) {
                    try { call("POST", "/api/auth/register", "{\"email\":\"ana@aula.test\",\"password\":\"Aula-demo-2026!\"}"); } catch (Exception ignored) { /* cuenta ya registrada */ }
                    JSONObject login = new JSONObject(call("POST", "/api/auth/login", "{\"email\":\"ana@aula.test\",\"password\":\"Aula-demo-2026!\"}"));
                    token = login.getString("token"); show("Sesión iniciada. Token guardado solo en memoria en esta versión.\n" + login);
                } else {
                    String result = call(method, path, body); show("Respuesta " + path + ":\n" + pretty(result));
                }
            } catch (Exception e) { show("No se pudo llamar a la API: " + e.getMessage() + "\nComprueba que la API está activa."); }
        });
    }
    private String call(String method, String path, String body) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(API + path).openConnection();
        connection.setRequestMethod(method); connection.setConnectTimeout(4000); connection.setReadTimeout(4000);
        connection.setRequestProperty("Content-Type", "application/json");
        if (!token.isEmpty()) connection.setRequestProperty("Authorization", "Bearer " + token);
        if (body != null) { connection.setDoOutput(true); try (OutputStream out = connection.getOutputStream()) { out.write(body.getBytes(StandardCharsets.UTF_8)); } }
        int status = connection.getResponseCode();
        java.io.InputStream stream = status < 400 ? connection.getInputStream() : connection.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
        StringBuilder result = new StringBuilder(); String line; while ((line = reader.readLine()) != null) result.append(line);
        connection.disconnect(); if (status >= 400) throw new Exception("HTTP " + status + " " + result);
        return result.toString();
    }
    private String pretty(String value) { try { return new JSONArray(value).toString(2); } catch (Exception ignored) { try { return new JSONObject(value).toString(2); } catch (Exception e) { return value; } } }
    @Override protected void onDestroy() { worker.shutdownNow(); super.onDestroy(); }
}
