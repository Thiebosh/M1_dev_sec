package fr.yncrea.pyjabank.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import fr.yncrea.pyjabank.database.dao.AccountDao;
import fr.yncrea.pyjabank.database.dao.UserDao;
import fr.yncrea.pyjabank.database.models.Account;
import fr.yncrea.pyjabank.database.models.User;

@Database(entities = {Account.class, User.class}, version = 1, exportSchema = false)
public abstract class BankDatabase extends RoomDatabase {
    public abstract AccountDao accountDao();
    public abstract UserDao userDao();
}
