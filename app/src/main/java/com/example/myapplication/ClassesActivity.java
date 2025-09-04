package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.puder.highlight.HighlightManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


@RequiresApi(api = Build.VERSION_CODES.N)
public class ClassesActivity extends AppCompatActivity {

    RecyclerView classesRV;
    CheckBox editBTN;
    ImageView addBTN;
    TextView thereNothingToSee;
    TextView tittle;

    boolean editable;
    public static ClassesAdapter adapter = new ClassesAdapter();
    public static ArrayList<Classes> classesList = new ArrayList<>();
    private static ClassesActivity instance;
    public static SharedPreferences settings;
    public static final String APP_PREFERENCES = "settings";
    public static boolean firstrun1;
    public static boolean firstrun2;
    public static boolean firstrun3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        settings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        HighlightManager highlightManager = new HighlightManager(this);
        instance = this;

        classesRV = findViewById(R.id.classes);
        editBTN = findViewById(R.id.edit_btn);
        addBTN = findViewById(R.id.add_btn);
        thereNothingToSee = findViewById(R.id.there_nothing_to_see);
        tittle = findViewById(R.id.title);

        if (settings.getBoolean("firstrun", true)) {
            settings.edit().putBoolean("firstrun", false).apply();
            firstrun1 = true;
            firstrun3=true;
        }
        else{
            classesList = DataSaver.getData(this);
        }

        initRV();

        if (firstrun1) {
            highlightManager.reshowAllHighlights();
            highlightManager.addView(R.id.edit_btn).setTitle(R.string.first_main).setDescriptionId(R.string.first_secondary);
        }


        editBTN.setOnClickListener(view -> {
            editable=!editable;
            if (editable){
                tittle.setText("Редактирование");
                tittle.setTextSize(25);
                if (classesList.isEmpty()){
                    classesList.add(new Classes());
                }
                if(!firstrun1) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                else {
                    highlightManager.addView(R.id.add_btn).setTitle(R.string.second);
                    highlightManager.addView(R.id.edit_btn).setTitle(R.string.third);
                    firstrun1=false;
                    firstrun2=true;
                }
                adapter.notifyDataSetChanged();
            }
            else{
                tittle.setText("Классы");
                tittle.setTextSize(32);
                for (int i=0; i<classesList.size(); i++){
                    if (classesList.get(i).getName().trim().equals("")){
                        classesList.remove(i);
                        i--;
                    }
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (firstrun2 && !classesList.isEmpty()){
                    highlightManager.addView(R.id.name).setTitle(R.string.fourth);
                    firstrun2=false;
                    firstrun3=true;
                }
            }
            adapter.setEditable();
            addBTN.setVisibility(addBTN.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            Collections.sort(classesList, comparatorOfNames);
            checkClasses();
            adapter.notifyDataSetChanged();
            DataSaver.SaveData(ClassesActivity.this, classesList);
        });

        addBTN.setOnClickListener(view -> {
            classesList.add(0, new Classes());
            adapter.notifyDataSetChanged();
            checkClasses();
        });

        adapter.SetOnItemClickListener((view, position) -> {
            if(!editable) {
                Intent intent = new Intent(ClassesActivity.this, StudentsActivity.class);
                intent.putExtra("class", position);
                startActivity(intent);
            }
            else{
                Toast.makeText(this, "Выйдите с режима редактирование, для продолжения", Toast.LENGTH_LONG).show();
            }
        });

    }



    void initRV(){
        thereNothingToSee.setVisibility(classesList.isEmpty() ? View.VISIBLE : View.GONE);
        classesRV.setAdapter(adapter);
        classesRV.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItems(classesList);
    }

    static ClassesActivity getInstance(){
        return instance;
    }

    void NewClassDialog() {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.new_class_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClassesActivity.this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = (EditText) promptsView.findViewById(R.id.etUserInput);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        userInput.post(() -> {
            userInput.requestFocus();
            inputManager.showSoftInput(userInput, 0);
        });
        alertDialogBuilder
                .setPositiveButton("Ок", (dialog, id) -> {
                    String temp = userInput.getText().toString();
                    if (temp.equals("") || temp.equals(" ")) {
                        Toast.makeText(this, "Вы не ввели название!", Toast.LENGTH_LONG).show();
                    } else {
                        Classes classes = new Classes(temp);
                        classesList.add(classes);
                        adapter.notifyDataSetChanged();
                        DataSaver.SaveData(ClassesActivity.this, classesList);
                        checkClasses();
                    }
                })
                .setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void deleteClass(Classes class_) {
        LayoutInflater li = LayoutInflater.from(ClassesActivity.this);
        View promptsView = li.inflate(R.layout.delete_class_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ClassesActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setPositiveButton("Да", (dialog, id) -> {
                    classesList.remove(class_);
                    DataSaver.SaveData(ClassesActivity.this, classesList);
                    adapter.notifyDataSetChanged();
                    ClassesActivity.getInstance().checkClasses();
                })
                .setNegativeButton("Нет", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        DataSaver.SaveData(ClassesActivity.this, classesList);
    }

    public void checkClasses(){
        thereNothingToSee.setVisibility(classesList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    Comparator<Classes> comparatorOfNames = Comparator.comparing(Classes::getName);
}