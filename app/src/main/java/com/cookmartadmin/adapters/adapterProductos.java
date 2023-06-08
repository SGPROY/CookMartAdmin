package com.cookmartadmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.AddProducto;
import com.cookmartadmin.Constantes;
import com.cookmartadmin.R;
import com.cookmartadmin.models.modeloProductos;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class adapterProductos extends RecyclerView.Adapter<adapterProductos.viewHolder> {

    private Context context;
    private ArrayList<modeloProductos> arrayList;

    public adapterProductos(Context context, ArrayList<modeloProductos> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_productos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        holder.tituloCarro.setText(arrayList.get(position).getTitulo());
        holder.precioCarro.setText(arrayList.get(position).getPrecio().concat(Constantes.CURRENCY_SIGN));
        holder.precioUnidad.setText("Precio por 1 " + arrayList.get(position).getUnidad());
        holder.btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddProducto.class);
                intent.putExtra("idProducto", arrayList.get(position).getIdProducto());
                intent.putExtra("titulo", arrayList.get(position).getTitulo());
                intent.putExtra("descripcion", arrayList.get(position).getDescripcion());
                intent.putExtra("imgMenor", arrayList.get(position).getImgMenor());
                intent.putExtra("imgMayor", arrayList.get(position).getImgMayor());
                intent.putExtra("categoria", arrayList.get(position).getCategoria());
                intent.putExtra("precio", arrayList.get(position).getPrecio());
                intent.putExtra("unidad", arrayList.get(position).getUnidad());
                context.startActivity(intent);
            }
        });
        Picasso.get().load(arrayList.get(position).getImgMenor()).into(holder.imagen);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private ImageView imagen;
        private final TextView tituloCarro;
        private final TextView precioUnidad;
        private final TextView precioCarro;
        private final ImageView btnEditar;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            imagen = itemView.findViewById(R.id.imagen_carro);
            tituloCarro = itemView.findViewById(R.id.carro_title);
            precioUnidad = itemView.findViewById(R.id.carro_precio_unidad);
            precioCarro = itemView.findViewById(R.id.carro_item_precio);
            btnEditar = itemView.findViewById(R.id.carro_edit_button);
        }
    }
}
