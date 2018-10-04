package info.vatov.threadsafesqlite.database

import android.database.sqlite.SQLiteDatabase

interface DatabaseProvider {
    fun getDatabase(): SQLiteDatabase
}