package org.angmarc.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.SimpleSpinnerTextFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NiceSpinner niceSpinner = findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        niceSpinner.attachDataSource(dataset);

        NiceSpinner tintedSpinner = findViewById(R.id.tinted_nice_spinner);
        tintedSpinner.attachDataSource(dataset);

        NiceSpinner bottomSpinner = findViewById(R.id.bottom_nice_spinner);

        List<Person> persons = new ArrayList<>();

        persons.add(new Person("John", "Smith"));
        persons.add(new Person("Adam", "Sandler"));
        persons.add(new Person("One", "Two"));

        SimpleSpinnerTextFormatter textFormatter = new SimpleSpinnerTextFormatter() {
            @Override
            public Spannable format(Object item) {
                Person person = (Person) item;
                return new SpannableString(person.getName() + " " + person.getSurname());
            }
        };

        bottomSpinner.setSpinnerTextFormatter(textFormatter);
        bottomSpinner.setSelectedTextFormatter(textFormatter);

        bottomSpinner.attachDataSource(persons);
    }
}

class Person {
    String name;
    String surname;

    Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    String getSurname() {
        return surname;
    }

    void setSurname(String surname) {
        this.surname = surname;
    }
}
