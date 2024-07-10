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

import com.example.apptodolistulife.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    // visualização Binding
    private ActivityLoginBinding binding;

    // autenticação Firebase
    private FirebaseAuth firebaseAuth;

    // logs de Progressos
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // inicia firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        // progresso Logs
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Por favor, aguarde!");
        progressDialog.setCanceledOnTouchOutside(false);


        // direciona para tela de registro
        binding.noAccountTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
            }
        });

        // Logando
        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateData();
            }
        });
    }

    private String email = "", password = "";

    private void validateData() {
        // após criar conta, validação de dados

        email = binding.emailEt.getText().toString().trim();
        password = binding.passwordEt.getText().toString().trim();

        // validação
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "E-mail inválido!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password)){
            Toast.makeText(this, "Insira uma senha!", Toast.LENGTH_SHORT).show();
        } else {
            // login
            loginUser();
        }
    }

    private void loginUser() {
        // exibe progresso
        progressDialog.setMessage("Acessando...");
        progressDialog.show();

        // login
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // sucesso e verifica se é usuario ou admin
                        checkUser();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        // falha
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkUser() {
        progressDialog.setMessage("Verificando Usuário...");
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        // verifica no banco
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(firebaseUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        progressDialog.dismiss();
                        // pega o tipo de user
                        String userType = ""+snapshot.child("userType").getValue();

                        // verifica o tipo
                        if (userType.equals("user")){
                            // inicia o dash de user
                            startActivity(new Intent(LoginActivity.this, DashboardUserActivity.class));
                            finish();

                        } else if (userType.equals("admin")){
                            // inicia o dash de admin
                            startActivity(new Intent(LoginActivity.this, DashboardAdminActivity.class));
                            finish();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {

                    }
                });
    }
}