package com.cookmartadmin.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.Constantes;
import com.cookmartadmin.R;
import com.cookmartadmin.models.modeloCodPostal;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class adapterCodPostal extends RecyclerView.Adapter<adapterCodPostal.viewHolder> {

    private final Context context;
    private final ArrayList<modeloCodPostal> arrayList;

    public adapterCodPostal(Context context, ArrayList<modeloCodPostal> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_codpostal_disponible, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Espere por favor..");
        progressDialog.setMessage("Estamos obteniendo todas direcciones.");
        progressDialog.setCanceledOnTouchOutside(false);
        holder.codPostal.setText(arrayList.get(position).getcodPostal().concat(""));
        holder.cargo.setText(arrayList.get(position).getcargoEnvio().concat(Constantes.CURRENCY_SIGN));
        holder.btnEditar.setOnClickListener(v -> {
            try {
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View vobj = LayoutInflater.from(context).inflate(R.layout.layout_add_codpostal, null);
                builder.setView(vobj);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                EditText codPostalDialog = vobj.findViewById(R.id.dialog_codPostal_add);
                EditText priceDialog = vobj.findViewById(R.id.dialog_cargo);
                TextView cancelBtn = vobj.findViewById(R.id.dialog_codPostal_cancel_btn);
                TextView saveBtn = vobj.findViewById(R.id.dialog_codPostal_save_btn);
                CheckBox codeAvail = vobj.findViewById(R.id.dialog_efectivo_checkbox);
                codPostalDialog.setText(arrayList.get(position).getcodPostal());
                priceDialog.setText(arrayList.get(position).getcargoEnvio());
                if (arrayList.get(position).getefectivoActivo().equals("true")) {
                    codeAvail.setChecked(true);
                }
                codeAvail.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        codeAvail.setText(context.getString(R.string.availbale));
                    } else {
                        codeAvail.setText(context.getString(R.string.NOT_AVAILBLE));
                    }
                });
                saveBtn.setOnClickListener(v1 -> {
                    if (!codPostalDialog.getText().toString().isEmpty() &&
                            !priceDialog.getText().toString().isEmpty()) {
                        progressDialog.show();
                        HashMap<String, String> hashMap = new HashMap<>();
                        hashMap.put("cargoEnvio", priceDialog.getText().toString());
                        if (codeAvail.isChecked()) {
                            hashMap.put("efectivoActivo", "true");
                        } else {
                            hashMap.put("efectivoActivo", "false");
                        }
                        FirebaseDatabase.getInstance().getReference("codpostal")
                                .child(codPostalDialog.getText().toString())
                                .setValue(hashMap).addOnSuccessListener(aVoid -> {
                                    alertDialog.cancel();
                                    progressDialog.cancel();
                                    notifyDataSetChanged();
                                    Toast.makeText(context, "Guardado exitosamente", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(context, "Los campos en blanco no se pueden procesar", Toast.LENGTH_SHORT).show();
                    }

                });
                cancelBtn.setOnClickListener(v12 -> alertDialog.cancel());
                alertDialog.show();
            } catch (Exception exception) {
                Toast.makeText(context, exception.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });
        if (arrayList.get(position).getefectivoActivo().equals("true")) {
            holder.efectivoActivo.setChecked(true);
        }
        holder.efectivoActivo.setClickable(false);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView codPostal;
        private final TextView cargo;
        private final CardView btnEditar;
        private final CheckBox efectivoActivo;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            codPostal = itemView.findViewById(R.id.layout_codPostal);
            cargo = itemView.findViewById(R.id.layout_cargo);
            btnEditar = itemView.findViewById(R.id.layout_codPostal_edit_btn);
            efectivoActivo = itemView.findViewById(R.id.layout_efectivo_checkbox);
        }
    }
}
