package fr.yncrea.pyjabank.interfaces;

import fr.yncrea.pyjabank.database.BankDatabase;

public interface Utils {
    BankDatabase getDatabase();

    boolean haveInternet();
}
