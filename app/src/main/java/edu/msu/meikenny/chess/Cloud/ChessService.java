package edu.msu.meikenny.chess.Cloud;

import static edu.msu.meikenny.chess.Cloud.Cloud.CATALOG_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.DELETE_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.JOIN_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.LOAD_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.LOGIN_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.NEW_GAME_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.SAVE_GAME_PATH;
import static edu.msu.meikenny.chess.Cloud.Cloud.SIGN_UP_PATH;

import edu.msu.meikenny.chess.Cloud.Models.Catalog;
import edu.msu.meikenny.chess.Cloud.Models.Delete;
import edu.msu.meikenny.chess.Cloud.Models.Join;
import edu.msu.meikenny.chess.Cloud.Models.Load;
import edu.msu.meikenny.chess.Cloud.Models.Login;
import edu.msu.meikenny.chess.Cloud.Models.NewGame;
import edu.msu.meikenny.chess.Cloud.Models.SaveGame;
import edu.msu.meikenny.chess.Cloud.Models.SignUp;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChessService {
    @GET(CATALOG_PATH)
    Call<Catalog> getCatalog(
            @Query("magic") String magic
    );

    @GET(LOGIN_PATH)
    Call<Login> login(
            @Query("user") String userId,
            @Query("magic") String magic,
            @Query("pw") String password
    );

    @FormUrlEncoded
    @POST(SIGN_UP_PATH)
    Call<SignUp> signUp(
            @Field("user") String userId,
            @Field("magic") String magic,
            @Field("pw") String password
    );

    @GET(LOAD_PATH)
    Call<Load> load(
            @Query("magic") String magic,
            @Query("id") String catId
    );

    @GET(JOIN_PATH)
    Call<Join> join(
            @Query("id") String catId,
            @Query("user") String p2
    );

    @FormUrlEncoded
    @POST(NEW_GAME_PATH)
    Call<NewGame> newGame(
            @Field("user") String player1
    );

    @FormUrlEncoded
    @POST(SAVE_GAME_PATH)
    Call<SaveGame> saveGame(
            @Field("id") String gameId,
            @Field("xml") String gameXml
    );

    @GET(DELETE_PATH)
    Call<Delete> deleteGame(
            @Query("id") String catId
    );
}
