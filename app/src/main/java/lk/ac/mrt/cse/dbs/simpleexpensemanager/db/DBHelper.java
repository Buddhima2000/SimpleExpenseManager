package lk.ac.mrt.cse.dbs.simpleexpensemanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "200151P.db", null, 1);
    }

    @Override
    //creating tables
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("Create table user_account(accountNo TEXT Primary key,bankName TEXT, accountHolderName TEXT,balance REAL)");
        db.execSQL("Create table transaction_log(id INTEGER Primary key autoincrement,date TEXT,accountNo TEXT,expenseType TEXT,amount REAL,Foreign key(accountNo) References user_account(accountNo))");
    }

        //deleting the tables
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("Drop table if exists user_account");
        db.execSQL("Drop table if exists transaction_log");
    }

}
