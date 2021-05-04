package com.aplicacion.mypet.activities.publicar;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.aplicacion.mypet.R;

public class ActivityCrearPublicacion extends AppCompatActivity {
    private Button boton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_publicacion);

        boton = findViewById(R.id.boton_cambiar_tipo);
    }

    public void cancelarPublicacion(View v) {
        finish();
    }

    public void cambiarTipoAnimal(View view) {
        AlertDialog dialogo;
        String[] array = getResources().getStringArray(R.array.lista_animales);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.tipo_animal))
                .setSingleChoiceItems(array,0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boton.setText(array[which]);
                        boton.setTextColor(getResources().getColor(R.color.black));
                        dialog.cancel();
                    }
                });

        dialogo = builder.create();
        dialogo.show();
    }
}