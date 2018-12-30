package upc.fib.victor.globetrotter.Presentation.Fragments;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import upc.fib.victor.globetrotter.Controllers.FirebaseDatabaseController;
import upc.fib.victor.globetrotter.Domain.TripProposal;
import upc.fib.victor.globetrotter.R;


public class AddTripProposalFragment extends Fragment {

    private String[] names = new String[] {"Andorra","Emiratos Árabes Unidos","Afganistán","Antigua y Barbuda","Anguila","Albania","Armenia","Angola","Antártida","Argentina","Samoa Americana","Austria","Australia","Aruba","Islas Aland","Azerbaiyán","Bosnia y Herzegovina","Barbados","Bangladesh","Bélgica","Burkina Faso","Bulgaria","Bahrein","Burundi","Benin","San Bartolomé","Brunéi Darussalam","Bolivia","Islas Bermudas","Caribe Neerlandés","Brasil","Bahamas","Bután","Isla Bouvet","Botsuana","Bielorrusia","Belice","Canadá","Islas Cocos (Keeling)","República Democrática del Congo","República Centroafricana","República del Congo","Suiza","Costa de Margil","Islas Cook","Chile","Camerún","China","Colombia","Costa Rica","Cuba","Cabo Verde","Curazao","Isla de Navidad","Chipre","República Checa","Alemania","Yibuti","Dinamarca","Dominica","República Dominicana","Argelia","Ecuador","Estonia","Egipto","Sahara Occidental","Eritrea","España","Etiopia","Finlandia","Fiyi","Islas Malvinas","Estados Federados de Micronesia","Islas Feroe","Francia","Gabón","Reino Unido","Granada","Georgia","Guayana Francesa","Guernsey","Ghana","Gibraltar","Groenlandia","Gambia","Guinea","Islas Gloriosas","Guadaloupe","Guinea Ecuatorial","Grecia","Islas Georgias del Sur y Sandwich del Sur","Guatemala","Guam","Guinea-Bisáu","Guyana","Hong Kong","Islas Heard y McDonald","Honduras","Croacia","Haití","Hungría","Indonesia","Irlanda","Israel","Isla de Man","India","Territorio Británico del Océano Índico","Irak","Iran","Islandia","Italia","Jersey","Jamaica","Jordán","Japón","Isla Juan De Nova","Kenia","Kirguizstán","Camboya","Kiribati","Comoras","San Cristóbal","Corea del Norte","Corea del Sur","Kosovo","Kuwait","Islas Caimán","Kazajstán","Laos","Líbano","Santa Lucía","Liechtenstein","Sri Lanka","Liberia","Lesoto","Lituania","Luxemburgo","Letonia","Libia","Marruecos","Mónaco","Moldavia","Montenegro","San Martín","Madagascar","Islas Marshall","Macedonia","Mali","Myanmar","Mongolia","Macau","Islas Marinas del Norte","Martinica","Mauritania","Montserrat","Malta","Mauricio","Maldivas","Malawi","Mexico","Malasia","Mozambique","Namibia","Nueva Caledonia","Níger","Isla Norfolk","Nigeria","Nicaragua","Países Bajos","Noruega","Nepal","Nauru","Niue","Nueva Zelanda","Omám","Panamá","Perú","Polinesia Francesa","Papúa Nueva Guinea","Filipinas","Pakistán","Polonia","San Pedro y Miquelón","Islas Pitcairn","Puerto Rico","Territorios Palestinos","Portugal","Palaos","Paraguay","Catar","Reunión","Rumania","Serbia","Rusia","Ruanda","Arabia Saudita","Islas Salomón","Seychelles","Sudán","Suecia","Singapur","Santa Elena","Eslovenia","Svalbard y Jan Mayen","Eslovaquia","Sierra Leona","San Marino","Senegal","Somalia","Surinam","Sudán del Sur","Santo Tomé y Príncipe","El Salvador","San Martín","Siria","Suazilandia","Islas Turcas y Caicos","Chad","Tierras Australes y Antárticas Francesas","Togo","Tailandia","Tayikistán","Tokelau","Timor Oriental","Turkmenistan","Túnez","Tonga","Turquía","Trinidad y Tobago","Tuvalu","Taiwán","Tanzania","Ucrania","Uganda","Isla Jarvis","Isla Baker","Isla Howland","Atolón Johnston","Islas Midway","Isla Wake","Estados Unidos","Uruguay","Uzbekistán","Ciudad del Vaticano","San Vicente y las Granadinas","Venezuela","Islas Vírgenes Británicas","Islas Vírgenes de EE.UU","Vietnam","Vanuatu","Wallis y Futuna","Samoa","Yemen","Mayotte","Sudáfrica","Zambia","Zimbabue"};

    private String uid;

    private Boolean phoneContact;

    private EditText countryTxt;
    private EditText publicationTxt;
    private EditText budgetTxt;
    private EditText iniDateTxt;
    private EditText endDateTxt;
    private Button acceptTxt;
    private Button cancelTxt;
    private RadioButton emailRdBtn;
    private RadioButton phoneRdBtn;
    private EditText emailTxt;
    private EditText phoneTxt;
    private TextView phoneLbl;
    private TextView emailLbl;

    private FirebaseDatabaseController firebaseDatabaseController;

    public AddTripProposalFragment() {
        // Required empty public constructor
    }

    public static AddTripProposalFragment newInstance(String idUser) {
        AddTripProposalFragment fragment = new AddTripProposalFragment();
        Bundle args = new Bundle();
        args.putString("uid", idUser);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString("uid");
        }
        firebaseDatabaseController = FirebaseDatabaseController.getInstance();
        phoneContact = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trip_proposal, container, false);

        countryTxt = view.findViewById(R.id.countryEditTxt);
        publicationTxt = view.findViewById(R.id.publicationEditTxt);
        budgetTxt = view.findViewById(R.id.budgetEditTxt);
        iniDateTxt = view.findViewById(R.id.fechaInicioEditTxt);
        endDateTxt = view.findViewById(R.id.fechaFinEditTxt);
        acceptTxt = view.findViewById(R.id.publishBtn);
        cancelTxt = view.findViewById(R.id.cancelarBtn);
        emailRdBtn = view.findViewById(R.id.emailRadioBtn);
        emailTxt = view.findViewById(R.id.emailEditTxt);
        phoneRdBtn = view.findViewById(R.id.movilRadioBtn);
        phoneTxt = view.findViewById(R.id.movilEditTxt);
        phoneLbl = view.findViewById(R.id.movilTextView);
        emailLbl = view.findViewById(R.id.emailTextView);

        iniDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(iniDateTxt);
            }
        });

        endDateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(endDateTxt);
            }
        });
        countryTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builderSingle = new AlertDialog.Builder(getContext());
                builderSingle.setIcon(R.mipmap.ic_globe_logo);
                builderSingle.setTitle("Selecciona el país:");

                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.select_dialog_singlechoice);
                for (String name : names) {
                    arrayAdapter.add(name);
                }

                builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        final String strName = arrayAdapter.getItem(i);
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(getContext());
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Has seleccionado el siguiente país:");
                        builderInner.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                countryTxt.setText(strName);
                                dialogInterface.dismiss();
                            }
                        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        builderInner.show();
                    }
                });
                builderSingle.show();
            }
        });

        emailRdBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    emailLbl.setVisibility(View.VISIBLE);
                    emailTxt.setVisibility(View.VISIBLE);

                    phoneLbl.setVisibility(View.INVISIBLE);
                    phoneTxt.setVisibility(View.INVISIBLE);

                    phoneContact = false;
                }
            }
        });

        phoneRdBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    emailLbl.setVisibility(View.INVISIBLE);
                    emailTxt.setVisibility(View.INVISIBLE);

                    phoneLbl.setVisibility(View.VISIBLE);
                    phoneTxt.setVisibility(View.VISIBLE);

                    phoneContact = true;
                }
            }
        });

        acceptTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("PHONECONTACT: ", String.valueOf(phoneContact));
                Log.d("PHONECONTACT: ", phoneTxt.getText().toString().trim());
                Log.d("PHONECONTACT: ", emailTxt.getText().toString().trim());
                if(!iniDateTxt.getText().toString().trim().isEmpty() && !endDateTxt.getText().toString().trim().isEmpty() && !countryTxt.getText().toString().trim().isEmpty()
                        && ((phoneContact && !phoneTxt.getText().toString().trim().isEmpty()) || (!phoneContact && !emailTxt.getText().toString().trim().isEmpty()))) {

                    String ini = iniDateTxt.getText().toString();
                    String end = endDateTxt.getText().toString();

                    int day = Integer.valueOf(ini.substring(0,2));
                    int mon = Integer.valueOf(ini.substring(3,5)) - 1;
                    int year = Integer.valueOf(ini.substring(6,10));

                    Calendar iniCal = Calendar.getInstance();
                    iniCal.set(year, mon, day);
                    final Date iniDate = iniCal.getTime();

                    day = Integer.valueOf(end.substring(0,2));
                    mon = Integer.valueOf(end.substring(3,5)) - 1;
                    year = Integer.valueOf(end.substring(6,10));

                    Calendar endCal = Calendar.getInstance();
                    endCal.set(year, mon, day);
                    final Date endDate = endCal.getTime();

                    if(iniDate.getTime() <= endDate.getTime() && iniDate.getTime() >= Calendar.getInstance().getTime().getTime()) {

                        final ProgressDialog progressDialog = new ProgressDialog(getContext());
                        progressDialog.setIndeterminate(true);
                        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        progressDialog.setCancelable(false);
                        progressDialog.setMessage("Publicando viaje...");
                        progressDialog.show();

                        firebaseDatabaseController.getUserName(uid, new FirebaseDatabaseController.GetUserNameResponse() {
                            @Override
                            public void success(String userName) {
                                String message = publicationTxt.getText().toString();

                                int budget;
                                if (budgetTxt.getText().toString().isEmpty()) {
                                    budget = -1;
                                } else {
                                    budget = Integer.valueOf(budgetTxt.getText().toString());
                                }

                                String country = countryTxt.getText().toString();

                                String contact;
                                if (phoneContact) {
                                    contact = phoneTxt.getText().toString();
                                } else {
                                    contact = emailTxt.getText().toString();
                                }

                                TripProposal tripProposal = new TripProposal(message, uid, userName, Calendar.getInstance().getTime(), iniDate, endDate, budget, country, contact);
                                firebaseDatabaseController.storeTripProposal(tripProposal, new FirebaseDatabaseController.StorePublicationResponse() {
                                    @Override
                                    public void success() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Publicado correctamente", Toast.LENGTH_SHORT).show();
                                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                                    }

                                    @Override
                                    public void error() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getContext(), "Error publicando", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void error() {
                                progressDialog.dismiss();
                                Toast.makeText(getContext(), "Error publicando", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (iniDate.getTime() < Calendar.getInstance().getTime().getTime()) Toast.makeText(getContext(), "Debes insertar una fecha válida", Toast.LENGTH_SHORT).show();
                        else Toast.makeText(getContext(), "La fecha de regreso debe ser igual o mayor que la de inicio", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getContext(), "Debes rellenar todos los campos obligatorios", Toast.LENGTH_SHORT).show();
                }
            }
        });

        cancelTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStackImmediate();
                Toast.makeText(getContext(), "Propuesta no publicada", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void showDatePickerDialog(final EditText editText) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                final String selectedDate = twoDigits(day) + "/" + twoDigits(month+1) + "/" + year;
                editText.setText(selectedDate);
            }
        });
        newFragment.show(getActivity().getFragmentManager(), "datePicker");
    }

    private String twoDigits(int n) {
        return (n<9) ? ("0"+n) : String.valueOf(n);
    }

}
