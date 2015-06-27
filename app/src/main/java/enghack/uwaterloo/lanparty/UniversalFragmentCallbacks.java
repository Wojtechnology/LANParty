package enghack.uwaterloo.lanparty;

/**
 * Created by wojtekswiderski on 15-06-26.
 */
public interface UniversalFragmentCallbacks {
    public void onNavigationDrawerItemSelected(int position);
    public void onCreateServer(int position);
    public void onConnectServer(int position);
    public void onAddSong(int position);
    public void onDeleteSong(int position);
}
