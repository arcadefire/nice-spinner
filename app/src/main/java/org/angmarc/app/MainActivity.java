package org.angmarc.app;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

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
        String str = niceSpinner.getCurrentItem();
        Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();

        List<String> data = new ArrayList<>();
        data.add("it's clickable");
        data.add("setOnItemClickListener");
        data.add("Hello World!");
        NiceSpinner tintedSpinner = findViewById(R.id.tinted_nice_spinner);
        tintedSpinner.attachDataSource(data);
        tintedSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, str, Toast.LENGTH_SHORT).show();
            }
        });

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
        bottomSpinner.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "You are select the " + position + " item: " + parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });
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

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                '}';
    }
}
