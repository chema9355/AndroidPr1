package es.upm.miw.ficheros;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.internal.view.menu.MenuBuilder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;

public class FicherosActivity extends AppCompatActivity {

    private String NOMBRE_FICHERO = "miFichero.txt";
    private String RUTA_FICHERO;         /** SD card **/
    EditText lineaTexto;
    Button botonAniadir;
    TextView contenidoFichero;
    TextView estado;
    boolean sd;
    private static final int MENUITEM = Menu.FIRST;
    @Override
    protected void onStart() {
        super.onStart();
        mostrarContenido(contenidoFichero);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ficheros);

        lineaTexto       = (EditText) findViewById(R.id.textoIntroducido);
        botonAniadir     = (Button)   findViewById(R.id.botonAniadir);
        contenidoFichero = (TextView) findViewById(R.id.contenidoFichero);
        RUTA_FICHERO = getExternalFilesDir(null) + "/" + NOMBRE_FICHERO;
        estado = new TextView(this);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        sd = pref.getBoolean("SD", true);
        NOMBRE_FICHERO = pref.getString("Fichero", "");
        mostrarContenido(contenidoFichero);

    }

         public void accionAniadir(View v) {

        if(sd){
        String estadoTarjetaSD = Environment.getExternalStorageState();
        try {
            if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                FileOutputStream fos = new FileOutputStream(RUTA_FICHERO, true);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                mostrarContenido(contenidoFichero);
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }}
        else
        {
            try {
                FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_APPEND);
                fos.write(lineaTexto.getText().toString().getBytes());
                fos.write('\n');
                fos.close();
                mostrarContenido(contenidoFichero);
                Log.i("FICHERO", "Click botón Añadir -> AÑADIR al fichero");
            } catch (Exception e) {
                Log.e("FILE I/O", "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

  }

    public void mostrarContenido(View textviewContenidoFichero) {
        boolean hayContenido = false;
        if(sd)
        {
        File fichero = new File(RUTA_FICHERO);
        String estadoTarjetaSD = Environment.getExternalStorageState();
        contenidoFichero.setText("");
        try {
            if (fichero.exists() &&
                    estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) {
                BufferedReader fin = new BufferedReader(new FileReader(new File(RUTA_FICHERO)));
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            }
        } catch (Exception e) {
            Log.e("FILE I/O", "ERROR: " + e.getMessage());
            e.printStackTrace();
        }

        }
    else {
            try {
                BufferedReader fin =
                        new BufferedReader(new InputStreamReader(openFileInput(NOMBRE_FICHERO)));
                contenidoFichero.setText("");
                String linea = fin.readLine();
                while (linea != null) {
                    hayContenido = true;
                    contenidoFichero.append(linea + '\n');
                    linea = fin.readLine();
                }
                fin.close();
                Log.i("FICHERO", "Click contenido Fichero -> MOSTRAR fichero");
            } catch (Exception e) {
                Log.e("FILE I/O", "ERROR: " + e.getMessage());
                e.printStackTrace();
            }

        }

        if (!hayContenido) {
            Toast.makeText(this, getString(R.string.txtFicheroVacio), Toast.LENGTH_SHORT).show();
        }
     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        menu.findItem(R.id.sd).setVisible(sd);
        menu.findItem(R.id.fichero).setVisible(!sd);
        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // case 1:
            case R.id.accionVaciar:
                borrarContenido();
                break;
            case R.id.preferencias:
                Intent nuevoIntent = new Intent(FicherosActivity.this,Configuracion.class);
                startActivity(nuevoIntent);
            case R.id.borrar:
                eliminarFicheros();
                break;
        }
        return true;
    }

    public void borrarContenido() {
        if(sd) {

            String estadoTarjetaSD = Environment.getExternalStorageState();
            try {
                if (estadoTarjetaSD.equals(Environment.MEDIA_MOUNTED)) { /** SD card **/
                    // FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                    FileOutputStream fos = new FileOutputStream(RUTA_FICHERO);
                    fos.close();
                    Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                    lineaTexto.setText(""); // limpio la linea de edición
                    mostrarContenido(contenidoFichero);
                }
            } catch (Exception e) {
                Log.e("FILE I/O", "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        else {
            try {
                FileOutputStream fos = openFileOutput(NOMBRE_FICHERO, Context.MODE_PRIVATE);
                fos.close();
                Log.i("FICHERO", "opción Limpiar -> VACIAR el fichero");
                lineaTexto.setText(""); // limpio la linea de edición
                mostrarContenido(contenidoFichero);
            } catch (Exception e) {
                Log.e("FILE I/O", "ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }

        }

    public void eliminarFicheros(){
        String path;
        if(sd){
            path = getExternalFilesDir(null)+"";
        }
        else{
            path = "/data/data/es.upm.miw.ficheros/files";
        }

        File[] archivos = new File(path).listFiles(new FileFilter() {
            public boolean accept(File archivo) {
                if (archivo.isFile())
                return archivo.getName().endsWith('.' + "txt");
                return false;
            }
        });
        for (File archivo : archivos)
        archivo.delete();


        if(sd){
        Toast.makeText(this,"Fichero eliminados de la tarjeta sd", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this,"Fichero eliminados de la memoria interna", Toast.LENGTH_SHORT).show();

        }

        contenidoFichero.setText("");

    }



}
