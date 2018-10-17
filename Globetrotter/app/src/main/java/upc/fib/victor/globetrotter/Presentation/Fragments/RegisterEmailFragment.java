package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import upc.fib.victor.globetrotter.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterEmailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterEmailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterEmailFragment extends Fragment {

    private Button siguienteBtn;

    private EditText correoTxt;

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
        siguienteBtn = view.findViewById(R.id.siguiente_btn);

        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String correo = correoTxt.getText().toString().trim();
                if (correo.equals("") || !correo.contains("@")) {
                    //TODO: Show error message
                } else {
                    mListener.onSetCorreo(correo);
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
        // TODO: Update argument type and name
        void onSetCorreo(String correo);
    }
}
