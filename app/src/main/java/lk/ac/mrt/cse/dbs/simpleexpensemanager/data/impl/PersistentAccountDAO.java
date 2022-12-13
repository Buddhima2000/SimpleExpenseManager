package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class PersistentAccountDAO implements AccountDAO {
    private final Map<String, Account> accounts;
    private final SQLiteDatabase sqlDB;

    public PersistentAccountDAO(SQLiteDatabase sqLiteDatabase) {
        this.accounts = new HashMap<>();
        this.sqlDB = sqLiteDatabase;
        loadAccountData();
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public void addAccount(Account account) {
        accounts.put(account.getAccountNo(), account);

    //store data in the  the database
        ContentValues contentValues = new ContentValues();
        contentValues.put("accountNo",account.getAccountNo());
        contentValues.put("bankName",account.getBankName());
        contentValues.put("accountHolderName",account.getAccountHolderName());
        contentValues.put("balance",account.getBalance());
        sqlDB.insert("user_account",null,contentValues);
    }


    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        Cursor c1 = sqlDB.rawQuery("SELECT * FROM user_account WHERE accountNo = ?",new String[]{accountNo});
        if(!(c1.getCount()>=1)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }else {
            sqlDB.delete("user_account", "accountNo=?", new String[]{accountNo});
            accounts.remove(accountNo);
        }
        c1.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
        Account account = accounts.get(accountNo);

        switch (expenseType) {
            case EXPENSE:
                if(amount>account.getBalance()){
                    throw new InvalidAccountException("Insufficient balance!");
                }
                account.setBalance(account.getBalance() - amount);
                break;
            case INCOME:
                account.setBalance(account.getBalance() + amount);
                break;
        }
        accounts.put(accountNo, account);

        ContentValues contentValues = new ContentValues();
        contentValues.put("balance",account.getBalance());
        sqlDB.update("user_account",contentValues,"accountNo=?",new String[]{accountNo});
    }



    public void loadAccountData(){

        Cursor c2 = sqlDB.rawQuery("SELECT * FROM user_account",null);
        while (c2.moveToNext()){
            String accNo = c2.getString(0);
            String bank = c2.getString(1);
            String accountHolder = c2.getString(2);
            double balance = c2.getDouble(3);
            Account account = new Account(accNo,bank,accountHolder,balance);

            accounts.put(account.getAccountNo(), account);
        }
        c2.close();
    }
}
