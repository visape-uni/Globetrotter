package upc.fib.victor.globetrotter.Presentation.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Calendar;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.DiaryPage;
import upc.fib.victor.globetrotter.R;

public class DiaryPageActivity extends AppCompatActivity {

    private String uid;
    private DiaryPage diaryPage;

    private FirebaseDatabaseController firebaseDatabaseController;

    private EditText titleTxt;
    private EditText contentTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_page);

        setTitle("Página del diario de viajero");

        titleTxt = findViewById(R.id.page_title);
        contentTxt = findViewById(R.id.page_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        firebaseDatabaseController = FirebaseDatabaseController.getInstance();


        String pageId = getIntent().getStringExtra("pageId");
        if (pageId.isEmpty()) {
            diaryPage = new DiaryPage();
        } else {
            firebaseDatabaseController.getPage(uid, pageId, new FirebaseDatabaseController.GetPageResponse() {
                @Override
                public void success(DiaryPage page) {
                    diaryPage = page;
                    showPage();
                }

                @Override
                public void notFound() {
                    diaryPage = new DiaryPage();
                    Toast.makeText(getApplicationContext(), "Página no encontrada", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void error(String message) {
                    Toast.makeText(getApplicationContext(), "Ha sucedido un error recuperando los datos: " + message, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_activity_diary_page, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_note:
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(titleTxt.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(contentTxt.getWindowToken(), 0);
                //Guardar nota
                if (titleTxt.getText().toString().trim().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "La página debe tener título", Toast.LENGTH_LONG).show();
                } else {
                    if (diaryPage.getDateModified() != null) {
                        firebaseDatabaseController.deletePage(uid, String.valueOf(diaryPage.getDateModified().getTime()));
                    }
                    DiaryPage page = new DiaryPage(uid, titleTxt.getText().toString().trim(), contentTxt.getText().toString().trim(), Calendar.getInstance().getTime());
                    firebaseDatabaseController.storePage(uid, page, new FirebaseDatabaseController.StorePageResponse() {
                        @Override
                        public void success() {
                            Toast.makeText(getApplicationContext(), "Página guardada correctamente", Toast.LENGTH_LONG).show();
                        }

                        @Override
                        public void error() {
                            Toast.makeText(getApplicationContext(), "Ha sucedido un error, vuelve a intentarlo más tarde", Toast.LENGTH_LONG).show();
                        }
                    });
                }
                break;

            case android.R.id.home:

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case DialogInterface.BUTTON_POSITIVE:
                                finish();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(DiaryPageActivity.this);
                builder.setMessage("¿Estás seguro de que quieres cerrar? Los cambios no guardados se perderán.")
                        .setPositiveButton("Sí", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showPage() {
        titleTxt.setText(diaryPage.getTitle());
        contentTxt.setText(diaryPage.getContent());
    }
}
