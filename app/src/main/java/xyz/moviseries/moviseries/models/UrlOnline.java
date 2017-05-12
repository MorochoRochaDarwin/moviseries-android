package xyz.moviseries.moviseries.models;

/**
 * Created by DARWIN on 8/5/2017.
 */

public class UrlOnline {

    private String url_id, file_id, quality, server, language_name;

    public UrlOnline(String url_id, String file_id, String quality, String server, String language_name) {
        this.url_id = url_id;
        this.file_id = file_id;
        this.quality = quality;
        this.server = server;
        this.language_name = language_name;
    }

    public String getUrl_id() {
        return url_id;
    }

    public String getFile_id() {
        return file_id;
    }

    public String getQuality() {
        return quality;
    }

    public String getServer() {
        return server;
    }

    public String getLanguage_name() {
        return language_name;
    }
}
