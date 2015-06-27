package enghack.uwaterloo.lanparty;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyMusicFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyMusicFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyMusicFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private UniversalFragmentCallbacks mCallbacks;
    private ListView songListView;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyMusicFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyMusicFragment newInstance(String param1, String param2) {
        MyMusicFragment fragment = new MyMusicFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }
    public MyMusicFragment() {
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
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music, container, false);

        songListView = (ListView) view.findViewById(R.id.my_music_song_list);

        ArrayList<Song> songList = new ArrayList<Song>();
        ContentResolver musicResolver = getActivity().getContentResolver();
        Uri musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor musicCursor = musicResolver.query(musicUri,null, null, null, null);

        if(musicCursor!=null && musicCursor.moveToFirst()){
            int titleColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.TITLE);
            int idColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media._ID);
            int artistColumn = musicCursor.getColumnIndex
                    (android.provider.MediaStore.Audio.Media.ARTIST);
            int dataColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                int thisId = musicCursor.getInt(idColumn);
                String thisTitle = musicCursor.getString(titleColumn);
                String thisArtist = musicCursor.getString(artistColumn);
                String thisUri = musicCursor.getString(dataColumn);
                songList.add(new Song(thisId, thisTitle, thisArtist, thisUri));
            } while (musicCursor.moveToNext());
        }

        SongListAdapter adapter = new SongListAdapter(getActivity(), songList);
        songListView.setAdapter(adapter);

        songListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song clickedSong = (Song) parent.getAdapter().getItem(position);
                MainActivity main = (MainActivity) getActivity();

                if (main.getState() == main.CONNECTED) {
                    final AsyncHttpClient songClient = new AsyncHttpClient();
                    RequestParams params = new RequestParams();
                    JSONObject jo = new JSONObject();
                    try {
                        File songFile = new File(clickedSong.getUri());
                        byte[] bFile = new byte[(int) songFile.length()];
                        InputStream inputStream = new FileInputStream(songFile);
                        inputStream.read(bFile);
                        inputStream.close();
                        //songFile.read()
                        params.put("song", songFile);
                        jo.put("artist", clickedSong.getArtist());
                        jo.put("title", clickedSong.getTitle());
                    } catch (FileNotFoundException e) {
                        Log.e("Error", "FNFE");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String url = null;
                    try {
                        url = "http://" + main.getIp() + ":8080/queue/" + URLEncoder.encode(jo.toString(), "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    };

                    final ProgressDialog barProgressDialog = new ProgressDialog(getActivity());
                    barProgressDialog.setTitle("Uploading Song");
                    barProgressDialog.setMessage("Adding song to queue");
                    barProgressDialog.setProgressStyle(barProgressDialog.STYLE_HORIZONTAL);
                    barProgressDialog.setProgress(0);
                    barProgressDialog.setMax(100);
                    barProgressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            songClient.cancelRequests(getActivity(), true);
                        }
                    });
                    barProgressDialog.show();

                    songClient.post(getActivity(), url, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Log.e("Success", String.valueOf(statusCode));
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Log.e("Failure", error.toString());
                        }

                        @Override
                        public void onCancel() {
                            super.onCancel();
                            barProgressDialog.hide();
                        }

                        @Override
                        public void onProgress(int bytesWritten, int totalSize) {
                            super.onProgress(bytesWritten, totalSize);
                            barProgressDialog.setProgress(100 * bytesWritten / totalSize);
                            if (bytesWritten == totalSize)
                                barProgressDialog.hide();
                        }
                    });
                }
            }
        });
        return view;
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
