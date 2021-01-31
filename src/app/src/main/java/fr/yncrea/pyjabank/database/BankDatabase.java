package fr.yncrea.pyjabank.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SupportFactory;

import fr.yncrea.pyjabank.database.dao.AccountDao;
import fr.yncrea.pyjabank.database.dao.UserDao;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.database.models.User;

@Database(entities = {Account.class, User.class}, version = 1, exportSchema = false)
public abstract class BankDatabase extends RoomDatabase {

    private static volatile BankDatabase INSTANCE;

    public static void buildDatabase(final Context context) {//pas de pattern singleton car argument
        //if pas de fichier,
        //génère clé avec aléatoire
        //récupère emprunte cryptographique
        //enregistre en fichier

        //lit clé chiffrée depuis fichier

        byte[] key = SQLiteDatabase.getBytes("passPhrase".toCharArray());
        final SupportFactory factory = new SupportFactory(key, null,false);

        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BankDatabase.class, "PyjaBank.db")
                .openHelperFactory(factory) //commenter pour passer sur db classique
                .build();
    }

    public static BankDatabase getDatabase() {
        return INSTANCE;
    }

    public abstract AccountDao accountDao();
    public abstract UserDao userDao();
}
