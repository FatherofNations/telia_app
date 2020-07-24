package com.example.myapplication;


import android.content.ActivityNotFoundException;
import java.time.*;
import android.content.Intent;
import android.speech.RecognizerIntent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private final int REQ_CODE = 100;

    Date aug_point = new Date(2020, 8, 25);
    Date sep_point = new Date(2020, 9, 15);

    int[] triple_room = {3500, 2500, 2000};
    int[] double_room = {2600, 2000, 1500};
    int[] four_room = {4000, 3000, 2500};
    int[] lux_room = {5000, 4000, 2600};
    int[] ec_triple_room = {3000, 2000, 1500};
    int[] ec_double_room = {2100, 1500, 1000};

    TextView textView;

    public String check_point(Date start, Date end) {

        if (this.aug_point.getTime() < end.getTime() && this.aug_point.getTime() > start.getTime()) {
            return "aug";
        }

        if (this.sep_point.getTime() < end.getTime() && this.sep_point.getTime() > start.getTime()) {
            return "sep";
        }

        return  "none";
    }

    public Integer relocale(String month) {

        Integer month_number = 7;

        switch (month) {
            case "июля":  month_number = 7;
                break;
            case "августа":  month_number = 8;
                break;
            case "сентября":  month_number = 9;
                break;
            case "октября": month_number = 10;
        }

        return month_number;

    }

    public Integer Calculate(Date point, Date start, Date end, int[] type) {

        Integer index = 0;

        if (point == sep_point) {
            index = 1;
        }

        long milliseconds_one = point.getTime() - start.getTime();
        int days_one = (int) (milliseconds_one / (24 * 60 * 60 * 1000));

        long milliseconds_two = end.getTime() - point.getTime();
        int days_two = (int) (milliseconds_two / (24 * 60 * 60 * 1000));

        return days_one * type[index] + days_two * type[index + 1];
    }

    public Integer getDefaultPrice(Date start, int days, int[] type) {
        LocalDate localDate = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue() - 1;
        int day = localDate.getDayOfMonth();
        int index = 0;

        if (month == 8 && day > 25) { index = 1; }
        if (month == 9 && day > 15) { index = 2; }

        return type[index] * days;
    }

    public int[] getType(String type) {
        switch (type) {
            case "трехместный":  return triple_room;
            case "3-естный":  return triple_room;
            case "двухместный":  return double_room;
            case "2-местный":  return double_room;
            case "четырехместный":  return four_room;
            case "4-местынй":  return four_room;
            case "Люкс": return lux_room;
//            case "эконом_трехместный":  return four_room;
//
//            case "эконом_двухместный": return lux_room;
//
        }
        return triple_room;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        ImageView speak = findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ru");
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Need to speak");
                try {
                    startActivityForResult(intent, REQ_CODE);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Sorry your device not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    String results = result.get(0).toString();

                    String[] parts = results.split(" ");

                    String room = parts[0];
                    Date start_day = new Date(2020, this.relocale(parts[3]), Integer.parseInt(parts[2]));
                    Date end_day = new Date(2020, this.relocale(parts[6]), Integer.parseInt(parts[5]));

                    long milliseconds = end_day.getTime() - start_day.getTime();
                    int days = (int) (milliseconds / (24 * 60 * 60 * 1000));
                    int price = days*0;

                    String pref = "chim";

                    switch (check_point(start_day, end_day)) {
                        case "aug": price = Calculate(aug_point, start_day, end_day, getType(parts[0])); pref = "aug";
                            break;
                        case "sep": price = Calculate(sep_point, start_day, end_day, getType(parts[0])); pref = "sep";
                            break;
                        case "none": price = getDefaultPrice(start_day, days, getType(parts[0])); pref = "none";
                    }





                    textView.setText("Заселение на " + days + " дней, на сумму " + price + "р");


//                    textView.setText(result.get(0).toString());

                }
                break;
            }
        }
    }
}