package edu.msu.meikenny.chess.Cloud;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import edu.msu.meikenny.chess.Chess;
import edu.msu.meikenny.chess.Cloud.Models.Catalog;
import edu.msu.meikenny.chess.Cloud.Models.ChessUser;
import edu.msu.meikenny.chess.Cloud.Models.Delete;
import edu.msu.meikenny.chess.Cloud.Models.Item;
import edu.msu.meikenny.chess.Cloud.Models.Join;
import edu.msu.meikenny.chess.Cloud.Models.Load;
import edu.msu.meikenny.chess.Cloud.Models.Login;
import edu.msu.meikenny.chess.Cloud.Models.NewGame;
import edu.msu.meikenny.chess.Cloud.Models.SaveGame;
import edu.msu.meikenny.chess.Cloud.Models.SignUp;
import edu.msu.meikenny.chess.R;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;

@SuppressWarnings("deprecation")
public class Cloud {
    private static final String MAGIC = "NechAtHa6RuzeR8x";
    private static final String USER = "";
    private static final String PASSWORD = "";

    // USED FOR TESTING, CHANGE TO: https://webdev.cse.msu.edu/~rajend23/cse476/project2/
    private static final String BASE_URL = "https://webdev.cse.msu.edu/~rajend23/cse476/project2/";
    public static final String CATALOG_PATH = "chess-cat.php";
    public static final String JOIN_PATH = "chess-joingame.php";
    public static final String DELETE_PATH = "chess-delete.php";
    public static final String LOAD_PATH = "chess-loadboard.php";
    public static final String LOGIN_PATH = "chess-login.php";
    public static final String SIGN_UP_PATH = "chess-signup.php";
    public static final String NEW_GAME_PATH = "chess-newgame.php";
    public static final String SAVE_GAME_PATH = "chess-saveboard.php";
    private static final String UTF8 = "UTF-8";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build();

    public static class CatalogAdapter extends BaseAdapter {
        /**
         * The items we display in the list box. Initially this is
         * null until we get items from the server.
         */
        private Catalog catalog = new Catalog("", new ArrayList(), "");

        /**
         * Constructor
         */
        public CatalogAdapter(final View view) {
            // Create a thread to load the catalog
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        catalog = getCatalog();

                        if (catalog.getStatus().equals("no")) {
                            String msg = "Loading catalog returned status 'no'! Message is = '" + catalog.getMessage() + "'";
                            throw new Exception(msg);
                        }
                        if (catalog.getItems().isEmpty()) {
                            String msg = "Catalog does not contain any existing games.";
                            throw new Exception(msg);
                        }

                        view.post(new Runnable() {

                            @Override
                            public void run() {
                                // Tell the adapter the data set has been changed
                                notifyDataSetChanged();
                            }

                        });
                    } catch (Exception e) {
                        // Error condition! Something went wrong
                        Log.e("CatalogAdapter", "Something went wrong when loading the catalog", e);
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                String string;
                                // make sure that there is a message in the catalog
                                // if there isn't use the message from the exception
                                if (catalog.getMessage() == null) {
                                    string = e.getMessage();
                                } else {
                                    string = catalog.getMessage();
                                }
                                Toast.makeText(view.getContext(), string, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }).start();
        }

        // Create a GET query
        public Catalog getCatalog() throws IOException, RuntimeException {
            ChessService service = retrofit.create(ChessService.class);

            Response<Catalog> response = service.getCatalog(MAGIC).execute();
            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("getCatalog", "Failed to get catalog, response code is = " + response.code());
                return new Catalog("no", new ArrayList<Item>(), "Server error " + response.code());
            }
            Catalog catalog = response.body();
            if (catalog.getStatus().equals("no")) {
                String string = "Failed to get catalog, msg is = " + catalog.getMessage();
                Log.e("getCatalog", string);
                return new Catalog("no", new ArrayList<Item>(), string);
            }
            if (catalog.getItems() == null) {
                catalog.setItems(new ArrayList<Item>());
            }
            return catalog;
        }

        @Override
        public int getCount() {
            return catalog.getItems().size();
        }

        @Override
        public Item getItem(int position) {
            return catalog.getItems().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if(view == null) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.catalog_item, parent, false);
            }

            TextView tv = (TextView)view.findViewById(R.id.textItem);
            tv.setText(catalog.getItems().get(position).getName() + "'s game");


            return view;
        }

        public String getId(int position) {
            return getItem(position).getId();
        }

        public String getName(int position) {
            return getItem(position).getName();
        }

    }

    /**
     * Logs in user
     * @param userName username
     * @param password password
     * @return user id if successful, -1 otherwise
     */
    public ChessUser Login(final String userName, final String password) {
        ChessService service = retrofit.create(ChessService.class);
        ChessUser user = new ChessUser();
        try {
            Response<Login> response = service.login(userName, MAGIC, password).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("Login", "Failed to login, response code is = " + response.code());
                return user;
            }

            Login result = response.body();
            if (result.getStatus().equals("yes")) {
                user.setName(result.getUsername());
                user.setId(result.getId());
                return user;
            }

            Log.e("Login", "Failed to login, message is = '" + result.getMessage() + "'");
            if (result.getMessage().equals("user error")) {
                user.setId(-2);
                return user;
            }
            else if (result.getMessage().equals("password error")) {
                user.setId(-3);
                return user;
            }
            return user;
        } catch (IOException | RuntimeException e) {
            Log.e("Login", "Exception occurred while loading account!", e);
            return user;
        }
    }

    /**
     * Creates new user if username available
     * @param userName username
     * @param password password
     * @return user id if successful, 0 otherwise
     */
    public ChessUser CreateNewUser(String userName, String password) {
        ChessService service = retrofit.create(ChessService.class);
        ChessUser user = new ChessUser();
        try {
            Response<SignUp> response = service.signUp(userName, MAGIC, password).execute();
            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("SignUp", "Failed to sign up, response code is = " + response.code());
                return user;
            }

            SignUp result = response.body();
            if (result.getStatus().equals("yes")) {
                user.setName(result.getUsername());
                user.setId(result.getId());
                return user;
            }

            Log.e("SignUp", "Failed to sign up, message is = '" + result.getMessage() + "'");
            // Username already exists
            if (result.getMessage().equals("user error")) {
                user.setId(-2);
                return user;
            }

            return user;
        } catch (IOException | RuntimeException e) {
            Log.e("SignUp", "Exception occurred while signing up!", e);
            return user;
        }
    }

    public Load LoadBoard(String catId) {
        ChessService service = retrofit.create(ChessService.class);
        try {
            Response<Load> response = service.load(MAGIC, catId).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("Load", "Failed to load board, response code is = " + response.code());
                return null;
            }

            Load result = response.body();
            if (result.getStatus().equals("yes")) {
                return result;
            } else {
                Log.e("Load", "Failed to load board, " + result.getMessage());
                return null;
            }
        } catch (IOException | RuntimeException e) {
            Log.e("Load", "Exception occurred while loading board!", e);
            return null;
        }
    }

    public boolean JoinGame(String catId, String player2) {
        ChessService service = retrofit.create(ChessService.class);
        try {
            Response<Join> response = service.join(catId, player2).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("Join", "Failed to join game, response code is = " + response.code());
                return false;
            }

            Join result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            } else {
                Log.e("Join", "Failed to join game, " + result.getMessage());
                return false;
            }
        } catch (IOException | RuntimeException e) {
            Log.e("Join", "Exception occurred while joining game!", e);
            return false;
        }
    }

    public String NewGame(String user) {
        ChessService service = retrofit.create(ChessService.class);
        try {
            Response<NewGame> response = service.newGame(user).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("New Game", "Failed to create new game, response code is = " + response.code());
                return null;
            }

            NewGame result = response.body();
            if (result.getStatus().equals("yes")) {
                return result.getId();
            } else {
                Log.e("New Game", "Failed to create new game, " + result.getMessage());
                return null;
            }
        } catch (IOException | RuntimeException e) {
            Log.e("New Game", "Exception occurred while creating new game!", e);
            return null;
        }
    }

    public boolean SaveGame(String gameId, String xml) {
        ChessService service = retrofit.create(ChessService.class);
        try {
            Response<SaveGame> response = service.saveGame(gameId, xml).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("Save Game", "Failed to save game, response code is = " + response.code());
                return false;
            }

            SaveGame result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            } else {
                Log.e("Save Game", "Failed to save game, " + result.getMessage());
                return false;
            }
        } catch (IOException | RuntimeException e) {
            Log.e("Save Game", "Exception occurred while saving game!", e);
            return false;
        }
    }

    public boolean DeleteGame(String gameId) {
        ChessService service = retrofit.create(ChessService.class);
        try {
            Response<Delete> response = service.deleteGame(gameId).execute();

            // check if request failed
            if (!response.isSuccessful()) {
                Log.e("Delete Game", "Failed to delete game, response code is = " + response.code());
                return false;
            }

            Delete result = response.body();
            if (result.getStatus().equals("yes")) {
                return true;
            } else {
                Log.e("Delete Game", "Failed to delete game: " + result.getMessage());
                return false;
            }
        } catch (IOException | RuntimeException e) {
            Log.e("Delete Game", "Exception occurred while deleting game!", e);
            return false;
        }
    }
}
