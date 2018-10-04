package info.vatov.threadsafesqlite.database.models

import info.vatov.threadsafesqlite.lineSeparator

data class User(val id: String, val name: String, val age: Int) {
    override fun toString(): String {
        return "name: $name , age: $age ${lineSeparator()}"
    }
}