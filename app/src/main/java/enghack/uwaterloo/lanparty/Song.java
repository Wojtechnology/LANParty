package enghack.uwaterloo.lanparty;


public class Song {
    private int id;
    private String title;
    private String artist;
    private String uri;

    public Song(int id, String title, String artist, String uri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.uri = uri;
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

    public String getUri() {
        return this.uri;
    }
}
