package com.example.planer;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {
    private Context context;
    private List<Task> taskList;
    private DatabaseHelper db;

    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;

    public TaskAdapter(Context context, List<Task> taskList) {
        this.context = context;
        this.taskList = taskList;
        this.db = new DatabaseHelper(this.context);
    }

    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item,null,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        Task task = taskList.get(position);
        holder.title.setText(task.getTitle());
        holder.description.setText("Описание : " + task.getDescription());
        holder.dateAdded.setText(task.getDateTaskAdded());
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private Context context;

        public TextView title;
        public TextView description;
        public TextView dateAdded;

        public Button rowDeleteButton;
        public Button rowEditButton;



        public ViewHolder(@NonNull View itemView, Context ctx) {
            super(itemView);
            this.context = ctx;

            title = itemView.findViewById(R.id.row_title);
            description = itemView.findViewById(R.id.row_description);
            dateAdded = itemView.findViewById(R.id.row_dateadded);

            rowEditButton = itemView.findViewById(R.id.row_edit_button);
            rowDeleteButton = itemView.findViewById(R.id.row_delete_button);

            rowDeleteButton.setOnClickListener(this);
            rowEditButton.setOnClickListener(this);



        }

        @Override
        public void onClick(View view) {
            //мы получаем идентификатор нажатой кнопки и удаляем / редактируем
            int position = getAdapterPosition();
            Task task = taskList.get(position);
            if (view.getId() == R.id.row_delete_button){
                deleteTask(task.getId());
            }
            else if (view.getId() == R.id.row_edit_button){
                updateTask(task);
            }
        }



        private void deleteTask(final int id){

            builder= new AlertDialog.Builder(context);
            View view = LayoutInflater.from(context).inflate(R.layout.delete_popup,null);

            Button yesButton = view.findViewById(R.id.yes_button);
            Button  noButton = view.findViewById(R.id.no_button);

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //вызов базы данных и удаление
                    db.deleteTask(id);
                    //remove from the recycler view too
                    taskList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());
                    alertDialog.dismiss();
                }
            });

            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });


        }
        private void updateTask(final Task newtodo ) {

            builder = new AlertDialog.Builder(context);

            View view = LayoutInflater.from(context).inflate(R.layout.pop_up,null);

            TextView titleTextView = view.findViewById(R.id.title);
            final EditText titleeditText = view.findViewById(R.id.popup_title);
            final EditText titleDescription = view.findViewById(R.id.popup_description);
            Button saveButton = view.findViewById(R.id.popup_button);


            //позиция текущего выбранного нами элемента берется из адаптера и передается в элемент действия
            final Task task = taskList.get(getAdapterPosition());

            //получение сохраненных элементов в edittext
            titleTextView.setText(R.string.title_text);
            saveButton.setText(R.string.update_text);

            titleeditText.setText(task.getTitle());
            titleDescription.setText(task.getDescription());

            builder.setView(view);
            alertDialog = builder.create();
            alertDialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //todo update the items entered by the user
                    db = new DatabaseHelper(context);

                    newtodo.setTitle(titleeditText.getText().toString().trim());
                    newtodo.setDescription(titleDescription.getText().toString().trim());

                    if (!titleeditText.getText().toString().isEmpty() && !titleDescription.getText().toString().isEmpty()){
                        db.updateTask(newtodo);
                        notifyItemChanged(getAdapterPosition(),newtodo);
                        Snackbar.make(view,"Задача обновлена",Snackbar.LENGTH_SHORT);
                    }else {
                        Snackbar.make(view,"Поля не могут быть пустыми",Snackbar.LENGTH_SHORT);
                    }
                    alertDialog.dismiss();


                    db.close();
                }
            });

        }

    }
}

