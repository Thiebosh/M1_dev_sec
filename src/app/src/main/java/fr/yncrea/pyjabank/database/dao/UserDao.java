package fr.yncrea.pyjabank.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import fr.yncrea.pyjabank.database.models.User;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("SELECT CASE COUNT(*) WHEN 0 THEN 0 ELSE 1 END FROM User")
    boolean isUser();

    //boolean exitUser(username, password); //if exitUser(null, null) => updateUser

    @Query("SELECT * FROM User WHERE id == (:idUser)")
    User get(int idUser);//remplace by username

    //void updateUser(user);

    @Query("DELETE FROM User")
    void deleteAll();
}
