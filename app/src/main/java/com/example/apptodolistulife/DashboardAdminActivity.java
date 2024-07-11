package com.example.apptodolistulife;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.apptodolistulife.databinding.ActivityDashboardAdminBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashboardAdminActivity extends AppCompatActivity {

    // visualização binding
    private ActivityDashboardAdminBinding binding;

    // firebase auth
    private FirebaseAuth firebaseAuth;

    // arraylist para tarefas
    private ArrayList<ModelTask> taskArrayList;

    // adapter
    private AdapterTask adapterTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // inicia firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();
        loadTasks();

        // logout
        binding.logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                checkUser();
            }
        });

        // inicia a tela de adicionar tarefas
        binding.addTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdminActivity.this, TaskAddActivity.class));
            }
        });
    }

    private void loadTasks() {
        // inicia arraylist
        taskArrayList = new ArrayList<>();

        // obtem todas as tarefas do firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // limpa arraylist antes de inserir o valor
                taskArrayList.clear();
                for (DataSnapshot ds: snapshot.getChildren()){
                    // obtem dados
                    ModelTask model = ds.getValue(ModelTask.class);

                    // adiciona ao arraylist
                    taskArrayList.add(model);
                }
                // monta adapter
                adapterTask = new AdapterTask(DashboardAdminActivity.this, taskArrayList);

                // insere o adapter ao recyclerview
                binding.tasksRv.setAdapter(adapterTask);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkUser() {
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser == null){
            // não possui login, vai para tela principal
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } else {
            String email = firebaseUser.getEmail();
            binding.subTitleTv.setText(email);
        }
    }
}