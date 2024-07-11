package com.example.apptodolistulife;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptodolistulife.databinding.ActivityTaskAddBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class TaskAddActivity extends AppCompatActivity {
    // visualização binding
    private ActivityTaskAddBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // progresso logs
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTaskAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // inicia firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // progresso logs
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor, aguarde!");
        progressDialog.setCanceledOnTouchOutside(false);

        // retorna
        binding.backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getOnBackPressedDispatcher();
            }
        });


        // inicia registro
        binding.submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String tarefa = "";

    private void validateData() {
        // antes de adicionar os dados validos da tarefa

        tarefa = binding.taskEt.getText().toString().trim();

        if (TextUtils.isEmpty(tarefa)){
            Toast.makeText(this, "Insira uma tarefa...", Toast.LENGTH_SHORT).show();
        } else {
            addTaskFirebase();
        }
    }

    private void addTaskFirebase() {
        // exibe progresso
        progressDialog.setMessage("Adicionando Tarefa...");
        progressDialog.show();

        //tempo da execução
        long timestamp = System.currentTimeMillis();

        // obtem dados para adicionar no banco
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("id", ""+timestamp);
        hashMap.put("task", ""+tarefa);
        hashMap.put("timestamp", timestamp);
        hashMap.put("uid", ""+firebaseAuth.getUid());

        // insere dados
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");
        ref.child(""+timestamp)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // dados inseridos com sucesso
                        progressDialog.dismiss();
                        Toast.makeText(TaskAddActivity.this, "Tarefa Inserida!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // dados não inseridos com sucesso
                        progressDialog.dismiss();
                        Toast.makeText(TaskAddActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }
}