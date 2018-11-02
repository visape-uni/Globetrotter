package upc.fib.victor.globetrotter.Presentation.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.Spinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import upc.fib.victor.globetrotter.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterBornDateFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterBornDateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterBornDateFragment extends Fragment {

    private Spinner daySpinner, monthSpinner, yearSpinner;

    private Button siguienteBtn;

    private OnFragmentInteractionListener mListener;

    public RegisterBornDateFragment() {
        // Required empty public constructor
    }

    public static RegisterBornDateFragment newInstance() {
        RegisterBornDateFragment fragment = new RegisterBornDateFragment();
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
        View view = inflater.inflate(R.layout.fragment_register_nacimiento, container, false);

        siguienteBtn = view.findViewById(R.id.siguiente_btn);
        daySpinner = view.findViewById(R.id.daySpinner);
        monthSpinner = view.findViewById(R.id.monthSpinner);
        yearSpinner = view.findViewById(R.id.yearSpinner);


        ArrayList<String> years = new ArrayList<String>();
        int thisYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = thisYear; i >= 1900; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, years);
        yearSpinner.setAdapter(adapter);


        siguienteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.YEAR, Integer.valueOf(yearSpinner.getSelectedItem().toString()));
                cal.set(Calendar.MONTH, Integer.valueOf(monthSpinner.getSelectedItem().toString()) - 1);
                cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(daySpinner.getSelectedItem().toString()));
                mListener.onSetNacimiento(cal.getTime());
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
        void onSetNacimiento(Date date);
    }

}
