package fr.yncrea.pyjabank.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.ArrayList;
import java.util.List;

import fr.yncrea.pyjabank.database.models.Account;

@Dao
public interface AccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<Account> accounts);// insertAll(accounts, idUser)

    @Query("SELECT * FROM Account") // WHERE id_user = (SELECT id FROM User WHERE name = :user)
    List<Account> getAll();// getAll(user)

    @Query("DELETE FROM Account")
    void deleteAll();
}