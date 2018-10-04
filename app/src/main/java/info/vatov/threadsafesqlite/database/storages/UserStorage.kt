package info.vatov.threadsafesqlite.database.storages

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import info.vatov.threadsafesqlite.database.DatabaseProvider
import info.vatov.threadsafesqlite.database.models.User

class UserStorage(private val databaseProvider: DatabaseProvider) : Storage {

    override fun create(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_TABLE)
    }

    override fun upgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun addUser(user: User) {
        val values = ContentValues().apply {
            put(USER_ID, user.id)
            put(USER_NAME, user.name)
            put(USER_AGE, user.age)
        }
        databaseProvider.getDatabase().insert(TABLE_NAME, null, values)
    }

    fun deleteUser(userId: String) {
        val selection = "$USER_ID == ?"
        val selectionArgs = arrayOf(userId)
        databaseProvider.getDatabase().delete(TABLE_NAME, selection, selectionArgs)
    }

    fun getUserById(userId: String): User? {
        var user: User? = null
        var cursor: Cursor? = null
        try {
            cursor = databaseProvider.getDatabase().query(
                    TABLE_NAME,
                    arrayOf(USER_ID, USER_NAME, USER_AGE),
                    "$USER_ID = ?",
                    arrayOf(userId),
                    null, null, null
            )
            if (cursor.moveToFirst()) {
                with(cursor)
                {
                    user = User(
                            getString(getColumnIndexOrThrow(USER_ID)),
                            getString(getColumnIndexOrThrow(USER_NAME)),
                            getInt(getColumnIndexOrThrow(USER_AGE))
                    )
                }
            }
            return user
        } finally {
            cursor?.close()
        }
    }

    fun getUserByAge(age: Int): User? {
        var user: User? = null
        var cursor: Cursor? = null
        try {
            cursor = databaseProvider.getDatabase().query(
                    TABLE_NAME,
                    arrayOf(USER_ID, USER_NAME, USER_AGE),
                    "$USER_AGE >= ?",
                    arrayOf("$age"),
                    null, null, null, "1"
            )
            if (cursor.moveToFirst()) {
                with(cursor)
                {
                    user = User(
                            getString(getColumnIndexOrThrow(USER_ID)),
                            getString(getColumnIndexOrThrow(USER_NAME)),
                            getInt(getColumnIndexOrThrow(USER_AGE))
                    )
                }
            }
            return user
        } finally {
            cursor?.close()
        }
    }

    fun getAllUsers(): ArrayList<User> {
        val result = ArrayList<User>()
        var cursor: Cursor? = null
        try {
            cursor = databaseProvider.getDatabase().query(
                    TABLE_NAME,
                    arrayOf(USER_ID, USER_NAME, USER_AGE),
                    null,
                    null,
                    null,
                    null,
                    "$USER_AGE DESC"
            )

            with(cursor)
            {
                while (moveToNext()) {
                    result.add(User(
                            getString(getColumnIndexOrThrow(USER_ID)),
                            getString(getColumnIndexOrThrow(USER_NAME)),
                            getInt(getColumnIndexOrThrow(USER_AGE))
                    ))
                }
            }
            return result
        } finally {
            cursor?.close()
        }
    }

    fun updateUser(user: User) {
        val values = ContentValues().apply {
            put(USER_NAME, user.name)
            put(USER_AGE, user.age)
        }
        val selection = "$USER_ID = ?"
        val selectionArgs = arrayOf(user.id)
        databaseProvider.getDatabase().update(
                TABLE_NAME,
                values,
                selection,
                selectionArgs)
    }

    companion object {

        private const val TABLE_NAME = "users"
        private const val USER_ID = "id"
        private const val USER_NAME = "name"
        private const val USER_AGE = "age"

        private const val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
                "'$USER_ID' TEXT PRIMARY KEY, " +
                "'$USER_NAME' TEXT, " +
                "'$USER_AGE' INTEGER" +
                ")"
    }
}