package enghack.uwaterloo.lanparty;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.dd.processbutton.iml.ActionProcessButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private CircularProgressButton mCreateButton;
    private TextView mIpDisplay;
    private EditText mIpEdit;
    private ActionProcessButton mConnectButton;

    private UniversalFragmentCallbacks mCallbacks;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mIpDisplay = (TextView) getView().findViewById(R.id.ip_display);
        mIpEdit = (EditText) getView().findViewById(R.id.ip_edit);
        mCreateButton = (CircularProgressButton) getView().findViewById(R.id.create_button);
        mCreateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int state = mCallbacks.getState();
                String ip = mCallbacks.getIp();

                if (state == MainActivity.CONNECTED) {
                    resetUI(MainActivity.DISCONNECTED, "");
                } else {
                    // Start running server here
                    resetUI(MainActivity.MASTER, Utils.getIPAddress(true));
                }
            }
        });
        mConnectButton = (ActionProcessButton) getView().findViewById(R.id.connect_button);
        mConnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectButton.setMode(ActionProcessButton.Mode.ENDLESS);
                mConnectButton.setProgress(1);
                mIpEdit.setEnabled(false);
                mConnectButton.setEnabled(false);
                new ConnectTask().execute(mIpEdit.getText().toString());
            }
        });

        int state = mCallbacks.getState();
        String ip = mCallbacks.getIp();

        Log.e("Settings", "onViewCreated " + ip);

        resetUI(state, ip);
    }

    private void resetUI(int state, String ip) {
        mCallbacks.onStateChanged(state, ip);
        if(state == MainActivity.DISCONNECTED) {
            mIpEdit.setEnabled(true);
            mConnectButton.setProgress(0);
            mCreateButton.setProgress(0);
            mConnectButton.setText("Connect");
            mCreateButton.setText("Start a server...");
            mConnectButton.setEnabled(true);
            mCreateButton.setEnabled(true);
            mIpDisplay.setText("Not running...");
        } else if(state == MainActivity.CONNECTED) {
            mIpEdit.setEnabled(false);
            mConnectButton.setProgress(100);
            mCreateButton.setProgress(0);
            mCreateButton.setText("Disconnect");
            mConnectButton.setEnabled(false);
            mCreateButton.setEnabled(true);
            mIpDisplay.setText("Connected to " + mCallbacks.getIp());
        } else {
            mIpEdit.setEnabled(false);
            mConnectButton.setProgress(0);
            mCreateButton.setProgress(100);
            mConnectButton.setText("Shutdown server...");
            mConnectButton.setEnabled(true);
            mCreateButton.setEnabled(false);
            mIpDisplay.setText("Running on " + mCallbacks.getIp());
        }
    }

    private class ConnectTask extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL obj = new URL("http", strings[0], 8080, "/connect");
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();

                con.setRequestMethod("GET");

                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                if (response.toString().equals("Connected!")) {
                    return true;
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                resetUI(MainActivity.CONNECTED, mIpEdit.getText().toString());
            } else {
                resetUI(MainActivity.DISCONNECTED, "");
            }
        }
    };

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (UniversalFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
