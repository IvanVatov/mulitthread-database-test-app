package info.vatov.threadsafesqlite.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import info.vatov.threadsafesqlite.Counter
import info.vatov.threadsafesqlite.database.models.User
import info.vatov.threadsafesqlite.database.storages.UserStorage
import java.util.concurrent.locks.ReentrantLock

class Database(context: Context, dbName: String) : SQLiteOpenHelper(context, getDatabaseName(dbName), null, DB_VERSION), DatabaseProvider {

    private var db: SQLiteDatabase? = null
    private val lock = ReentrantLock()

    // Storage
    private val userStorage = UserStorage(this)

    @Synchronized
    override fun getDatabase(): SQLiteDatabase {
        if (db != null) {
            return db!!
        }
        db = writableDatabase
        return db!!
    }

    override fun onCreate(db: SQLiteDatabase?) {
        userStorage.create(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {

    }

    fun addUser(user: User) {
        lock.lock()
        try {
            userStorage.addUser(user)
            Counter.creations++
        } finally {
            lock.unlock()
        }
    }

    fun deleteUser(userId: String) {
        lock.lock()
        try {
            userStorage.deleteUser(userId)
            Counter.deletions++
        } finally {
            lock.unlock()
        }
    }

    fun updateUser(user: User) {
        lock.lock()
        try {
            userStorage.updateUser(user)
            Counter.updates++
        } finally {
            lock.unlock()
        }
    }

    fun getUserById(userId: String): User? {
        try {
            return userStorage.getUserById(userId)
        } finally {
            Counter.singleget++
        }
    }

    fun getUserByAge(age: Int): User? {
        try {
            return userStorage.getUserByAge(age)
        } finally {
            Counter.singleget++
        }
    }

    fun getAllUsers(): ArrayList<User> {
        val initTime = System.currentTimeMillis()
        try {
            return userStorage.getAllUsers()
        } finally {
            Counter.getAllTime = System.currentTimeMillis() - initTime
            Counter.allget++
        }
    }


    companion object {
        private const val DB_VERSION = 1
        private const val DB_POSTFIX = "_MYDB"

        private fun getDatabaseName(name: String): String {
            return name + DB_POSTFIX
        }
    }
}