package com.example.myapplication;

import static com.example.myapplication.ClassesActivity.classesList;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.StudentsViewHolder> {

    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private final int[] colors = {R.color.black, R.color.bloody_red, R.color.green, R.color.yellow, R.color.blue};
    private ArrayList<Students> studentsList = new ArrayList<>();
    private boolean editable;


    @NonNull
    @Override
    public StudentsAdapter.StudentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.students_adapter, parent, false);
        StudentsAdapter.StudentsViewHolder vh = new StudentsAdapter.StudentsViewHolder(view, new StudentsAdapter.MyCustomEditTextListener());
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull StudentsAdapter.StudentsViewHolder holder, int position) {
        holder.bind(studentsList.get(position));
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.editName.setText(studentsList.get(holder.getAdapterPosition()).getName());
    }

    @Override
    public int getItemCount() {
        return studentsList.size();
    }

    public void setItems(ArrayList<Students> arr) {
        studentsList=arr;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view , int position);
    }

    public void SetOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    public void SetOnItemLongClickListener(final OnItemLongClickListener mItemLongClickListener) {
        this.mItemLongClickListener = mItemLongClickListener;
    }

    public void setEditable(){
        editable = !editable;
    }

    class StudentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView name;
        public TextView score;
        public Students students;
        public ImageView delBTN;
        public ImageView colorBTN;
        public EditText editName;
        public MyCustomEditTextListener myCustomEditTextListener;

        public StudentsViewHolder(View itemView, StudentsAdapter.MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            name = itemView.findViewById(R.id.name);
            name.setSelected(true);
            score = itemView.findViewById(R.id.score);
            delBTN = itemView.findViewById(R.id.delete_btn);
            colorBTN = itemView.findViewById(R.id.color_btn);

            this.editName = itemView.findViewById(R.id.editTextName);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editName.addTextChangedListener(myCustomEditTextListener);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Students students){
            this.students=students;
            name.setText(students.getName());
            score.setText(String.valueOf(students.getScore()));
            delBTN.setVisibility(editable ? View.VISIBLE : View.GONE);
            score.setVisibility(!editable ? View.VISIBLE : View.GONE);
            colorBTN.setVisibility(!editable ? View.VISIBLE : View.GONE);
            editName.setVisibility(editable ? View.VISIBLE : View.GONE);
            name.setVisibility(!editable ? View.VISIBLE : View.GONE);
            delBTN.setOnClickListener(view -> StudentsActivity.getInstance().deleteStudent(students));
            if (studentsList.indexOf(students)==0){
                editName.requestFocus();
            }


            colorBTN.setColorFilter(ContextCompat.getColor(StudentsActivity.getInstance(),
                    colors[students.curColor]), android.graphics.PorterDuff.Mode.MULTIPLY);
            name.setTextColor(ContextCompat.getColor(StudentsActivity.getInstance(),
                    colors[students.curColor]));
            score.setTextColor(ContextCompat.getColor(StudentsActivity.getInstance(),
                    colors[students.curColor]));

            colorBTN.setOnClickListener(view -> {
                students.curColor++;
                StudentsActivity.getInstance().vibrate();
                if (students.curColor==5)
                    students.curColor=0;
                colorBTN.setColorFilter(ContextCompat.getColor(StudentsActivity.getInstance(),
                        colors[students.curColor]), android.graphics.PorterDuff.Mode.MULTIPLY);
                name.setTextColor(ContextCompat.getColor(StudentsActivity.getInstance(),
                        colors[students.curColor]));
                score.setTextColor(ContextCompat.getColor(StudentsActivity.getInstance(),
                        colors[students.curColor]));
                DataSaver.SaveData(StudentsActivity.getInstance(), classesList);
            });
        }

        @Override
        public void onClick(View view) {
            if (mItemClickListener != null) {
                mItemClickListener.onItemClick(view, getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            mItemLongClickListener.onItemLongClick(view, getAdapterPosition());
            return true;
        }

    }

    private class MyCustomEditTextListener implements TextWatcher {
        private int position;

        public void updatePosition(int position) {
            this.position = position;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            studentsList.get(position).setName(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }

}