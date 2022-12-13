package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentTransactionDAO implements TransactionDAO {
    private final List<Transaction> transactions;
    private final SQLiteDatabase sqlDB;
    private final SimpleDateFormat simpleDateFormat;

    public PersistentTransactionDAO(SQLiteDatabase sqLiteDatabase) {
        this.transactions = new LinkedList<>();
        this.sqlDB = sqLiteDatabase;
        this.simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        //load details of the accounts to the hashmap
        loadTransactions();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        //adding to database
        ContentValues contentValues = new ContentValues();
        contentValues.put("date",simpleDateFormat.format(date));
        contentValues.put("accountNo",accountNo);
        contentValues.put("expenseType", String.valueOf(expenseType));
        contentValues.put("amount",amount);
        sqlDB.insert("transaction_log",null,contentValues);
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }

        return transactions.subList(size - limit, size);
    }

    public void loadTransactions(){

        Cursor c3 = sqlDB.rawQuery("SELECT * FROM transaction_log",null);

        while (c3.moveToNext()){
            Date date = null;
            try {
                date = simpleDateFormat.parse(c3.getString(1));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String AccNo = c3.getString(2);
            ExpenseType type = Enum.valueOf(ExpenseType.class,c3.getString(3));
            double amount = c3.getDouble(4);
            Transaction transaction = new Transaction(date, AccNo, type, amount);

            transactions.add(transaction);
        }
        c3.close();
    }
}
