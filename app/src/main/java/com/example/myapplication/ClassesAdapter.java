package com.example.myapplication;

import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.myapplication.ClassesActivity.adapter;

import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.ClassesViewHolder> {

    OnItemClickListener mItemClickListener;
    OnItemLongClickListener mItemLongClickListener;
    private ArrayList<Classes> classesList = new ArrayList<>();
    private boolean editable;

    @NonNull
    @Override
    public ClassesAdapter.ClassesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.classes_adapter, parent, false);
        ClassesViewHolder vh = new ClassesViewHolder(view, new MyCustomEditTextListener());
        return vh;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onBindViewHolder(@NonNull ClassesAdapter.ClassesViewHolder holder, int position) {
        holder.bind(classesList.get(position));
        holder.myCustomEditTextListener.updatePosition(holder.getAdapterPosition());
        holder.editName.setText(classesList.get(holder.getAdapterPosition()).getName());
    }

    @Override
    public int getItemCount() {
        return classesList.size();
    }

    public void setItems(ArrayList<Classes> arr) {
        classesList=arr;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        public void onItemClick(View view , int position);
    }

    public interface OnItemLongClickListener {
        public void onItemLongClick(View view , int position);
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

    class ClassesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

        public TextView name;
        public ImageView delBTN;
        public Classes class_;
        EditText editName;
        MyCustomEditTextListener myCustomEditTextListener;
        public ClassesViewHolder(View itemView, MyCustomEditTextListener myCustomEditTextListener) {
            super(itemView);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            name = itemView.findViewById(R.id.name);
            delBTN = itemView.findViewById(R.id.delete_btn);

            this.editName = itemView.findViewById(R.id.editTextName);
            this.myCustomEditTextListener = myCustomEditTextListener;
            this.editName.addTextChangedListener(myCustomEditTextListener);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        public void bind(Classes class_){
            this.class_=class_;
            name.setText(class_.getName());
            name.setSelected(true);
            delBTN.setVisibility(editable ? View.VISIBLE : View.GONE);
            editName.setVisibility(editable ? View.VISIBLE : View.GONE);
            name.setVisibility(!editable ? View.VISIBLE : View.GONE);
            delBTN.setOnClickListener(view -> ClassesActivity.getInstance().deleteClass(class_));
            if (classesList.indexOf(class_)==0){
                editName.requestFocus();
            }
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
            classesList.get(position).setName(charSequence.toString());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    }
}