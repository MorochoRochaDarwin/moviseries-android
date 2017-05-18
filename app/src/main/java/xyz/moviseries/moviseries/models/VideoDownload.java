package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 14/5/2017.
 */

public class VideoDownload {
    private String id,fileName, url;

    public VideoDownload(String id, String fileName, String url) {
        this.id = id;
        this.fileName = fileName;
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
