package upc.fib.victor.globetrotter.Presentation.Utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import upc.fib.victor.globetrotter.Presentation.Activities.DiaryActivity;
import upc.fib.victor.globetrotter.Presentation.Activities.DiaryPageActivity;
import upc.fib.victor.globetrotter.R;

public class DiaryListAdapter extends ArrayAdapter<Pair<String,String>> {

    private DiaryActivity context;


    public DiaryListAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }
    public DiaryListAdapter(Context context, List<Pair<String,String>> items) {
        super(context, R.layout.custom_diary_list, items);
        this.context = (DiaryActivity) context;
    }



    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        if(convertView == null) {
            LayoutInflater vi = LayoutInflater.from(getContext());
            row = vi.inflate(R.layout.custom_diary_list, null);
        }

        String title = getItem(position).second;

        if (title != null) {
            TextView titleTxt = row.findViewById(R.id.diary_title_txt);
            ImageButton deleteBtn = row.findViewById(R.id.delete_btn);
            titleTxt.setText(title);

            titleTxt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent pageIntent = new Intent(context, DiaryPageActivity.class);
                    pageIntent.putExtra("pageId", getItem(position).first);
                    context.startActivity(pageIntent);
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    context.deletePage(getItem(position).first, position);
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage("¿Estás seguro de que quieres eliminar esta página?")
                            .setPositiveButton("Sí", dialogClickListener)
                            .setNegativeButton("No", dialogClickListener).show();
                }
            });
        }

        return row;
    }
}
