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
    private String[] names = new String[] {"Andorra","United Arab Emirates","Afghanistan","Antigua and Barbuda","Anguilla","Albania","Armenia","Angola","Antarctica","Argentina","American Samoa","Austria","Australia","Aruba","Aland Islands","Azerbaijan","Bosnia and Herzegovina","Barbados","Bangladesh","Belgium","Burkina Faso","Bulgaria","Bahrain","Burundi","Benin","Saint Barthelemy","Brunei Darussalam","Bolivia","Bermuda","Bonair, Saint Eustachius and Saba","Brazil","Bahamas","Bhutan","Bouvet Island","Botswana","Belarus","Beliza","Canada","Cocos (Keeling) Islands","Democratic Republic of Congo","Central African Republic","Republic of Congo","Switzerland","Côte d'Ivoire","Cook Islands","Chile","Cameroon","China","Colombia","Costa Rica","Cuba","Cape Verde","Curaçao","Christmas Island","Cyprus","Czech Republic","Germany","Djibouti","Denmark","Dominica","Dominican Republic","Algeria","Ecuador","Estonia","Egypt","Western Sahara","Eritrea","Spain","Ethiopia","Finland","Fiji","Falkland Islands","Federated States of Micronesia","Faroe Islands","France","Gabon","United Kingdom","Grenada","Georgia","French Guiana","Guernsey","Ghana","Gibraltar","Greenland","Gambia","Guinea","Glorioso Islands","Guadaloupe","Equatorial Guinea","Greece","South Georgia and South Sandwich Islands","Guatemala","Guam","Guinea-Bissau","Guyana","Hong Kong","Heard Island and McDonald Islands","Honduras","Croatia","Haiti","Hungary","Indonesia","Ireland","Israel","Isle of Man","India","British Indian Ocean Territory","Iraq","Iran","Iceland","Italy","Jersey","Jamaica","Jordan","Japan","Juan De Nova Island","Kenya","Kyrgyzstan","Cambodia","Kiribati","Comoros","Saint Kitts","North Korea","South Korea","Kosovo","Kuwait","Cayman Islands","Kazakhstan","Lao People's Democratic Republic","Lebanon","Saint Lucia","Liechtenstein","Sri Lanka","Liberia","Lesotho","Lithuania","Luxembourg","Latvia","Libya","Morocco","Monaco","Moldova","Montenegro","Saint Martin","Madagascar","Marshall Islands","Macedonia","Mali","Myanmar","Mongolia","Macau","Nothern Mariana Islands","Martinique","Mauritania","Montserrat","Malta","Mauritius","Maldives","Malawi","Mexico","Malaysia","Mozambique","Namibia","New Caledonia","Niger","Norfolk Island","Nigeria","Nicaragua","Netherlands","Norway","Nepal","Nauru","Niue","New Zealand","Oman","Panama","Peru","French Polynesia","Papua New Guinea","Philippines","Pakistan","Poland","Saint Pierre and Miquelon","Pitcairn Islands","Puerto Rico","Palestinian Territories","Portugal","Palau","Paraguay","Qatar","Reunion","Romania","Serbia","Russia","Rwanda","Saudi Arabia","Solomon Islands","Seychelles","Sudan","Sweden","Singapore","Saint Helena","Slovenia","Svalbard and Jan Mayen","Slovakia","Sierra Leone","San Marino","Senegal","Somalia","Suriname","South Sudan","Sao Tome and Principe","El Salvador","Sint Maarten","Syria","eSwatini","Turks and Caicos Islands","Chad","French Southern and Antarctic Lands","Togo","Thailand","Tajikistan","Tokelau","Timor-Leste","Turkmenistan","Tunisia","Tonga","Turkey","Trinidad and Tobago","Tuvalu","Taiwan","Tanzania","Ukraine","Uganda","Jarvis Island","Baker Island","Howland Island","Johnston Atoll","Midway Islands","Wake Island","United States","Uruguay","Uzbekistan","Vatican City","Saint Vincent and the Grenadines","Venezuela","British Virgin Islands","US Virgin Islands","Vietnam","Vanatu","Wallis and Futuna","Samoa","Yemen","Mayotte","South Africa","Zambia","Zimbabwe"};

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

        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        uid = sharedPreferences.getString("uid", null);

        firebaseDatabaseController = FirebaseDatabaseController.getInstance();

        getViews();
        setMap();
        initializeIds();
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
