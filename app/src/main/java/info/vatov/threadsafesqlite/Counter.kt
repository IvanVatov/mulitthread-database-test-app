package info.vatov.threadsafesqlite

object Counter {
    @Volatile
    var creations = 0
    @Volatile
    var deletions = 0
    @Volatile
    var updates = 0
    @Volatile
    var allget = 0
    @Volatile
    var singleget = 0
    @Volatile
    var dbrecords = 0
    @Volatile
    var getAllTime = 0L

    fun reset() {
        creations = 0
        deletions = 0
        updates = 0
        allget = 0
        singleget = 0
        dbrecords = 0
        getAllTime = 0L
    }

    override fun toString(): String {
        return "Created: $creations , Deleted: $deletions, Updates: $updates ${lineSeparator()} GetAll: $allget , GetSingle: $singleget , Records: $dbrecords ${lineSeparator()} ReadAllTime: $getAllTime millis"
    }
}

fun lineSeparator(): String {
    return System.getProperty("line.separator")!!
}