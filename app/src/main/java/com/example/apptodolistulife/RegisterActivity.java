package com.example.apptodolistulife;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptodolistulife.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    // visualização Binding
    private ActivityRegisterBinding binding;

    // autenticação Firebase
    private FirebaseAuth firebaseAuth;

    // logs de Progressos
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        */
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // inicializa Firebase Auth
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


        // Inicia Registro
        binding.registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });

    }
    private String name = "", email = "", ra = "", password = "";
    private void validateData() {
        // após criar conta, validação de dados

        //  obtem dados
        name = binding.nameEt.getText().toString().trim();
        email = binding.emailEt.getText().toString().trim();
        ra = binding.raEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();
        String confPassword = binding.confPasswordEt.getText().toString().trim();

        // valida
        if (TextUtils.isEmpty(name)){
            Toast.makeText(this, "Insira seu nome completo!", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "E-mail inválido!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(ra)){
            Toast.makeText(this, "Insira seu RA", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Insira uma senha!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(confPassword)){
            Toast.makeText(this, "Confirme sua senha!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(confPassword)){
            Toast.makeText(this, "Senhas não iguais!", Toast.LENGTH_SHORT).show();
        } else {
            createUserAccount();
        }

    }

    private void createUserAccount() {
        // exibe progresso
        progressDialog.setMessage("Registrando Usuário...");
        progressDialog.show();

        // criar usuário no Firebase
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // sucesso
                        updateUserInfo();
                    }
                })

                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // falha
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUserInfo() {
        progressDialog.setMessage("Registrando Informações...");

        //tempo da execução
        long timestamp = System.currentTimeMillis();

        // obtem id usuário
        String uid = firebaseAuth.getUid();

        // obtem dados para adicionar no banco
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("ra", ra);
        hashMap.put("name", name);
        hashMap.put("profileImage", ""); //vazio por enquanto
        hashMap.put("userType", "user"); //possiveris valores são usuario e admin, faremos manualmente em database mudando esse valor
        hashMap.put("timestamp", timestamp);

        // insere dados no banco
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // dados inseridos com sucesso
                        Toast.makeText(RegisterActivity.this, "Conta Criada!", Toast.LENGTH_SHORT).show();

                        // inicia dashboard do user após criação do usuário
                        startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // dados não inseridos com sucesso
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}