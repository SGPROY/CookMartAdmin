package com.cookmartadmin.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.R;
import com.cookmartadmin.adapters.adapterCodPostal;
import com.cookmartadmin.models.modeloCodPostal;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class CodPostalFragment extends Fragment {

    private ArrayList<modeloCodPostal> arrayList;
    private ProgressDialog progressDialog;
    private adapterCodPostal adapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_codpostal, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.codPostal_recycler_view);
        arrayList = new ArrayList<>();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Espere por favor..");
        progressDialog.setMessage("Estamos recibiendo todos los productos.");
        progressDialog.setCanceledOnTouchOutside(false);
        CardView addProductBtn = view.findViewById(R.id.add_codPostal_btn);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new adapterCodPostal(getContext(), arrayList);
        recyclerView.setAdapter(adapter);
        obtenerDatosCodPostal();
        addProductBtn.setOnClickListener(v -> {
            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                View vObj = LayoutInflater.from(getContext()).inflate(R.layout.layout_add_codpostal, null);
                builder.setView(vObj);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                EditText codPostal = vObj.findViewById(R.id.dialog_codPostal_add);
                EditText price = vObj.findViewById(R.id.dialog_cargo);
                CheckBox codeAvail = vObj.findViewById(R.id.dialog_efectivo_checkbox);
                TextView cancelBtn = vObj.findViewById(R.id.dialog_codPostal_cancel_btn);
                TextView saveBtn = vObj.findViewById(R.id.dialog_codPostal_save_btn);
                codeAvail.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        codeAvail.setText("Activo");
                    } else {
                        codeAvail.setText("No Activo");
                    }
                });
                saveBtn.setOnClickListener(v1 -> {
                    if (!codPostal.getText().toString().isEmpty() &&
                            !price.getText().toString().isEmpty()) {
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("cargoEnvio", price.getText().toString().replace("$",""));
                        if (codeAvail.isChecked()) {
                            hashMap.put("efectivoActivo", "true");
                        } else {
                            hashMap.put("efectivoActivo", "false");
                        }
                        FirebaseDatabase.getInstance().getReference("codpostal")
                                .child(codPostal.getText().toString())
                                .setValue(hashMap).addOnSuccessListener(aVoid -> {
                                    alertDialog.cancel();
                                    Toast.makeText(getContext(), "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                                    obtenerDatosCodPostal();
                                });
                    } else {
                        Toast.makeText(getContext(), "Los campos en blanco no se pueden procesar.", Toast.LENGTH_SHORT).show();
                    }

                });
                cancelBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();
                    }
                });
                alertDialog.show();
            } catch (Exception e) {
                Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        return view;
    }


    private void obtenerDatosCodPostal() {
        progressDialog.show();
        arrayList.clear();
        FirebaseDatabase.getInstance().getReference("codpostal").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, HashMap<String, String>> main = (HashMap<String, HashMap<String, String>>) snapshot.getValue();
                    HashMap<String, String> child;
                    for (int i = 0; i < main.size(); i++) {
                        child = main.get(main.keySet().toArray()[i]);
                        arrayList.add(new modeloCodPostal(main.keySet().toArray()[i] + "", child.get("efectivoActivo"), child.get("cargoEnvio")));
                    }
                    adapter.notifyDataSetChanged();
                    progressDialog.cancel();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.cancel();
                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}