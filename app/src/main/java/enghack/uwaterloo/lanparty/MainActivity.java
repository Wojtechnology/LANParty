package enghack.uwaterloo.lanparty;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;


public class MainActivity extends ActionBarActivity
        implements UniversalFragmentCallbacks {

    private NavigationDrawerFragment mNavigationDrawerFragment;

    public static int DISCONNECTED = 0;
    public static int CONNECTED = 1;
    public static int MASTER = 2;

    private int mState = DISCONNECTED;
    private String mIp = "";
    private Masterbater mMasterbater;
    private Button mPlayButton;
    private Button mNextButton;
    private LinearLayout mPlayHint;

    private CharSequence mTitle;

    public QueueAdapter mQueueAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        onSectionAttached(1);

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        mPlayButton = (Button) findViewById(R.id.play_hint_button);
        mNextButton = (Button) findViewById(R.id.next_hint_button);
        mPlayHint = (LinearLayout) findViewById(R.id.play_hint);
        mPlayHint.setVisibility(View.INVISIBLE);

        mPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mState == MASTER && mMasterbater != null){
                    boolean isPlaying = mMasterbater.togglePlay();
                    if (isPlaying){
                        mPlayButton.setBackgroundResource(R.drawable.ic_pause_hint);
                    } else {
                        mPlayButton.setBackgroundResource(R.drawable.ic_play_hint);
                    }
                }
            }
        });

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mState == MASTER && mMasterbater != null){
                    mMasterbater.playNext();
                }
            }
        });
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        onSectionAttached(position + 1);
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new SettingsFragment();
                break;

            case 1:
                fragment = new QueueFragment();
                break;

            default:
                fragment = new MyMusicFragment();
        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    @Override
    public void onStateChanged(int state, String ip) {
        mState = state;
        mIp = ip;
        if (state == MASTER) {
            mPlayHint.setVisibility(View.VISIBLE);
        }else{
            mPlayHint.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onMaster(Masterbater master) {
        mMasterbater = master;
    }

    @Override
    public int getState() {
        return mState;
    }

    @Override
    public String getIp() {
        return mIp;
    }

    @Override
    public Masterbater getMaster() {
        return mMasterbater;
    }

    @Override
    public void setQueueAdapter(QueueAdapter adapter){
        mQueueAdapter = adapter;
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for(File file : getFilesDir().listFiles()) {
            Log.e("Deleting", file.getName());
            deleteFile(file.getName());
        }
        Log.e("Length", getFilesDir().listFiles().length + "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setBottomBar(String artist, String title) {
        mPlayButton.setBackgroundResource(R.drawable.ic_pause_hint);
        TextView hintTitle = (TextView) findViewById(R.id.hint_title);
        TextView hintArtist = (TextView) findViewById(R.id.hint_artist);
        hintTitle.setText(title);
        hintArtist.setText(artist);
    }
}
