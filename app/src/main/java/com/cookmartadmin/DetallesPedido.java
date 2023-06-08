package com.cookmartadmin;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cookmartadmin.adapters.adapterEditarDetalles;
import com.cookmartadmin.models.modeloEditarDetalles;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class DetallesPedido extends AppCompatActivity {

    String status;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView detallespedidoTxt;
    private TextView direccionCliente;
    private TextView numeroTlfTxt;
    private TextView IdPedido;
    private PedidosObject pedidosObject;
    private EditText mensajeCliente;
    private TextView productosPedidosTxt;
    private TextView estadoPedido;
    private ConstraintLayout detallesPedido;
    private ConstraintLayout detallesPedidoBtn, productosPedidoBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_pedido);

        try {
            recyclerView = findViewById(R.id.detalles_pedido_recycler);
            detallesPedidoBtn = findViewById(R.id.pedido_detalles_btn);
            progressBar = findViewById(R.id.detalles_pedido_progress_bar);
            TextView paymentMode = findViewById(R.id.pedido_detalles_modoPago);
            IdPedido = findViewById(R.id.pedido_detalles_pedidoId);
            mensajeCliente = findViewById(R.id.mensaje_cliente);
            productosPedidosTxt = findViewById(R.id.order_products_txt);
            detallesPedido = findViewById(R.id.detalles_pedido_layout);
            direccionCliente = findViewById(R.id.pedido_detalles_direccion);
            numeroTlfTxt = findViewById(R.id.pedidio_detalles_movil);
            TextView price = findViewById(R.id.pedidio_detalles_precio);
            estadoPedido = findViewById(R.id.pedido_detalles_estado);
            TextView date = findViewById(R.id.pedido_detalles_fecha);
            detallespedidoTxt = findViewById(R.id.orderedDetailsTxt);
            pedidosObject = (PedidosObject) getIntent().getSerializableExtra("PedidosObject");
            estadoPedido.setText(pedidosObject.getEstado());
            date.setText(pedidosObject.getFecha());
            price.setText(pedidosObject.getPrecioTotal().concat(Constantes.CURRENCY_SIGN));
            paymentMode.setText(pedidosObject.getTipoPago());
            IdPedido.setText("Id Pedido: #" + pedidosObject.getIndex());
            productosPedidoBtn = findViewById(R.id.pedido_productos_btn);
            ArrayList<modeloEditarDetalles> arrayList = new ArrayList<>();
            adapterEditarDetalles adapter = new adapterEditarDetalles(this, arrayList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter);
            productosPedidoBtn.setOnClickListener(v -> {
                recyclerView.setVisibility(View.VISIBLE);
                detallesPedido.setVisibility(View.INVISIBLE);
                productosPedidosTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
                detallespedidoTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
                detallesPedidoBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
                productosPedidoBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
            });
            detallesPedidoBtn.setOnClickListener(v -> {
                recyclerView.setVisibility(View.INVISIBLE);
                productosPedidosTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
                detallesPedido.setVisibility(View.VISIBLE);
                detallespedidoTxt.setTextColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
                detallesPedidoBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.apptheme));
                productosPedidoBtn.setBackgroundColor(ContextCompat.getColor(DetallesPedido.this, R.color.white));
            });
            HashMap<String, HashMap<String, String>> hashMap = pedidosObject.getProductos();
            try {
                for (int i = 0; i < hashMap.size(); i++) {
                    HashMap<String, String> child = hashMap.get(hashMap.keySet().toArray()[i]);
                    arrayList.add(new modeloEditarDetalles(child.get("imgMenor"), child.get("titulo"), child.get("unidad"), child.get("cantidad"), child.get("precio")));
                }
                adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        getAddress();
    }

    public void mandarMensajeCliente(View view) {
        if (!mensajeCliente.getText().toString().equals("")) {
            sendAndUploadNotification(mensajeCliente.getText().toString());
        } else {
            Toast.makeText(this, "El mensaje está en blanco", Toast.LENGTH_SHORT).show();
        }
    }

    private void sendAndUploadNotification(String body) {
        progressBar.setVisibility(View.VISIBLE);
        HashMap<String, String> hashMap = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy | HH:mm", Locale.getDefault());
        String currentDateandTime = sdf.format(new Date());
        hashMap.put("titulo", body);
        hashMap.put("fecha", currentDateandTime);
        FirebaseDatabase.getInstance().getReference("notificaciones").child(getIntent().getStringExtra("clienteUid"))
                .push().setValue(hashMap).addOnSuccessListener(aVoid -> {
            progressBar.setVisibility(View.INVISIBLE);
            try {
                FirebaseDatabase.getInstance().getReference("usuarios")
                        .child(getIntent().getStringExtra("clienteUid"))
                        .child("fcmToken")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                progressBar.setVisibility(View.VISIBLE);
                                String fcmToke = (String) snapshot.getValue();
                                JSONObject notificationObj = new JSONObject();
                                RequestQueue mRequestQue = Volley.newRequestQueue(DetallesPedido.this);
                                JSONObject json = new JSONObject();
                                try {
                                    json.put("to", fcmToke);
                                    notificationObj.put("titulo", "Actualizacion Pedido");
                                    notificationObj.put("body", body);
                                    //replace notification with data when went send data
                                    json.put("notificacion", notificationObj);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                String URL = "https://fcm.googleapis.com/fcm/send";
                                JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                                        json,
                                        response -> successMethod(),
                                        this::failMethod
                                ) {
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String, String> header = new HashMap<>();
                                        header.put("content-type", "application/json");
                                        header.put("authorization", "key="+ Constantes.FCM_SERVER_KEY);
                                        return header;
                                    }
                                };


                                mRequestQue.add(request);
                            }

                            private void failMethod(VolleyError error) {
                                progressBar.setVisibility(View.INVISIBLE);
                                if (error.networkResponse != null && error.networkResponse.data != null) {
                                    String errorResponse = new String(error.networkResponse.data);
                                    Log.e("VolleyError", "Error en Volley: " + errorResponse, error);
                                } else {
                                    Log.e("VolleyError", "Error en Volley", error);
                                }
                                Toast.makeText(DetallesPedido.this, "Ha habido un error.", Toast.LENGTH_SHORT).show();
                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("FirebaseError", "Error en Firebase", error.toException());
                            }
                        });

            } catch (Exception e) {
                Toast.makeText(DetallesPedido.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> Toast.makeText(DetallesPedido.this, e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void successMethod() {
        progressBar.setVisibility(View.INVISIBLE);
        mensajeCliente.setText("");
        Toast.makeText(DetallesPedido.this, "Enviado correctamente.", Toast.LENGTH_SHORT).show();
    }


    public void editarEstadoPedido(View view) {
        try {
            AlertDialog alertDialog;
            final AlertDialog.Builder builder = new AlertDialog.Builder(DetallesPedido.this);
            View vobj = LayoutInflater.from(DetallesPedido.this).inflate(R.layout.layout_edit_estado_pedido, null);
            builder.setView(vobj);
            alertDialog = builder.create();
            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();
            RadioButton radPreparando = vobj.findViewById(R.id.radioButton_editPreparandoBtn);
            RadioButton radEncamino = vobj.findViewById(R.id.radioButton_enCaminoBtn);
            RadioButton radEntregado = vobj.findViewById(R.id.radioButton_entregadoBtn);
            RadioButton radEnviado = vobj.findViewById(R.id.radioButton_enviadoBtn);
            if (estadoPedido.getText().toString().equals("Preparando") || estadoPedido.getText().toString().equals("preparando")) {
                radPreparando.setChecked(true);
            } else if (estadoPedido.getText().toString().equals("Enviado") || estadoPedido.getText().toString().equals("enviado")) {
                radEnviado.setChecked(true);
            } else if (estadoPedido.getText().toString().equals("Entregado") || estadoPedido.getText().toString().equals("entregado")) {
                radEntregado.setChecked(true);
            } else if (estadoPedido.getText().toString().equals("En Camino") || estadoPedido.getText().toString().equals("encamino")) {
                radEncamino.setChecked(true);
            }
//
            radEntregado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    radEnviado.setChecked(false);
                    radPreparando.setChecked(false);
                    radEntregado.setChecked(true);
                    status = "entregado";
                    radEncamino.setChecked(false);
                }

            });
            radEnviado.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    radEnviado.setChecked(true);
                    radPreparando.setChecked(false);
                    status = "enviado";
                    radEntregado.setChecked(false);
                    radEncamino.setChecked(false);
                }

            });
            radEncamino.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    radEnviado.setChecked(false);
                    radEntregado.setChecked(false);
                    radPreparando.setChecked(false);
                    radEncamino.setChecked(true);
                    status = "encamino";
                }

            });
            radPreparando.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    radEnviado.setChecked(false);
                    radEntregado.setChecked(false);
                    status = "preparando";
                    radPreparando.setChecked(true);
                    radEncamino.setChecked(false);
                }
            });

            String clienteUid = getIntent().getStringExtra("clienteUid");
            String idPedido = getIntent().getStringExtra("idPedido");

            Log.d("DEBUG", "clienteUid: " + clienteUid);
            Log.d("DEBUG", "idPedido: " + idPedido);

            CardView savebtn = vobj.findViewById(R.id.guardar_estadoPedido_btn);
            savebtn.setOnClickListener(v -> {
                FirebaseDatabase.getInstance().getReference("pedidos")
                        .child(getIntent().getStringExtra("clienteUid"))
                        .child(getIntent().getStringExtra("idPedido"))
                        .child("estado").setValue(status).addOnSuccessListener(aVoid -> {
                    alertDialog.cancel();
                    estadoPedido.setText(status);
                    sendAndUploadNotification("Sus productos con id: " + IdPedido.getText().toString() + " esta " + status);
                    Toast.makeText(DetallesPedido.this, "Cambiado con éxito", Toast.LENGTH_SHORT).show();
                });
            });

        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }


    private void getAddress() {
        FirebaseDatabase.getInstance().getReference("usuarios").child(getIntent().getStringExtra("clienteUid"))
                .child("deliveryAddress").child(pedidosObject.getIdDireccion()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    HashMap<String, String> hashMap = (HashMap<String, String>) snapshot.getValue();
                    String temp = hashMap.get("addressLine1").concat(", " + Objects.requireNonNull(hashMap.get("addressLine2")))
                            .concat(", " + hashMap.get("city")).concat(", " + Objects.requireNonNull(hashMap.get("codPostal")));
                    direccionCliente.setText(temp);
                    numeroTlfTxt.setText(hashMap.get("phoneNumber"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void cerrarDetallesPedido(View view) {
        startActivity(new Intent(DetallesPedido.this, MainActivity.class));
    }
}