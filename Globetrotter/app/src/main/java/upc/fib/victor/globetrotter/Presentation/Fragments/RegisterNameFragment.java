package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import upc.fib.victor.globetrotter.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterNameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterNameFragment extends Fragment {

    private EditText nombreTxt;
    private EditText apellidosTxt;

    private Button siguienteBtn;

    private OnFragmentInteractionListener mListener;

    public RegisterNameFragment() {
        // Required empty public constructor
    }

    public static RegisterNameFragment newInstance() {
        RegisterNameFragment fragment = new RegisterNameFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register_name, container, false);

        nombreTxt = view.findViewById(R.id.nombreTxt);
        apellidosTxt = view.findViewById(R.id.apellidosTxt);
        siguienteBtn = view.findViewById(R.id.siguiente_btn);

        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nombre = nombreTxt.getText().toString().trim();
                String apellidos = apellidosTxt.getText().toString().trim();
                if (nombre.equals("") || apellidos.equals("")) {
                    Toast.makeText(getContext(), "Debes introducir tu nombre y apellidos",
                            Toast.LENGTH_SHORT).show();
                } else {
                    mListener.onSetNombreApellidos(nombre, apellidos);
                }
            }
        });

        // Inflate the layout for this fragment
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
        // TODO: Update argument type and name
        void onSetNombreApellidos(String nombre, String apellidos);
    }
}
