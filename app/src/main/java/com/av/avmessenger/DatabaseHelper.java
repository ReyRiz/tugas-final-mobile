// app/src/main/java/com/av/avmessenger/DatabaseHelper.java
package com.av.avmessenger;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "chat.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_MESSAGES = "messages";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_MESSAGE = "message";
    private static final String COLUMN_SENDERID = "senderid";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_ROOM = "room";

    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USERID = "userId";
    private static final String COLUMN_USERNAME = "userName";
    private static final String COLUMN_MAIL = "mail";
    private static final String COLUMN_PASSWORD = "password";
    private static final String COLUMN_PROFILEPIC = "profilepic";
    private static final String COLUMN_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MESSAGES_TABLE = "CREATE TABLE " + TABLE_MESSAGES + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_MESSAGE + " TEXT,"
                + COLUMN_SENDERID + " TEXT,"
                + COLUMN_TIMESTAMP + " INTEGER,"
                + COLUMN_ROOM + " TEXT"
                + ")";
        db.execSQL(CREATE_MESSAGES_TABLE);

        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + COLUMN_USERID + " TEXT PRIMARY KEY,"
                + COLUMN_USERNAME + " TEXT,"
                + COLUMN_MAIL + " TEXT,"
                + COLUMN_PASSWORD + " TEXT,"
                + COLUMN_PROFILEPIC + " TEXT,"
                + COLUMN_STATUS + " TEXT"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    // Message methods (unchanged)
    public void addMessage(msgModelclass message, String room) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_MESSAGE, message.getMessage());
        values.put(COLUMN_SENDERID, message.getSenderid());
        values.put(COLUMN_TIMESTAMP, message.getTimeStamp());
        values.put(COLUMN_ROOM, room);
        db.insert(TABLE_MESSAGES, null, values);
        db.close();
    }

    public ArrayList<msgModelclass> getMessages(String room) {
        ArrayList<msgModelclass> messages = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MESSAGES, null, COLUMN_ROOM + "=?",
                new String[]{room}, null, null, COLUMN_TIMESTAMP + " ASC");
        if (cursor.moveToFirst()) {
            do {
                String message = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MESSAGE));
                String senderid = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_SENDERID));
                long timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP));
                messages.add(new msgModelclass(message, senderid, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return messages;
    }

    // User methods
    public boolean registerUser(Users user) {
        if (getUserByEmail(user.getMail()) != null) return false;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERID, user.getUserId());
        values.put(COLUMN_USERNAME, user.getUserName());
        values.put(COLUMN_MAIL, user.getMail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_PROFILEPIC, user.getProfilepic());
        values.put(COLUMN_STATUS, user.getStatus());
        long result = db.insert(TABLE_USERS, null, values);
        db.close();
        return result != -1;
    }

    public Users authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_MAIL + "=? AND " + COLUMN_PASSWORD + "=?",
                new String[]{email, password}, null, null, null);
        Users user = null;
        if (cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return user;
    }

    public Users getUserById(String userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_USERID + "=?",
                new String[]{userId}, null, null, null);
        Users user = null;
        if (cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return user;
    }

    public Users getUserByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, COLUMN_MAIL + "=?",
                new String[]{email}, null, null, null);
        Users user = null;
        if (cursor.moveToFirst()) {
            user = getUserFromCursor(cursor);
        }
        cursor.close();
        db.close();
        return user;
    }

    public ArrayList<Users> getAllUsers() {
        ArrayList<Users> users = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                users.add(getUserFromCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return users;
    }

    public void deleteMessage(String messages) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MESSAGES, COLUMN_MESSAGE + "=?", new String[]{messages});
        db.close();
    }

    public void updateUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, user.getUserName());
        values.put(COLUMN_MAIL, user.getMail());
        values.put(COLUMN_PASSWORD, user.getPassword());
        values.put(COLUMN_PROFILEPIC, user.getProfilepic());
        values.put(COLUMN_STATUS, user.getStatus());
        db.update(TABLE_USERS, values, COLUMN_USERID + "=?", new String[]{user.getUserId()});
        db.close();
    }

    private Users getUserFromCursor(Cursor cursor) {
        String userId = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERID));
        String userName = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_USERNAME));
        String mail = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_MAIL));
        String password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD));
        String profilepic = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PROFILEPIC));
        String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
        return new Users(userId, userName, mail, password, profilepic, status);
    }
}