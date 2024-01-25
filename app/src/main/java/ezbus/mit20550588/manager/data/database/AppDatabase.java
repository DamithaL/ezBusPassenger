//// Location: ezbus.mit20550588.manager.data.database
//
//package ezbus.mit20550588.manager.data.database;
//
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.room.Database;
//import androidx.room.Room;
//import androidx.room.RoomDatabase;
//import androidx.room.TypeConverters;
//import androidx.sqlite.db.SupportSQLiteDatabase;
//
//import com.google.android.gms.maps.model.LatLng;
//
//import java.util.Date;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//import ezbus.mit20550588.manager.util.Converters;
//
//@Database(entities = {RecentSearchModel.class, PurchasedTicketModel.class}, version = 1, exportSchema = false)
//@TypeConverters(Converters.class)
//public abstract class AppDatabase extends RoomDatabase {
//
//    private static AppDatabase instance;
//
//    public abstract RecentSearchDao recentSearchDao();
//
//    public abstract PurchasedTicketDao purchasedTicketDao();
//
//    public static synchronized AppDatabase getInstance(Context context) {
//        if (instance == null) {
//            instance = Room.databaseBuilder(
//                            context.getApplicationContext(),
//                            AppDatabase.class,
//                            "app_database"
//                    ).fallbackToDestructiveMigration()
//                    .addCallback(roomCallback).build();
//        }
//        return instance;
//    }
//
//    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//
//            PlaceHolderDataAsyncTask firstTask = new PlaceHolderDataAsyncTask(instance);
//            firstTask.performBackgroundTask();
//        }
//    };
//
//
//    private static class PlaceHolderDataAsyncTask {
//        private Executor executor = Executors.newSingleThreadExecutor();
//        private RecentSearchDao recentSearchDao;
//        private PurchasedTicketDao purchasedTicketDao;
//        private static final int MAX_RECENT_SEARCHES = 30;
//
//
//        public PlaceHolderDataAsyncTask(AppDatabase db) {
//            recentSearchDao = db.recentSearchDao();
//            purchasedTicketDao = db.purchasedTicketDao();
//        }
//
//        public void performBackgroundTask() {
//            executor.execute(new Runnable() {
//
//                // Sample LatLng for locationLatLang
//                LatLng sampleLatLng = new LatLng(6.90246, 79.86115);  // UCSC coordinates
//
//                // Sample Date for searchDate
//                Date sampleSearchDate = new Date();  // This will use the current date and time
//
//                @Override
//                public void run() {
//                    recentSearchDao.insertRecentSearch(new RecentSearchModel("Your recent search", sampleLatLng, sampleSearchDate));
//                                    }
//            });
//        }
//
//
//
//    }
//
//
//}
