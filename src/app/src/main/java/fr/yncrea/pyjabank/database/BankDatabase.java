package fr.yncrea.pyjabank.database;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import java.util.UUID;

import fr.yncrea.pyjabank.R;
import fr.yncrea.pyjabank.database.dao.AccountDao;
import fr.yncrea.pyjabank.database.dao.UserDao;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.database.models.User;

import static android.content.Context.MODE_PRIVATE;

@Database(entities = {Account.class, User.class}, version = 2, exportSchema = false)
public abstract class BankDatabase extends RoomDatabase {

    private static volatile BankDatabase INSTANCE;

    public static BankDatabase getDatabase() {
        return INSTANCE;
    }

    public abstract AccountDao accountDao();
    public abstract UserDao userDao();

    public static void buildDatabase(final Context context) {//pas de pattern singleton car argument

        final String PREF_SLOT_ACCESS = "PyjaBankUserSecure";
        final String PREF_ACCESS_DB_KEY = "dbKey";

        SharedPreferences prefs = context.getSharedPreferences(PREF_SLOT_ACCESS, MODE_PRIVATE);

        String uniqueID = prefs.getString(PREF_ACCESS_DB_KEY, null);
        if (uniqueID == null) {
            uniqueID = UUID.randomUUID().toString();
            prefs.edit().putString(PREF_ACCESS_DB_KEY, uniqueID).apply();
        }

        final SupportFactory factory = new SupportFactory(SQLiteDatabase.getBytes(uniqueID.toCharArray()), null,false);
        uniqueID = null;

        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BankDatabase.class, context.getString(R.string.app_db_name))
                .openHelperFactory(factory) //commenter pour passer sur db classique
                .addMigrations(MIGRATION_1_2)
                .build();
    }

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE users ADD COLUMN username TEXT");//only once at a time
            database.execSQL("ALTER TABLE users ADD COLUMN password TEXT");
        }
    };
}
