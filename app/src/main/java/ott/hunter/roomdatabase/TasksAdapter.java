package ott.hunter.roomdatabase;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ott.hunter.R;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TasksViewHolder> {

//    private View.OnClickListener onClickListener;
    private Context mCtx;
    private List<Task> taskList;

private  EditText search_edit_text;

    public TasksAdapter(Context mCtx, List<Task> taskList, EditText  search_edit_text) {
        this.mCtx = mCtx;
        this.taskList = taskList;
        this.search_edit_text = search_edit_text;

    }

 /*   public TasksAdapter(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;

    }*/



    @Override
    public TasksViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mCtx).inflate(R.layout.recyclerview_tasks, parent, false);
        return new TasksViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TasksViewHolder holder, int position) {
        Task t = taskList.get(position);
        holder.textViewTask.setText(t.getTask());
        holder.textViewDesc.setText(t.getDesc());
        holder.textViewFinishBy.setText(t.getFinishBy());

/*
        holder.textViewTask.setOnClickListener(onClickListener);*/

        holder.textViewTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            //    SearchActivity.searchkeyword = t.getTask();
                search_edit_text.setText(t.getTask());
            }
        });



        holder.clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteTask(t);

                taskList.remove(position);
                notifyItemRemoved(position);
                notifyDataSetChanged();

            }
        });


        if (t.isFinished())
            holder.textViewStatus.setText("Completed");
        else
            holder.textViewStatus.setText("Not Completed");
    }

    private void deleteTask(final Task task) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
//                DatabaseClient.getInstance(mCtx).getAppDatabase()
//                        .taskDao()
//                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                // Toast.makeText(mCtx, "Deleted", Toast.LENGTH_LONG).show();
             /*   mCtx.finish();
                startActivity(new Intent(UpdateTaskActivity.this, MainActivity.class));*/
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TasksViewHolder extends RecyclerView.ViewHolder {

        TextView textViewStatus, textViewTask, textViewDesc, textViewFinishBy;
        ImageView clear;

        public TasksViewHolder(View itemView) {
            super(itemView);

            textViewStatus = itemView.findViewById(R.id.textViewStatus);
            textViewTask = itemView.findViewById(R.id.textViewTask);
            textViewDesc = itemView.findViewById(R.id.textViewDesc);
            clear = itemView.findViewById(R.id.clear);
            textViewFinishBy = itemView.findViewById(R.id.textViewFinishBy);


            //  itemView.setOnClickListener(this);
        }

       /* @Override
        public void onClick(View view) {
            //Task task = taskList.get(getAdapterPosition());

           *//* Intent intent = new Intent(mCtx, DetailsActivity.class);
            intent.putExtra("task", task);

*//*
            Toast.makeText(mCtx, "clicked", Toast.LENGTH_LONG).show();


           // mCtx.startActivity(intent);
        }*/
    }
}

