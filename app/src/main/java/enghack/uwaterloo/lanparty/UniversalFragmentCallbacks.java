package enghack.uwaterloo.lanparty;

/**
 * Created by wojtekswiderski on 15-06-26.
 */
public interface UniversalFragmentCallbacks {
    public void onNavigationDrawerItemSelected(int position);
    public void onStateChanged(int state, String ip);
    public void onMaster(Masterbater master);
    public void setQueueAdapter(QueueAdapter adapter);

    public int getState();
    public String getIp();
    public Masterbater getMaster();
}
