package com.cookmartadmin.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.AddProducto;
import com.cookmartadmin.R;
import com.cookmartadmin.adapters.adapterProductos;
import com.cookmartadmin.models.modeloProductos;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class ProductosFragment extends Fragment {

    private ProgressDialog progressDialog;
    private adapterProductos adapterReposteria, adapterFrutas, adapterAceites, adapterCarnes, adapterBebidas, adapterEnlatados;
    private ArrayList<modeloProductos> arrayListFrutas, arrayListCarnes, arrayListReposteria, arrayListBebidas, arrayListAceites, arrayEnlatados;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_productos, container, false);
        RecyclerView recyclerViewFruits = view.findViewById(R.id.productos_recycler_frutas_verduras);
        RecyclerView recyclerViewBakery = view.findViewById(R.id.productos_recycler_panaderia_reposteria);
        RecyclerView recyclerViewBeverages = view.findViewById(R.id.productos_recycler_bebidas);
        RecyclerView recyclerViewMeats = view.findViewById(R.id.productos_recycler_carnes_pescados);
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Espere por favor..");
        progressDialog.setMessage("Estamos recibiendo todos los productos.");
        progressDialog.setCanceledOnTouchOutside(false);
        RecyclerView recyclerViewOils = view.findViewById(R.id.productos_recycler_aceites_vinagres);
        RecyclerView recyclerViewDairy = view.findViewById(R.id.product_recycler_dairy_products);

        ImageView addPanaderia = view.findViewById(R.id.panaderia_reposteria_add_btn);
        ImageView addAceites = view.findViewById(R.id.aceites_vinagres_add_btn);
        ImageView addCarnes = view.findViewById(R.id.carnes_pescados_add_btn);
        ImageView addBebidas = view.findViewById(R.id.bebidas_add_btn);
        ImageView addFrutasVerduras = view.findViewById(R.id.frutas_verduras_add_btn);
        ImageView addEnlatados = view.findViewById(R.id.productos_enlatados_envasados_add_btn);



        addFrutasVerduras.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProducto.class);
            intent.putExtra("categoria", "frutasVerduras");
            startActivity(intent);
        });

        addBebidas.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProducto.class);
            intent.putExtra("categoria", "bebidas");
            startActivity(intent);
        });

        addPanaderia.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProducto.class);
            intent.putExtra("categoria", "reposteria");
            startActivity(intent);
        });

        addAceites.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProducto.class);
            intent.putExtra("categoria", "aceitesyvinagres");
            startActivity(intent);
        });

        addCarnes.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProducto.class);
            intent.putExtra("categoria", "carnesypescados");
            startActivity(intent);
        });

        addEnlatados.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProducto.class);
            intent.putExtra("categoria", "envasados");
            startActivity(intent);
        });


        arrayListFrutas = new ArrayList<>();
        arrayListReposteria = new ArrayList<>();
        arrayListCarnes = new ArrayList<>();
        arrayListBebidas = new ArrayList<>();
        arrayListAceites = new ArrayList<>();
        arrayEnlatados = new ArrayList<>();


        recyclerViewFruits.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewOils.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBakery.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewMeats.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDairy.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewBeverages.setLayoutManager(new LinearLayoutManager(getContext()));

        adapterReposteria = new adapterProductos(getContext(), arrayListReposteria);
        adapterFrutas = new adapterProductos(getContext(), arrayListFrutas);
        adapterBebidas = new adapterProductos(getContext(), arrayListBebidas);
        adapterAceites = new adapterProductos(getContext(), arrayListAceites);
        adapterCarnes = new adapterProductos(getContext(), arrayListCarnes);
        adapterEnlatados = new adapterProductos(getContext(), arrayEnlatados);
        recyclerViewFruits.setAdapter(adapterFrutas);
        recyclerViewBakery.setAdapter(adapterReposteria);
        recyclerViewOils.setAdapter(adapterAceites);
        recyclerViewMeats.setAdapter(adapterCarnes);
        recyclerViewBeverages.setAdapter(adapterBebidas);
        recyclerViewDairy.setAdapter(adapterEnlatados);
        getProductosDb();
        return view;
    }


    private void getProductosDb() {
        progressDialog.show();
        FirebaseDatabase.getInstance().getReference("productos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    HashMap<String, HashMap<String, String>> hashMap = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    HashMap<String, String> child;
                    for (int i = 0; i < hashMap.size(); i++) {
                        child = hashMap.get(hashMap.keySet().toArray()[i]);
                        if (child.get("categoria").equals("frutasVerduras")) {
                            arrayListFrutas.add(new modeloProductos(child.get("categoria"), child.get("imgMenor"), child.get("imgMayor"), child.get("titulo"), child.get("descripcion"), child.get("precio"), child.get("unidad"), hashMap.keySet().toArray()[i] + ""));
                        } else if (child.get("categoria").equals("reposteria")) {
                            arrayListReposteria.add(new modeloProductos(child.get("categoria"), child.get("imgMenor"), child.get("imgMayor"), child.get("titulo"), child.get("descripcion"), child.get("precio"), child.get("unidad"), hashMap.keySet().toArray()[i] + ""));
                        } else if (child.get("categoria").equals("aceitesyvinagres")) {
                            arrayListAceites.add(new modeloProductos(child.get("categoria"), child.get("imgMenor"), child.get("imgMayor"), child.get("titulo"), child.get("descripcion"), child.get("precio"), child.get("unidad"), hashMap.keySet().toArray()[i] + ""));
                        } else if (child.get("categoria").equals("envasados")) {
                            arrayEnlatados.add(new modeloProductos(child.get("categoria"), child.get("imgMenor"), child.get("imgMayor"), child.get("titulo"), child.get("descripcion"), child.get("precio"), child.get("unidad"), hashMap.keySet().toArray()[i] + ""));
                        } else if (child.get("categoria").equals("carnesypescados")) {
                            arrayListCarnes.add(new modeloProductos(child.get("categoria"), child.get("imgMenor"), child.get("imgMayor"), child.get("titulo"), child.get("descripcion"), child.get("precio"), child.get("unidad"), hashMap.keySet().toArray()[i] + ""));
                        } else if (child.get("categoria").equals("bebidas")) {
                            arrayListBebidas.add(new modeloProductos(child.get("categoria"), child.get("imgMenor"), child.get("imgMayor"), child.get("titulo"), child.get("descripcion"), child.get("precio"), child.get("unidad"), hashMap.keySet().toArray()[i] + ""));
                        }
                        adapterReposteria.notifyDataSetChanged();
                        adapterBebidas.notifyDataSetChanged();
                        adapterEnlatados.notifyDataSetChanged();
                        adapterFrutas.notifyDataSetChanged();
                        adapterAceites.notifyDataSetChanged();
                        adapterCarnes.notifyDataSetChanged();
                        progressDialog.cancel();

                    }
                } catch (Exception exception) {
                    Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}