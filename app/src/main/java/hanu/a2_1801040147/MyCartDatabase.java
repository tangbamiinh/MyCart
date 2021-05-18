package hanu.a2_1801040147;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import hanu.a2_1801040147.daos.CartItemDao;
import hanu.a2_1801040147.daos.ProductDao;
import hanu.a2_1801040147.models.CartItem;
import hanu.a2_1801040147.models.Product;

@Database(entities = {Product.class, CartItem.class}, version = 1)
public abstract class MyCartDatabase extends RoomDatabase {

    private static MyCartDatabase instance;

    public abstract ProductDao productDao();

    public abstract CartItemDao cartItemDao();

    public static synchronized MyCartDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    MyCartDatabase.class, "my_cart_database")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static final Callback roomCallback = new Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
//            new PopulateDbAsyncTask(instance).execute();
        }
    };

//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
//        private ProductDao noteDao;
//
//        private PopulateDbAsyncTask(MyCartDatabase db) {
//            noteDao = db.noteDao();
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//
//            return null;
//        }
//    }
}
