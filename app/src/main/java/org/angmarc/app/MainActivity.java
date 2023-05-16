package org.angmarc.app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.view.View;
import android.widget.Toast;

import org.angmarch.views.NiceSpinner;
import org.angmarch.views.OnSpinnerItemSelectedListener;
import org.angmarch.views.SpinnerTextFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupDefault();
        setupTintedWithCustomClass();
        setupXml();
    }

    private void setupXml() {
        NiceSpinner spinner = findViewById(R.id.niceSpinnerXml);
        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupTintedWithCustomClass() {
        final NiceSpinner spinner = findViewById(R.id.tinted_nice_spinner);
        List<Person> people = new ArrayList<>();

        people.add(new Person("Tony", "Stark"));
        people.add(new Person("Steve", "Rogers"));
        people.add(new Person("Bruce", "Banner"));

        SpinnerTextFormatter textFormatter = new SpinnerTextFormatter<Person>() {
            @Override
            public Spannable format(Person person) {
                return new SpannableString(person.getName() + " " + person.getSurname());
            }
        };

        spinner.setSpinnerTextFormatter(textFormatter);
        spinner.setSelectedTextFormatter(textFormatter);
        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                Person person = (Person) spinner.getSelectedItem();
                Toast.makeText(MainActivity.this, "Selected: " + person.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        spinner.attachDataSource(people);
    }

    private void setupDefault() {
        NiceSpinner spinner = findViewById(R.id.nice_spinner);
        List<String> dataset = new LinkedList<>(Arrays.asList("One", "Two", "Three", "Four", "Five"));
        spinner.attachDataSource(dataset);
        spinner.setOnSpinnerItemSelectedListener(new OnSpinnerItemSelectedListener() {
            @Override
            public void onItemSelected(NiceSpinner parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(MainActivity.this, "Selected: " + item, Toast.LENGTH_SHORT).show();
            }
        });
    }
}

class Person {

    private String name;
    private String surname;

    Person(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    String getName() {
        return name;
    }

    String getSurname() {
        return surname;
    }

    @Override
    public String toString() {
        return name + " " + surname;
    }
}
