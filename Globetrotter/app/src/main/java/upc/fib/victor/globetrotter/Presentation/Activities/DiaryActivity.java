package upc.fib.victor.globetrotter.Presentation.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Presentation.Utils.DiaryListAdapter;
import upc.fib.victor.globetrotter.R;

public class DiaryActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private FirebaseDatabaseController firebaseDatabaseController;

    private ListView diaryListView;
    private TextView noPagesTxt;

    private List<Pair<String,String>> diaryList;
    private DiaryListAdapter adapter;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setTitle("Tu diario de viajero");

        diaryListView = findViewById(R.id.diary_list);
        noPagesTxt = findViewById(R.id.noPaginasTxt);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando tus historias...");
        progressDialog.show();

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        firebaseDatabaseController = FirebaseDatabaseController.getInstance();

        diaryList = new ArrayList<>();

        getList();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firebaseDatabaseController != null) getList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_diary, menu);
        Drawable drawable = menu.findItem(R.id.action_add_note).getIcon();
        if(drawable != null) {
            drawable.mutate();
            drawable.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_note:
                Intent pageIntent = new Intent(getApplicationContext(), DiaryPageActivity.class);
                pageIntent.putExtra("pageId", "");
                startActivity(pageIntent);
                break;

            case android.R.id.home:

                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getList() {
        firebaseDatabaseController.getUserPages(uid, new FirebaseDatabaseController.GetUserPagesResponse() {
            @Override
            public void success(HashMap<String, String> pages) {
                Iterator it = pages.entrySet().iterator();

                diaryList = new ArrayList<>();

                while (it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    diaryList.add(new Pair<String, String>(pair.getKey().toString(), pair.getValue().toString()));
                    it.remove();
                }
                fillList();
                progressDialog.dismiss();
            }

            @Override
            public void error() {
                fillList();
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: No se ha podido obtener tu diario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setVisibleNoPagesTxt() {
        noPagesTxt.setVisibility(View.VISIBLE);
    }

    private void setGoneNoPagesTxt() {
        noPagesTxt.setVisibility(View.GONE);
    }

    private void fillList() {
        adapter = new DiaryListAdapter(this, diaryList);
        diaryListView.setAdapter(adapter);
        if(adapter.getCount() == 0) setVisibleNoPagesTxt();
        else setGoneNoPagesTxt();
    }

    public void deletePage(String id, int position) {
        firebaseDatabaseController.deletePage(uid, id);
        diaryList.remove(position);
        adapter.notifyDataSetChanged();
        if(adapter.getCount() == 0) setVisibleNoPagesTxt();
        else setGoneNoPagesTxt();
    }

}
