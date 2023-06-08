package com.cookmartadmin.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookmartadmin.Constantes;
import com.cookmartadmin.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class HomeFragment extends Fragment {

    private TextView sobreNosotros, pedidosTotales, clientesTotales, gananciasTotales, productosTotales;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sobreNosotros = view.findViewById(R.id.home_sobrenosotros);
        pedidosTotales = view.findViewById(R.id.frag_home_pedidos_totales);
        clientesTotales = view.findViewById(R.id.frag_home_clientes_totales);
        gananciasTotales = view.findViewById(R.id.frag_home_ganacias_totales);
        productosTotales = view.findViewById(R.id.frag_home_productos_totales);
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> {
            // do UI changes before background work here
            Executor executor = Executors.newSingleThreadExecutor();
            executor.execute(() -> {
                // do background work here
                try {
                    getInformacionTienda();
                    buscarNUsuarios();
                    buscarProductos();
                    buscarVentasIngresos();
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                handler.post(() -> {
                    // do UI changes after background work here
                });
            });
        });
        return view;
    }


    private void getInformacionTienda() {
        FirebaseDatabase
                .getInstance().getReference("infoTienda").child("informacion")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String aboutStr = (String) snapshot.getValue();
                        sobreNosotros.setText(aboutStr);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void buscarVentasIngresos() {
        FirebaseDatabase.getInstance().getReference("pedidos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalOrdersInt = 0, totalEarningInt = 0;
                try {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        totalOrdersInt +=  dataSnapshot.getChildrenCount();
                        for (DataSnapshot objectOrders : dataSnapshot.getChildren()) {
                            HashMap<String, String> hashMap = (HashMap<String, String>) objectOrders.getValue();
                            totalEarningInt += Double.parseDouble(hashMap.get("precioTotal"));
                        }
                        gananciasTotales.setText(String.valueOf(totalEarningInt).concat(Constantes.CURRENCY_SIGN));
                    }
                    pedidosTotales.setText(String.valueOf(totalOrdersInt));
                } catch (Exception e) {
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscarProductos() {
        FirebaseDatabase.getInstance().getReference("productos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    assert hashMap != null;
                    productosTotales.setText(String.valueOf(hashMap.size()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void buscarNUsuarios() {
        FirebaseDatabase.getInstance().getReference("usuarios")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            if (snapshot.exists()) {
                                HashMap<String, HashMap<String, String>> hashMapHashMap = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                                assert hashMapHashMap != null;
                                clientesTotales.setText(String.valueOf(hashMapHashMap.size()));
                            }
                        } catch (Exception ignored) {
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}