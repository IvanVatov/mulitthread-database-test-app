package info.vatov.threadsafesqlite.database.storages

import android.database.sqlite.SQLiteDatabase

interface Storage {
    fun create(db: SQLiteDatabase?)
    fun upgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int)
}