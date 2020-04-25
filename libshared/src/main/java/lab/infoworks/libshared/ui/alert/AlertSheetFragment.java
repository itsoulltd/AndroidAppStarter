package lab.infoworks.libshared.ui.alert;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import lab.infoworks.libshared.R;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlertSheetFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlertSheetFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlertSheetFragment extends BottomSheetDialogFragment {
    //
    private static final String REFERENCE_CODE = "ref_code";
    private static final String MESSAGE = "";

    private Button goBtn;
    private TextView textView;

    private int refCode;
    private String message;

    private OnFragmentInteractionListener mListener;

    public AlertSheetFragment() { /*Required empty public constructor*/}

    public static AlertSheetFragment newInstance(int refCode, String message) {
        AlertSheetFragment fragment = new AlertSheetFragment();
        Bundle args = new Bundle();
        args.putInt(REFERENCE_CODE, refCode);
        args.putString(MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            refCode = getArguments().getInt(REFERENCE_CODE);
            message = getArguments().getString(MESSAGE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alert_sheet, container, false);
        goBtn = view.findViewById(R.id.go_btn);
        textView = view.findViewById(R.id.message_tv);
        textView.setText(message);
        goBtn.setOnClickListener(v -> {
            this.dismiss();
            mListener.onBottomSheetButtonClick(refCode);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onBottomSheetButtonClick(int refCode);
    }

}
