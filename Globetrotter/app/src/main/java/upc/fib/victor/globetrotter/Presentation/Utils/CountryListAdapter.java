package upc.fib.victor.globetrotter.Presentation.Utils;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import upc.fib.victor.globetrotter.Presentation.Activities.UserMapActivity;
import upc.fib.victor.globetrotter.R;


public class CountryListAdapter extends ArrayAdapter<String> {

    private HashMap<String, String> visitados;
    private UserMapActivity context;

    public CountryListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    public CountryListAdapter (Context context, List<String> items, HashMap<String, String> visitados) {
        super(context,R.layout.custom_list,items);
        this.visitados = visitados;
        this.context = (UserMapActivity) context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;

        if (convertView == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            row = vi.inflate(R.layout.custom_list, null);
        }


        final String countryName = getItem(position);

        if (countryName != null) {
            TextView countryNameTxt = row.findViewById(R.id.countryNameTxt);
            Switch visitedSwitch = row.findViewById(R.id.visitedSwitch);
            countryNameTxt.setText(countryName);

            visitedSwitch.setChecked(visitados.containsKey(countryName));

            visitedSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Switch visitedSwitch = (Switch) view;
                    if (visitedSwitch.isChecked()) {
                        context.setVisited(countryName);
                        visitados.put(countryName, "id");
                    } else {
                        context.setUnvisited(countryName);
                        visitados.remove(countryName);
                    }
                }
            });
        }

        return row;
    }
}
