package enghack.uwaterloo.lanparty;

/**
 * Created by wojtekswiderski on 15-06-26.
 */
public interface UniversalFragmentCallbacks {
    public void onNavigationDrawerItemSelected(int position);
    public void onStateChanged(int state, String ip);
    public void onAddSong(int position);
    public void onDeleteSong(int position);

    public int getState();
    public String getIp();
}
