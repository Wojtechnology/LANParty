package enghack.uwaterloo.lanparty;


import java.io.File;
import java.net.URL;

public class Song {
    private int id;
    private String title;
    private String artist;
    private String uri;
    private File file;

    public Song(int id, String title, String artist, String uri) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.uri = uri;
    }

    public void setFile(File newFile){
        file = newFile;
    }

    public File getFile(){
        return file;
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
