package com.cookmartadmin.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.cookmartadmin.Constantes;
import com.cookmartadmin.DetallesPedido;
import com.cookmartadmin.PedidosObject;
import com.cookmartadmin.R;

import java.util.ArrayList;

public class adapterPedidos extends RecyclerView.Adapter<adapterPedidos.viewHolder> {

    private final Context context;
    private final ArrayList<PedidosObject> arrayList;
    private final ArrayList<String> idPedido;
    private final ArrayList<String> idCliente;

    public adapterPedidos(ArrayList<String> idPedido, Context context, ArrayList<PedidosObject> arrayList, ArrayList<String> idCliente) {
        this.context = context;
        this.idCliente = idCliente;
        this.idPedido = idPedido;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new viewHolder(LayoutInflater.from(context).inflate(R.layout.layout_pedidos, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        try {
            String estadoStr = arrayList.get(position).getEstado().replaceFirst(arrayList.get(position).getEstado().charAt(0) + "", (arrayList.get(position).getEstado().charAt(0) + "").toUpperCase());
            holder.estado.setText(estadoStr);
            holder.constraintLayout.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetallesPedido.class);
                intent.putExtra("PedidosObject", arrayList.get(position));
                intent.putExtra("clienteUid", idCliente.get(position));
                intent.putExtra("idPedido", idPedido.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
            if (arrayList.get(position).getTipoPago().equals("efectivo")) {
                holder.tipoPago.setText(context.getString(R.string.PAGOEFECTIVO));
            } else {
                holder.tipoPago.setText(context.getString(R.string.PAGOONLINE));
            }
            holder.precio.setText(arrayList.get(position).getPrecioTotal().concat(Constantes.CURRENCY_SIGN));
            holder.fecha.setText(arrayList.get(position).getFecha());
            holder.idPedido.setText("Id pedido: #" + arrayList.get(position).getIndex());
        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class viewHolder extends RecyclerView.ViewHolder {
        private final TextView idPedido;
        private final TextView fecha;
        private final TextView precio;
        private final TextView estado;
        private final TextView tipoPago;
        private final ConstraintLayout constraintLayout;

        public viewHolder(@NonNull View itemView) {
            super(itemView);
            idPedido = itemView.findViewById(R.id.layout_pedidoId);
            fecha = itemView.findViewById(R.id.layout_pedido_fecha);
            estado = itemView.findViewById(R.id.layout_estado_pedido);
            precio = itemView.findViewById(R.id.layout_pedido_precio);
            tipoPago = itemView.findViewById(R.id.layout_pedido_tipoPago);
            constraintLayout = itemView.findViewById(R.id.pedidosMainContainer);
        }
    }
}
