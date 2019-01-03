package upc.fib.victor.globetrotter.Presentation.Activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Presentation.Utils.CountryListAdapter;
import upc.fib.victor.globetrotter.R;

public class UserMapActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;

    private WebView webView;
    private EditText buscarPais;
    private ListView listView;

    private CountryListAdapter adapter;

    private FirebaseDatabaseController firebaseDatabaseController;

    private String uid;
    private HashMap<String, String> paisesVisitados;

    private HashMap<String, String> paisesId;
    private List<String> idList;
    private List<String> nameList;

    private String[] ids = new String[] {"AD","AE","AF","AG","AI","AL","AM","AO","AQ","AR","AS","AT","AU","AW","AX","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ","BL","BN","BO","BM","BQ","BR","BS","BT","BV","BW","BY","BZ","CA","CC","CD","CF","CG","CH","CI","CK","CL","CM","CN","CO","CR","CU","CV","CW","CX","CY","CZ","DE","DJ","DK","DM","DO","DZ","EC","EE","EG","EH","ER","ES","ET","FI","FJ","FK","FM","FO","FR","GA","GB","GD","GE","GF","GG","GH","GI","GL","GM","GN","GO","GP","GQ","GR","GS","GT","GU","GW","GY","HK","HM","HN","HR","HT","HU","ID","IE","IL","IM","IN","IO","IQ","IR","IS","IT","JE","JM","JO","JP","JU","KE","KG","KH","KI","KM","KN","KP","KR","XK","KW","KY","KZ","LA","LB","LC","LI","LK","LR","LS","LT","LU","LV","LY","MA","MC","MD","ME","MF","MG","MH","MK","ML","MM","MN","MO","MP","MQ","MR","MS","MT","MU","MV","MW","MX","MY","MZ","NA","NC","NE","NF","NG","NI","NL","NO","NP","NR","UN","NZ","OM","PA","PE","PF","PG","PH","PK","PL","PM","PN","PR","PS","PT","PW","PY","QA","RE","RO","RS","RU","RW","SA","SB","SC","SD","SE","SG","SH","SI","SJ","SK","SL","SM","SN","SO","SR","SS","ST","SV","SX","SY","SZ","TC","TD","TF","TG","TH","TJ","TK","TL","TM","TN","TO","TR","TT","TV","TW","TZ","UA","UG","UM-DQ","UM-FQ","UM-HQ","UM-JQ","UM-MQ","UM_WQ","US","UY","UZ","VA","VC","VW","VG","VI","VN","VU","WF","WS","YE","YT","ZA","ZM","ZW"};
    private String[] names = new String[] {"Andorra","Emiratos Árabes Unidos","Afganistán","Antigua y Barbuda","Anguila","Albania","Armenia","Angola","Antártida","Argentina","Samoa Americana","Austria","Australia","Aruba","Islas Aland","Azerbaiyán","Bosnia y Herzegovina","Barbados","Bangladesh","Bélgica","Burkina Faso","Bulgaria","Bahrein","Burundi","Benin","San Bartolomé","Brunéi Darussalam","Bolivia","Islas Bermudas","Caribe Neerlandés","Brasil","Bahamas","Bután","Isla Bouvet","Botsuana","Bielorrusia","Belice","Canadá","Islas Cocos (Keeling)","República Democrática del Congo","República Centroafricana","República del Congo","Suiza","Costa de Margil","Islas Cook","Chile","Camerún","China","Colombia","Costa Rica","Cuba","Cabo Verde","Curazao","Isla de Navidad","Chipre","República Checa","Alemania","Yibuti","Dinamarca","Dominica","República Dominicana","Argelia","Ecuador","Estonia","Egipto","Sahara Occidental","Eritrea","España","Etiopia","Finlandia","Fiyi","Islas Malvinas","Estados Federados de Micronesia","Islas Feroe","Francia","Gabón","Reino Unido","Granada","Georgia","Guayana Francesa","Guernsey","Ghana","Gibraltar","Groenlandia","Gambia","Guinea","Islas Gloriosas","Guadaloupe","Guinea Ecuatorial","Grecia","Islas Georgias del Sur y Sandwich del Sur","Guatemala","Guam","Guinea-Bisáu","Guyana","Hong Kong","Islas Heard y McDonald","Honduras","Croacia","Haití","Hungría","Indonesia","Irlanda","Israel","Isla de Man","India","Territorio Británico del Océano Índico","Irak","Iran","Islandia","Italia","Jersey","Jamaica","Jordán","Japón","Isla Juan De Nova","Kenia","Kirguizstán","Camboya","Kiribati","Comoras","San Cristóbal","Corea del Norte","Corea del Sur","Kosovo","Kuwait","Islas Caimán","Kazajstán","Laos","Líbano","Santa Lucía","Liechtenstein","Sri Lanka","Liberia","Lesoto","Lituania","Luxemburgo","Letonia","Libia","Marruecos","Mónaco","Moldavia","Montenegro","San Martín","Madagascar","Islas Marshall","Macedonia","Mali","Myanmar","Mongolia","Macau","Islas Marinas del Norte","Martinica","Mauritania","Montserrat","Malta","Mauricio","Maldivas","Malawi","Mexico","Malasia","Mozambique","Namibia","Nueva Caledonia","Níger","Isla Norfolk","Nigeria","Nicaragua","Países Bajos","Noruega","Nepal","Nauru","Niue","Nueva Zelanda","Omám","Panamá","Perú","Polinesia Francesa","Papúa Nueva Guinea","Filipinas","Pakistán","Polonia","San Pedro y Miquelón","Islas Pitcairn","Puerto Rico","Territorios Palestinos","Portugal","Palaos","Paraguay","Catar","Reunión","Rumania","Serbia","Rusia","Ruanda","Arabia Saudita","Islas Salomón","Seychelles","Sudán","Suecia","Singapur","Santa Elena","Eslovenia","Svalbard y Jan Mayen","Eslovaquia","Sierra Leona","San Marino","Senegal","Somalia","Surinam","Sudán del Sur","Santo Tomé y Príncipe","El Salvador","San Martín","Siria","Suazilandia","Islas Turcas y Caicos","Chad","Tierras Australes y Antárticas Francesas","Togo","Tailandia","Tayikistán","Tokelau","Timor Oriental","Turkmenistan","Túnez","Tonga","Turquía","Trinidad y Tobago","Tuvalu","Taiwán","Tanzania","Ucrania","Uganda","Isla Jarvis","Isla Baker","Isla Howland","Atolón Johnston","Islas Midway","Isla Wake","Estados Unidos","Uruguay","Uzbekistán","Ciudad del Vaticano","San Vicente y las Granadinas","Venezuela","Islas Vírgenes Británicas","Islas Vírgenes de EE.UU","Vietnam","Vanuatu","Wallis y Futuna","Samoa","Yemen","Mayotte","Sudáfrica","Zambia","Zimbabue"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);
        setTitle("Paises visitados");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Cargando paises...");
        progressDialog.show();

        uid = getIntent().getExtras().getString("uidOwner");

        firebaseDatabaseController = FirebaseDatabaseController.getInstance(getApplicationContext());

        getViews();
        setMap();
        initializeIds();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        firebaseDatabaseController.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void getPaisesVisitados() {
        paisesVisitados = new HashMap<>();
        firebaseDatabaseController.getCountriesVisited(uid, new FirebaseDatabaseController.GetCountriesResponse() {
            @Override
            public void success(HashMap<String, String> countries) {

                Iterator it = countries.entrySet().iterator();
                while(it.hasNext()) {
                    Map.Entry pair = (Map.Entry) it.next();
                    paisesVisitados.put(pair.getKey().toString(), pair.getValue().toString());
                    webView.loadUrl("javascript:addCountry('"+ pair.getValue().toString() +"', '"+ pair.getKey().toString() +"');");
                    it.remove();
                }
                fillList();
                progressDialog.dismiss();
            }

            @Override
            public void error() {
                fillList();

                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Error: No se han podido obtener los paises visitados por el usuario", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getViews() {
        webView = findViewById(R.id.webView);
        buscarPais = findViewById(R.id.buscarPais);
        listView = findViewById(R.id.countryList);
    }

    private void fillList() {
        adapter = new CountryListAdapter(this, nameList, paisesVisitados);
        listView.setAdapter(adapter);
    }

    private void setMap() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                Log.d("WEBCLIENT: ", "onPageStarted");
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WEBCLIENT: ", "onPageFinished");
                getPaisesVisitados();
            }
        });
        webView.setWebChromeClient(new WebChromeClient());
        webView.loadUrl("file:///android_asset/map.html");
    }

    private void initializeIds() {
        idList = Arrays.asList(ids);
        nameList = Arrays.asList(names);

        int size = idList.size();
        paisesId = new HashMap<>();

        for (int i = 0; i < size; ++i) {
            paisesId.put(nameList.get(i), idList.get(i));
        }

        buscarPais.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void setVisited(String title) {
        String id = paisesId.get(title);
        webView.loadUrl("javascript:addCountry('"+ id +"', '"+ title +"');");
        firebaseDatabaseController.setCountryVisited(uid, title, id);
    }

    public void setUnvisited(String title) {
        String id = paisesId.get(title);
        webView.loadUrl("javascript:deleteCountry('"+ id +"', '"+ title +"');");
        firebaseDatabaseController.deleteCountryVisited(uid, title);
    }
}
