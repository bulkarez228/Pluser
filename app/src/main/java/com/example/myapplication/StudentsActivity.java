package com.example.myapplication;

import static com.example.myapplication.ClassesActivity.classesList;
import static com.example.myapplication.ClassesActivity.firstrun3;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.puder.highlight.HighlightManager;

import java.util.Collections;
import java.util.Comparator;

@RequiresApi(api = Build.VERSION_CODES.N)
public class StudentsActivity extends AppCompatActivity {

    RecyclerView studentsRV;
    CheckBox editBTN;
    ImageView addBTN;
    ImageView clearBTN;
    ImageView sortBTN;
    ImageView plusOneBTN;
    TextView thereNothingToSee;
    StudentsAdapter adapter = new StudentsAdapter();
    TextView tittle;

    boolean editable;
    boolean doubleClick;
    boolean firstrun4;
    int class_;
    private static StudentsActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);
        Bundle bundle = getIntent().getExtras();
        class_ = bundle.getInt("class");
        Handler doubleHandler = new Handler();
        HighlightManager highlightManager = new HighlightManager(this);
        instance = this;

        studentsRV = findViewById(R.id.students);
        editBTN = findViewById(R.id.edit_btn);
        addBTN = findViewById(R.id.add_btn);
        clearBTN = findViewById(R.id.clear_btn);
        sortBTN = findViewById(R.id.sort_btn);
        plusOneBTN = findViewById(R.id.plus_one_btn);
        tittle = findViewById(R.id.title);
        thereNothingToSee = findViewById(R.id.there_nothing_to_see);
        checkStudents();

        if (firstrun3){
            highlightManager.addView(R.id.edit_btn).setTitle(R.string.fifth);
        }

        initRV();

        editBTN.setOnClickListener(view -> {
            editable=!editable;
            if (editable){
                tittle.setText("Редактирование");
                tittle.setTextSize(25);
                if (classesList.get(class_).getStudentsList().isEmpty()){
                    classesList.get(class_).getStudentsList().add(new Students());
                }
                if (!firstrun3){
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                }
                else{
                    highlightManager.addView(R.id.add_btn).setTitle(R.string.sixth);
                    highlightManager.addView(R.id.edit_btn).setTitle(R.string.seventh);
                    firstrun3=false;
                    firstrun4 = true;
                }
                adapter.notifyDataSetChanged();
            }
            else{
                tittle.setText("Ученики");
                tittle.setTextSize(32);
                for (int i=0; i<classesList.get(class_).getStudentsList().size(); i++){
                    if (classesList.get(class_).getStudentsList().get(i).getName().trim().equals("")){
                        classesList.get(class_).getStudentsList().remove(i);
                        i--;
                    }
                }
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                if (firstrun4 && !classesList.get(class_).getStudentsList().isEmpty()){
                    highlightManager.addView(R.id.score).setTitle(R.string.eights);
                    highlightManager.addView(R.id.plus_one_btn).setTitle(R.string.ninth);
                    highlightManager.addView(R.id.clear_btn).setTitle(R.string.tenth);
                    highlightManager.addView(R.id.sort_btn).setTitle(R.string.eleventh);
                    highlightManager.addView(R.id.color_btn).setTitle(R.string.tvelth);
                    highlightManager.addView(R.id.title).setTitle(R.string.thirteenth);
                    firstrun3=false;
                    firstrun4=false;
                }
            }
            adapter.setEditable();
            addBTN.setVisibility(addBTN.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            clearBTN.setVisibility(clearBTN.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            sortBTN.setVisibility(sortBTN.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            plusOneBTN.setVisibility(plusOneBTN.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            sortStudents(classesList.get(class_).getTypeOfSort());
            checkStudents();
            adapter.notifyDataSetChanged();
            DataSaver.SaveData(StudentsActivity.this, classesList);
        });

        addBTN.setOnClickListener((view) -> {
            classesList.get(class_).getStudentsList().add(0, new Students());
            adapter.notifyDataSetChanged();
            checkStudents();
        });

        clearBTN.setOnClickListener(view -> clearStudentsDialog());

        plusOneBTN.setOnClickListener(view -> {
            for (int i=0; i<classesList.get(class_).getStudentsList().size(); i++){
                classesList.get(class_).getStudentsList().get(i).score++;
            }
            vibrate();
            adapter.notifyDataSetChanged();
            DataSaver.SaveData(StudentsActivity.this, classesList);
        });

        sortBTN.setOnClickListener(view -> sortStudentsDiolog());

        adapter.SetOnItemClickListener((view, position) -> {
            if (!editable) {
                if (doubleClick) {
                    classesList.get(class_).getStudentsList().get(position).score++;
                    sortStudents(classesList.get(class_).getTypeOfSort());
                    adapter.notifyDataSetChanged();
                    vibrate();
                    DataSaver.SaveData(StudentsActivity.this, classesList);
                } else {
                    doubleClick = true;
                    Runnable task = () -> doubleClick = false;
                    Thread thread = new Thread(task);
                    doubleHandler.postDelayed(thread, 500);
                }
            }
            else{
                Toast.makeText(this, "Выйдите из режима редактирование, для продолжения", Toast.LENGTH_LONG).show();
            }
        });

        adapter.SetOnItemLongClickListener((view, position) -> {
            if (!editable) {
                classesList.get(class_).getStudentsList().get(position).score--;
                sortStudents(classesList.get(class_).getTypeOfSort());
                DataSaver.SaveData(StudentsActivity.this, classesList);
                vibrate();
                adapter.notifyDataSetChanged();
            }
            else{
                Toast.makeText(this, "Выйдите из режима редактирование, для продолжения", Toast.LENGTH_LONG).show();
            }
        });
    }

    static StudentsActivity getInstance(){
        return instance;
    }

    void initRV(){

        studentsRV.setAdapter(adapter);
        studentsRV.setLayoutManager(new LinearLayoutManager(this));
        adapter.setItems(classesList.get(class_).getStudentsList());
    }

    void deleteStudent(Students students) {
        LayoutInflater li = LayoutInflater.from(StudentsActivity.this);
        View promptsView = li.inflate(R.layout.delete_student_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudentsActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setPositiveButton("Да", (dialog, id) -> {
                    classesList.get(class_).getStudentsList().remove(students);
                    DataSaver.SaveData(StudentsActivity.this, classesList);
                    checkStudents();
                    adapter.notifyDataSetChanged();
                })
                .setNegativeButton("Нет", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    void sortStudentsDiolog(){
            final String[] NamesArray = {"Алфавитная", "По возрастанию баллов", "По убыванию баллов"};
            int[] typeOfSort = new int[1];
            AlertDialog.Builder builder = new AlertDialog.Builder(StudentsActivity.this);
            builder.setTitle("Тип сортировки")
                    .setSingleChoiceItems(NamesArray, classesList.get(class_).getTypeOfSort(),
                            (dialog, item) -> {
                                classesList.get(class_).setTypeOfSort(item);
                                sortStudents(item);
                                DataSaver.SaveData(StudentsActivity.this, classesList);
                                adapter.notifyDataSetChanged();
                                dialog.dismiss();
                            })
                    .setNegativeButton("Отмена", (dialog, id) -> { });
            builder.create();
            builder.show();
    }

    void sortStudents(int typeOfSort){
        switch (typeOfSort){
            case 2:
                Collections.sort(classesList.get(class_).getStudentsList(), comparatorOfScore);
                Collections.reverse(classesList.get(class_).getStudentsList());
                break;
            case 1:
                Collections.sort(classesList.get(class_).getStudentsList(), comparatorOfScore);
                break;
            case 0:
                Collections.sort(classesList.get(class_).getStudentsList(), comparatorOfNames);
                break;
        }
    }

    void newStudentDialog() {
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.new_student_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudentsActivity.this);
        alertDialogBuilder.setView(promptsView);
        final EditText userInput = promptsView.findViewById(R.id.etUserInput);
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        userInput.post(() -> {
            userInput.requestFocus();
            inputManager.showSoftInput(userInput, 0);
        });
        alertDialogBuilder
                .setPositiveButton("Ок", (dialog, id) -> {
                    String temp = userInput.getText().toString();
                    if (temp.equals("") || temp.equals(" ")){
                        Toast.makeText(this, "Вы не ввели имя!", Toast.LENGTH_LONG).show();
                    }
                    else {
                        Log.i("LOG", temp);
                        Students students = new Students(temp);
                        classesList.get(class_).getStudentsList().add(students);
                        adapter.notifyDataSetChanged();
                        DataSaver.SaveData(StudentsActivity.this, classesList);
                        checkStudents();
                    }
                })
                .setNegativeButton("Отмена", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void clearStudentsDialog(){
        LayoutInflater li = LayoutInflater.from(getApplicationContext());
        View promptsView = li.inflate(R.layout.clear_students_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(StudentsActivity.this);

        alertDialogBuilder.setView(promptsView);

        alertDialogBuilder
                .setPositiveButton("Да", (dialog, id) -> {
                    for (Students students : classesList.get(class_).getStudentsList()){
                        students.setScore(0);
                    }
                    adapter.notifyDataSetChanged();
                    DataSaver.SaveData(StudentsActivity.this, classesList);
                    checkStudents();
                })
                .setNegativeButton("Нет", (dialog, id) -> dialog.cancel());
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    void vibrate(){
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            v.vibrate(100);
        }
    }

    public void checkStudents(){
        thereNothingToSee.setVisibility(classesList.get(class_).getStudentsList().isEmpty() ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        DataSaver.SaveData(StudentsActivity.this, classesList);
    }

    Comparator<Students> comparatorOfNames = Comparator.comparing(Students::getName);
    Comparator<Students> comparatorOfScore = Comparator.comparing(Students::getScore);
}