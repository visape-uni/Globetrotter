package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import upc.fib.victor.globetrotter.R;

public class RegisterEmailFragment extends Fragment {

    private Button siguienteBtn;

    private AlertDialog dialog;

    private TextView terminosTxt;

    private CheckBox checkBox;

    private EditText correoTxt;
    private EditText passwordTxt;
    private EditText passwordTxt2;

    private OnFragmentInteractionListener mListener;

    public RegisterEmailFragment() {
        // Required empty public constructor
    }

    public static RegisterEmailFragment newInstance() {
        RegisterEmailFragment fragment = new RegisterEmailFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register_correo, container, false);

        correoTxt = view.findViewById(R.id.correoTxt);
        passwordTxt = view.findViewById(R.id.passwordTxt);
        passwordTxt2 = view.findViewById(R.id.password2Txt);
        siguienteBtn = view.findViewById(R.id.siguiente_btn);
        terminosTxt = view.findViewById(R.id.linkTerminos);
        checkBox = view.findViewById(R.id.terminos);

        terminosTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                LayoutInflater inflater = getLayoutInflater();

                View v = inflater.inflate(R.layout.dialog_terminos_y_condiciones, null);
                builder.setView(v);

                ImageView closeBtn = v.findViewById(R.id.closeBtn);
                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                dialog = builder.create();
                dialog.show();
            }
        });

        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkBox.isChecked()) {
                    String correo = correoTxt.getText().toString().trim();

                    if (correo.equals("") || !correo.contains("@")) {
                        Toast.makeText(getContext(), "Correo electrónico no válido",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        String password = passwordTxt.getText().toString().trim();
                        String password2 = passwordTxt2.getText().toString().trim();
                        if (password.length() < 8) {
                            Toast.makeText(getContext(), "La contraseña debe tener más de 8 caracteres",
                                    Toast.LENGTH_SHORT).show();
                        } else if (!password.equals(password2)) {
                            Toast.makeText(getContext(), "Las contraseñas deben coincidir",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            mListener.onSetCorreo(correo, password);
                        }
                    }
                } else {
                    Toast.makeText(getContext(), "Debes aceptar los términos y condiciones",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        void onSetCorreo(String correo, String password);
    }
}
