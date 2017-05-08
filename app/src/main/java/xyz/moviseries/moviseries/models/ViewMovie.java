package xyz.moviseries.moviseries.models;

import java.util.List;

/**
 * Created by DARWIN on 8/5/2017.
 */

public class ViewMovie {
    private MovieScore movie;
    private List<UrlOnline> urls;
    private List<MEGAUrl> mega_urls;

    public MovieScore getMovie() {
        return movie;
    }

    public List<UrlOnline> getUrls() {
        return urls;
    }

    public List<MEGAUrl> getMega_urls() {
        return mega_urls;
    }
}
