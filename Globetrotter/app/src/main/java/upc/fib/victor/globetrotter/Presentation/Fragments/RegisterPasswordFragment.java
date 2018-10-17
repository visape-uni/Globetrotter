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
 * {@link RegisterPasswordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterPasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterPasswordFragment extends Fragment {

    private EditText passwordTxt;

    private Button siguienteBtn;

    private OnFragmentInteractionListener mListener;

    public RegisterPasswordFragment() {
        // Required empty public constructor
    }

    public static RegisterPasswordFragment newInstance() {
        RegisterPasswordFragment fragment = new RegisterPasswordFragment();
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
        View view = inflater.inflate(R.layout.fragment_register_password, container, false);

        passwordTxt = view.findViewById(R.id.passwordTxt);
        siguienteBtn = view.findViewById(R.id.siguiente_btn);

        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordTxt.getText().toString().trim();
                if (password.length() < 8) {
                    //TODO: Show error message contraseña minimo 8 caractreres
                } else {
                    mListener.onSetPassword(password);
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
        void onSetPassword(String password);
    }
}
