package upc.fib.victor.globetrotter.Presentation.Activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import upc.fib.victor.globetrotter.R;

public class UserMapActivity extends AppCompatActivity {

    private Button visitadoBtn;
    private WebView webView;
    private Spinner paisesSpinner;

    private HashMap<String, String> paisesId;
    private List<String> idList;
    private List<String> nameList;

    private String[] ids = new String[] {"AD","AE","AF","AG","AI","AL","AM","AO","AQ","AR","AS","AT","AU","AW","AX","AZ","BA","BB","BD","BE","BF","BG","BH","BI","BJ","BL","BN","BO","BM","BQ","BR","BS","BT","BV","BW","BY","BZ","CA","CC","CD","CF","CG","CH","CI","CK","CL","CM","CN","CO","CR","CU","CV","CW","CX","CY","CZ","DE","DJ","DK","DM","DO","DZ","EC","EE","EG","EH","ER","ES","ET","FI","FJ","FK","FM","FO","FR","GA","GB","GD","GE","GF","GG","GH","GI","GL","GM","GN","GO","GP","GQ","GR","GS","GT","GU","GW","GY","HK","HM","HN","HR","HT","HU","ID","IE","IL","IM","IN","IO","IQ","IR","IS","IT","JE","JM","JO","JP","JU","KE","KG","KH","KI","KM","KN","KP","KR","XK","KW","KY","KZ","LA","LB","LC","LI","LK","LR","LS","LT","LU","LV","LY","MA","MC","MD","ME","MF","MG","MH","MK","ML","MM","MN","MO","MP","MQ","MR","MS","MT","MU","MV","MW","MX","MY","MZ","NA","NC","NE","NF","NG","NI","NL","NO","NP","NR","UN","NZ","OM","PA","PE","PF","PG","PH","PK","PL","PM","PN","PR","PS","PT","PW","PY","QA","RE","RO","RS","RU","RW","SA","SB","SC","SD","SE","SG","SH","SI","SJ","SK","SL","SM","SN","SO","SR","SS","ST","SV","SX","SY","SZ","TC","TD","TF","TG","TH","TJ","TK","TL","TM","TN","TO","TR","TT","TV","TW","TZ","UA","UG","UM-DQ","UM-FQ","UM-HQ","UM-JQ","UM-MQ","UM_WQ","US","UY","UZ","VA","VC","VW","VG","VI","VN","VU","WF","WS","YE","YT","ZA","ZM","ZW"};
    private String[] names = new String[] {"Andorra","United Arab Emirates","Afghanistan","Antigua and Barbuda","Anguilla","Albania","Armenia","Angola","Antarctica","Argentina","American Samoa","Austria","Australia","Aruba","Aland Islands","Azerbaijan","Bosnia and Herzegovina","Barbados","Bangladesh","Belgium","Burkina Faso","Bulgaria","Bahrain","Burundi","Benin","Saint Barthelemy","Brunei Darussalam","Bolivia","Bermuda","Bonair, Saint Eustachius and Saba","Brazil","Bahamas","Bhutan","Bouvet Island","Botswana","Belarus","Beliza","Canada","Cocos (Keeling) Islands","Democratic Republic of Congo","Central African Republic","Republic of Congo","Switzerland","Côte d'Ivoire","Cook Islands","Chile","Cameroon","China","Colombia","Costa Rica","Cuba","Cape Verde","Curaçao","Christmas Island","Cyprus","Czech Republic","Germany","Djibouti","Denmark","Dominica","Dominican Republic","Algeria","Ecuador","Estonia","Egypt","Western Sahara","Eritrea","Spain","Ethiopia","Finland","Fiji","Falkland Islands","Federated States of Micronesia","Faroe Islands","France","Gabon","United Kingdom","Grenada","Georgia","French Guiana","Guernsey","Ghana","Gibraltar","Greenland","Gambia","Guinea","Glorioso Islands","Guadaloupe","Equatorial Guinea","Greece","South Georgia and South Sandwich Islands","Guatemala","Guam","Guinea-Bissau","Guyana","Hong Kong","Heard Island and McDonald Islands","Honduras","Croatia","Haiti","Hungary","Indonesia","Ireland","Israel","Isle of Man","India","British Indian Ocean Territory","Iraq","Iran","Iceland","Italy","Jersey","Jamaica","Jordan","Japan","Juan De Nova Island","Kenya","Kyrgyzstan","Cambodia","Kiribati","Comoros","Saint Kitts","North Korea","South Korea","Kosovo","Kuwait","Cayman Islands","Kazakhstan","Lao People's Democratic Republic","Lebanon","Saint Lucia","Liechtenstein","Sri Lanka","Liberia","Lesotho","Lithuania","Luxembourg","Latvia","Libya","Morocco","Monaco","Moldova","Montenegro","Saint Martin","Madagascar","Marshall Islands","Macedonia","Mali","Myanmar","Mongolia","Macau","Nothern Mariana Islands","Martinique","Mauritania","Montserrat","Malta","Mauritius","Maldives","Malawi","Mexico","Malaysia","Mozambique","Namibia","New Caledonia","Niger","Norfolk Island","Nigeria","Nicaragua","Netherlands","Norway","Nepal","Nauru","Niue","New Zealand","Oman","Panama","Peru","French Polynesia","Papua New Guinea","Philippines","Pakistan","Poland","Saint Pierre and Miquelon","Pitcairn Islands","Puerto Rico","Palestinian Territories","Portugal","Palau","Paraguay","Qatar","Reunion","Romania","Serbia","Russia","Rwanda","Saudi Arabia","Solomon Islands","Seychelles","Sudan","Sweden","Singapore","Saint Helena","Slovenia","Svalbard and Jan Mayen","Slovakia","Sierra Leone","San Marino","Senegal","Somalia","Suriname","South Sudan","Sao Tome and Principe","El Salvador","Sint Maarten","Syria","eSwatini","Turks and Caicos Islands","Chad","French Southern and Antarctic Lands","Togo","Thailand","Tajikistan","Tokelau","Timor-Leste","Turkmenistan","Tunisia","Tonga","Turkey","Trinidad and Tobago","Tuvalu","Taiwan","Tanzania","Ukraine","Uganda","Jarvis Island","Baker Island","Howland Island","Johnston Atoll","Midway Islands","Wake Island","United States","Uruguay","Uzbekistan","Vatican City","Saint Vincent and the Grenadines","Venezuela","British Virgin Islands","US Virgin Islands","Vietnam","Vanatu","Wallis and Futuna","Samoa","Yemen","Mayotte","South Africa","Zambia","Zimbabwe"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_map);

        getViews();
        setMap();
        initializeIds();

        visitadoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = paisesSpinner.getSelectedItem().toString();
                String id = paisesId.get(title);
                webView.loadUrl("javascript:addCountry('"+ id +"', '"+ title +"');");
            }
        });
    }

    private void getViews() {
        webView = findViewById(R.id.webView);
        visitadoBtn = findViewById(R.id.buttonVisitado);
        paisesSpinner = findViewById(R.id.paisesSpinner);
    }

    private void setMap() {
        webView.getSettings().setJavaScriptEnabled(true);
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, nameList);
        adapter.setDropDownViewResource(android.R.layout.select_dialog_item);
        paisesSpinner.setAdapter(adapter);
    }

}
