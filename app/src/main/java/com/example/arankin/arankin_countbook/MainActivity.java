package com.example.arankin.arankin_countbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static android.provider.Telephony.Mms.Part.FILENAME;
import static com.example.arankin.arankin_countbook.R.id.parent;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_COUNTER_REQUEST = 1;
    public static final int EDIT_COUNTER_REQUEST = 2;

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CounterLayoutAdapter adapter;
    private TextView numCounterText;

    ArrayList<Counter> counterList = new ArrayList<Counter>();
    int numCounters = counterList.size();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        counterList = loadFromFile();


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CounterLayoutAdapter(counterList, this);
        recyclerView.setAdapter(adapter);
        numCounterText = (TextView) findViewById(R.id.numCountersText);
        numCounterText.setText(String.valueOf(numCounters));




        Button counterButton = (Button) findViewById(R.id.newCounterButton);
        counterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent counterPage = new Intent(MainActivity.this, AddCounterActivity.class);
                startActivityForResult(counterPage, NEW_COUNTER_REQUEST);
            }
        });

        Button clearButton = (Button) findViewById(R.id.clearCountersButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                counterList.clear();
                updateCountersNumber();


            }
        });
    }

    protected void onStart() {
        super.onStart();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView1);
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapter = new CounterLayoutAdapter(counterList, this);
        recyclerView.setAdapter(adapter);
        numCounters = counterList.size();
        numCounterText.setText(String.valueOf(numCounters));
        adapter.notifyDataSetChanged();
        saveToFile();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NEW_COUNTER_REQUEST && resultCode == RESULT_OK){
            int number = data.getIntExtra("number",0);
            String name = data.getStringExtra("name");
            String comment = data.getStringExtra("comment");
            Counter newCounter = new Counter(number,name,comment);
            counterList.add(newCounter);
            numCounters = counterList.size();
            numCounterText.setText(String.valueOf(numCounters));
            adapter.notifyDataSetChanged();
            saveToFile();
        }
        if (requestCode == EDIT_COUNTER_REQUEST && resultCode == RESULT_OK) {
            int position = data.getIntExtra("position",0);
            Counter counter = counterList.get(position);
            String name = data.getStringExtra("name");
            String comment = data.getStringExtra("comment");
            int currentValue = data.getIntExtra("current_number",0);
            int initialValue = data.getIntExtra("initial_number",0);
            counter.setCounterName(name);
            counter.setComment(comment);
            counter.setInitialValue(initialValue);
            counter.setCurrentValue(currentValue);
            counter.setDate();
            saveToFile();

            adapter.onDataReady(counter, position);
        }

    }

    public ArrayList<Counter> loadFromFile() {
        try {
            FileInputStream fis = openFileInput(FILENAME);
            BufferedReader in = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();

            Type listType = new TypeToken<ArrayList<Counter>>() {}.getType();
            counterList = gson.fromJson(in, listType);
            return counterList;


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block

        }
        return counterList;
    }

    public void saveToFile() {
        try {
            FileOutputStream fos = openFileOutput(FILENAME,
                    Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(counterList,writer);
            writer.flush();


            fos.close();
        }  catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException();
        }
    }

    public void updateCountersNumber(){
        numCounters = counterList.size();
        numCounterText.setText(String.valueOf(numCounters));
        adapter = new CounterLayoutAdapter(counterList, getBaseContext());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        saveToFile();
    }


}