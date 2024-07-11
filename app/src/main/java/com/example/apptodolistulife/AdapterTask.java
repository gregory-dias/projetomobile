package com.example.apptodolistulife;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.apptodolistulife.databinding.RowTaskBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdapterTask extends RecyclerView.Adapter<AdapterTask.HolderTask>{
    private Context context;
    private ArrayList<ModelTask> taskArrayList;

    // visão binding
    private RowTaskBinding binding;

    public AdapterTask(Context context, ArrayList<ModelTask> taskArrayList) {
        this.context = context;
        this.taskArrayList = taskArrayList;
    }

    @NonNull
    @Override
    public HolderTask onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // row_task.xml
        binding = RowTaskBinding.inflate(LayoutInflater.from(context), parent, false);

        return new HolderTask(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull HolderTask holder, int position) {
        // obtem dados
        ModelTask model = taskArrayList.get(position);
        String id = model.getId();
        String task = model.getTask();
        String uid = model.getUid();
        long timestamp = model.getTimestamp();

        // insere dados
        holder.taskTv.setText(task);

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // confirma a exclusão da tarefa
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Excluir")
                                .setMessage("Tem certeza que deseja excluir essa tarefa?")
                                    .setPositiveButton("Excluir", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(context, "Excluíndo...", Toast.LENGTH_SHORT).show();
                                            deleteTask(model, holder);
                                        }
                                    })
                                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .show();
            }
        });

    }

    private void deleteTask(ModelTask model, HolderTask holder) {
        String id = model.getId();

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Tasks");
        ref.child(id)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        // sucesso
                        Toast.makeText(context, "Tarefa Excluída!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // falha
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    // classe de suporte de visualizar para guardar visão do UI das tasks
    class HolderTask extends RecyclerView.ViewHolder{
        // visão ui de row_task.xml
        TextView taskTv;
        ImageButton deleteBtn;

        public HolderTask(@NonNull View itemView) {
            super(itemView);

            // inicia visão
            taskTv = binding.taskTv;
            deleteBtn = binding.deleteBtn;
        }
    }
}
