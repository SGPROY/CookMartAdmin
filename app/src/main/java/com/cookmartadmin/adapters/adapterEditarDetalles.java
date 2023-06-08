package com.cookmartadmin.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.Constantes;
import com.cookmartadmin.R;
import com.cookmartadmin.models.modeloEditarDetalles;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterEditarDetalles extends RecyclerView.Adapter<adapterEditarDetalles.viewHolder> {

    private final Context context;
    private final ArrayList<modeloEditarDetalles> arrayList;

    public adapterEditarDetalles(Context context, ArrayList<modeloEditarDetalles> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_detalles_productos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        try {
            double precio = Double.parseDouble(arrayList.get(position).getPrice());
            double cantidad = Double.parseDouble(arrayList.get(position).getCantidad());
            double total = precio * cantidad;
            String totalConMoneda = total + String.valueOf(Constantes.CURRENCY_SIGN);

            holder.titulo.setText(arrayList.get(position).getTitulo());
            holder.unidad.setText(arrayList.get(position).getUnidad().replace("1", arrayList.get(position).getCantidad()));
            holder.precio.setText(totalConMoneda);

            Picasso.get().load(arrayList.get(position).getImagen()).into(holder.imagen);
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final ImageView imagen;
        private final TextView titulo;
        private final TextView unidad;
        private final TextView precio;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.layout_editDetalles_imagen);
            titulo = itemView.findViewById(R.id.layout_editDetails_title);
            unidad = itemView.findViewById(R.id.layout_editDetalles_unidad);
            precio = itemView.findViewById(R.id.layout_editDetalles_precio);
        }
    }
}
