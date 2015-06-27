package enghack.uwaterloo.lanparty;


public class Song {
    private int id;
    private String title;
    private String artist;

    public Song(int id, String title, String artist) {
        this.id = id;
        this.title = title;
        this.artist = artist;
    }

    public int getID() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getArtist() {
        return this.artist;
    }
}
