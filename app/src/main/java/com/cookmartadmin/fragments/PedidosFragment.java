package com.cookmartadmin.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.PedidosObject;
import com.cookmartadmin.R;
import com.cookmartadmin.adapters.adapterPedidos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PedidosFragment extends Fragment {

    private RecyclerView recyclerView;
    private ConstraintLayout enviadoBtn, todosBtn, enCaminoBtn, preparandoBtn, entregadoBtn;
    private ArrayList<PedidosObject> arrayList;
    private adapterPedidos adapter;
    private ArrayList<String> clienteUid;
    private ProgressBar progressBar;
    private ArrayList<String> idPedido;
    private LinearLayout linearLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos, container, false);
        progressBar = view.findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.VISIBLE);
        arrayList = new ArrayList<>();
        idPedido = new ArrayList<>();
        clienteUid = new ArrayList<>();
        linearLayout = view.findViewById(R.id.fragment_estado_pedidos_layout);
        recyclerView = view.findViewById(R.id.ordersRecycler);
        adapter = new adapterPedidos(idPedido, getContext(), arrayList, clienteUid);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        getMisPedidos("all");

        preparandoBtn = view.findViewById(R.id.pedidosPreparandoBtn);
        enviadoBtn = view.findViewById(R.id.pedidoEnviadoBtn);
        enCaminoBtn = view.findViewById(R.id.pedidoEnCaminoBtn);
        entregadoBtn = view.findViewById(R.id.pedidoEntregadoBtn);
        todosBtn = view.findViewById(R.id.todosPedidosBtn);

        todosBtn.setOnClickListener(v -> {
            getMisPedidos("all");
            todosBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.apptheme));
            preparandoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enviadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            entregadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enCaminoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));

        });
        enCaminoBtn.setOnClickListener(v -> {
            getMisPedidos("enCamino");
            enCaminoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparandoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enviadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            entregadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            todosBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        entregadoBtn.setOnClickListener(v -> {
            getMisPedidos("entregado");
            entregadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparandoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enviadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            todosBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enCaminoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        enviadoBtn.setOnClickListener(v -> {
            getMisPedidos("enviado");
            enviadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            preparandoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            todosBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            entregadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enCaminoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        preparandoBtn.setOnClickListener(v -> {
            getMisPedidos("preparando");
            preparandoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.apptheme));
            todosBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enviadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            entregadoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
            enCaminoBtn.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.gray_eee));
        });
        return view;
    }

    private void getMisPedidos(String status) {
        arrayList.clear();
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
        FirebaseDatabase.getInstance().getReference("pedidos")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        arrayList.clear();
                        clienteUid.clear();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            for (DataSnapshot objectOrders : dataSnapshot.getChildren()) {
                                PedidosObject ordersObject = objectOrders.getValue(PedidosObject.class);
                                if (status.equals("all")) {
                                    arrayList.add(ordersObject);
                                    idPedido.add(objectOrders.getKey());
                                    clienteUid.add(dataSnapshot.getKey());
                                } else {
                                    if (ordersObject.getEstado().equals(status)) {
                                        arrayList.add(ordersObject);
                                        clienteUid.add(dataSnapshot.getKey());
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        if (arrayList.size() == 0) {
                            linearLayout.setVisibility(View.VISIBLE);
                        } else {
                            linearLayout.setVisibility(View.INVISIBLE);
                        }
                        recyclerView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}