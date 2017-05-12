package xyz.moviseries.moviseries.api_services;

import com.google.gson.JsonObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;
import xyz.moviseries.moviseries.MovieQualities;
import xyz.moviseries.moviseries.models.Capitulo;
import xyz.moviseries.moviseries.models.Category;
import xyz.moviseries.moviseries.models.Movie;
import xyz.moviseries.moviseries.models.Season;
import xyz.moviseries.moviseries.models.Serie;
import xyz.moviseries.moviseries.models.TopMovie;
import xyz.moviseries.moviseries.models.ViewMovie;
import xyz.moviseries.moviseries.models.ViewSerie;

/**
 * Created by DARWIN on 6/5/2017.
 */

public interface MoviseriesApiService {

    @FormUrlEncoded
    @POST("http://moviseries.xyz/android/login")
    Call<JsonObject> login(@Field("email") String email, @Field("password") String password);


    @GET
    Call<List<MovieQualities>> getLastMovies(@Url String url);


    @GET
    Call<List<Serie>> getLastSeries(@Url String url);


    @GET
    Call<List<Season>> getLastSeasons(@Url String url);


    @GET
    Call<List<TopMovie>> getTopMovies(@Url String url);


    @GET
    Call<ViewMovie> getMovie(@Url String url);


    @GET
    Call<ViewSerie> getSerie(@Url String url);

    @GET
    Call<List<Capitulo>> getSeason(@Url String url);


    @GET
    Call<List<Category>> getCategories(@Url String url);

}
