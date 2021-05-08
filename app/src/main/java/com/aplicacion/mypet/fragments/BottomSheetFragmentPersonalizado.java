package com.aplicacion.mypet.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aplicacion.mypet.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class BottomSheetFragmentPersonalizado extends BottomSheetDialogFragment {
    private  BottomSheetListener listener;
    private final int GALLERY_REQUEST_CODE = 1;

    private LinearLayout camara;
    private LinearLayout galeria;
    private LinearLayout borrar;

    public BottomSheetFragmentPersonalizado() {
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.menu_camara_galeria,container,false);

        camara = view.findViewById(R.id.boton_elegir_camara);
        camara.setOnClickListener(new ClickListener());
        galeria = view.findViewById(R.id.boton_elegir_galeria);
        galeria.setOnClickListener(new ClickListener());
        borrar = view.findViewById(R.id.boton_borrar_imagen);
        borrar.setOnClickListener(new ClickListener());
        return  view;
    }

    public interface BottomSheetListener {
        void  onButtonClicked(int numero);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (BottomSheetListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " tienes que implementar BottomSheetListener");
        }
    }

    private class ClickListener implements View.OnClickListener {


        @Override
        public void onClick(View v) {
            LinearLayout aux = (LinearLayout) v;

            if (aux.getId()== R.id.boton_elegir_camara) {
                listener.onButtonClicked(0);
                dismiss();
            } else if(aux.getId()== R.id.boton_elegir_galeria) {
                listener.onButtonClicked(1);
                dismiss();
            } else if(aux.getId()== R.id.boton_borrar_imagen) {
                listener.onButtonClicked(2);
                dismiss();
            }

        }
    }
}
