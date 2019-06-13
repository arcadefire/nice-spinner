package org.angmarc.app;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter<Person> {

    CustomAdapter(@NonNull Context context, int resource, List<Person> people) {
        super(context, resource, people);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
    }
}
