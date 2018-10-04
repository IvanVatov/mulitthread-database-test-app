package info.vatov.threadsafesqlite

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import info.vatov.threadsafesqlite.database.Database
import info.vatov.threadsafesqlite.database.models.User
import java.util.*

class MainActivity : AppCompatActivity() {

    private var database: Database? = null
    private var isLoopThreadRunning = false
    private val random = Random()
    private val executor = MultiThreadExecutor()

    // UI items
    private var counterTextView: TextView? = null
    private var loadDatabaseEditText: EditText? = null
    private var loadDatabaseButton: Button? = null
    private var databaseResultTextView: TextView? = null
    private var controlStopButton: Button? = null
    private var controlStartButton: Button? = null

    private val timer = object : CountDownTimer(250, 250) {
        override fun onFinish() {
            updateCounter()
            this.start()
        }

        override fun onTick(millisUntilFinished: Long) {

        }
    }

    private val clickListener = View.OnClickListener { view ->
        when (view.id) {
            R.id.load_database_button -> {
                loadDatabaseEditText?.let { loadDatabase(it.text.toString()) }
            }
            R.id.database_result_text_view -> {
                databaseResultTextView?.text = null
                Handler(Looper.getMainLooper()).post {
                    showDatabaseItems()
                }
            }
            R.id.control_stop_button -> stopUsingDataBase()
            R.id.control_start_button -> startUsingDataBase()
            R.id.counter_text_view -> Counter.reset()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        counterTextView = findViewById(R.id.counter_text_view)
        loadDatabaseEditText = findViewById(R.id.load_database_edit_text)
        loadDatabaseButton = findViewById(R.id.load_database_button)
        databaseResultTextView = findViewById(R.id.database_result_text_view)
        controlStopButton = findViewById(R.id.control_stop_button)
        controlStartButton = findViewById(R.id.control_start_button)


        loadDatabaseButton?.setOnClickListener(clickListener)
        databaseResultTextView?.setOnClickListener(clickListener)
        controlStopButton?.setOnClickListener(clickListener)
        controlStartButton?.setOnClickListener(clickListener)
        counterTextView?.setOnClickListener(clickListener)

        timer.start()
    }

    override fun onDestroy() {
        timer.cancel()
        database?.close()
        super.onDestroy()
    }

    private fun updateCounter() {
        counterTextView?.text = "$Counter${lineSeparator()}Executor Queue: ${executor.getQueueSize()} , Running Threads: ${executor.getRunningThreads()}"
    }

    private fun showDatabaseItems() {
        val stringBuilder = StringBuilder()
        val allUsers = database?.getAllUsers()
        allUsers?.let {
            Counter.dbrecords = it.size
            it.forEach { user ->
                stringBuilder.append(user.toString())
            }
        }
        databaseResultTextView?.text = stringBuilder.toString()
    }

    private fun startUsingDataBase() {
        isLoopThreadRunning = true
        val loopThread = object : Thread() {
            override fun run() {
                while (isLoopThreadRunning) {
                    if (executor.getQueueSize() > 250) {
                        Thread.sleep(250)
                        continue
                    }
                    executor.execute {
                        useDatabase()
                    }
                }
                return
            }
        }
        loopThread.start()
    }

    private fun stopUsingDataBase() {
        isLoopThreadRunning = false
    }

    private fun useDatabase() {
        val usageType = random.nextInt(100)
        when (usageType) {
            // create new
            in 0..20 -> database?.addUser(generateUser())


            // get one by random age and modify it
            in 21..69 -> database?.getUserByAge(generateAge())?.let { database?.updateUser(User(it.id, generateUUID(), generateAge())) }
            // get one by random age and delete it
            in 70..84 -> database?.getUserByAge(generateAge())?.let { database?.deleteUser(it.id) }


            // Update random from all
            in 85..94 -> database?.getAllUsers()?.let { arr ->
                Counter.dbrecords = arr.size
                if (arr.size > 0)
                    database?.updateUser(User(arr[random.nextInt(arr.size)].id, generateName(), generateAge()))
            }

            // Delete random from all
            in 95..99 -> database?.getAllUsers()?.let { arr ->
                Counter.dbrecords = arr.size
                if (arr.size > 0)
                    database?.deleteUser(arr[random.nextInt(arr.size)].id)
            }

        }

    }

    private fun generateUser(): User {
        return User(generateUUID(), generateName(), generateAge())
    }

    private fun generateAge(): Int {
        return random.nextInt(99) + 1
    }

    private fun generateUUID(): String {
        return UUID.randomUUID().toString()
    }

    private val names = arrayOf("Roselia Gillispie", "Jacob Rebelo", "Leighann Jorgensen", "Candida Alling", "Brent Weston", "Chester Reaper", "Hildegarde Cowman", "Jessica Wilkie", "Marx Houge", "Cecile Donner", "Eusebio Pratts", "Germaine Scism", "Kasandra Romberg", "Pete Ruffin", "Babette Stuart", "Garnett Kinkade", "Ossie Lepore", "Leroy Hosking", "Cristal Didonato", "Aiko Bresler", "Hermelinda Booher", "Rochell Gesell", "Zachary Bucy", "Mozell Hornak", "Phyliss Lipford", "Joann Claro", "Ai Pothier", "Arnette Kilpatrick", "Malcolm Fuentes", "Eric Kalinowski", "Carlee Hines", "Toshiko Delafuente", "Beatris Kucharski", "Sherri Stellmacher", "Amparo Stickle", "Vannesa Bronk", "Hong Roles", "Leandro Anstett", "Melodee Castruita", "Lacey Sundstrom", "Piper Mericle", "Arturo Notaro", "Orpha Beedle", "Noreen Junge", "Dave Scogin", "Tamara Szeto", "Trinity Kittleson", "Jong Berger", "Roselle Rowan", "Estell Proper", "Venice Ellman", "Chuck Guiterrez", "Phylis Lovelace", "Linwood Boros", "Rosann Medders", "Antony Pelayo", "Charline Glennon", "Chastity Kyle", "Titus Vandemark", "Rebbecca Sturdivant", "Cameron Foor", "Sulema Baylis", "Jenniffer Montesinos", "Rosina Wunsch", "Teddy Ceniceros", "Grace Potts", "Hal Mabry", "Belen Sippel", "Wanetta Shams", "Isis Lambson", "Marta Hackler", "Nannie Mode", "Hyun Dooley", "Rosaline Borman", "Lore Welcome", "Rafael Torina", "Bobbye Melchior", "Eufemia Totman", "Juliann Ferrier", "Aldo Zajicek", "Alberta Garlitz", "Abdul Drone", "Donte Uhl", "Latoria Bowles", "Dillon Mina", "Buffy Barry", "Sarita Yoo", "Rosario Bettis", "Cyrus Siddiqi", "Wilda Ruud", "Chet Huffine", "Stella Leathers", "Bernice Jeanbaptiste", "Desirae Justus", "Christi Buffum", "Providencia Timmins", "Trudie Wherry", "Eusebia Huseby", "Noella Bruder", "Verlie Holter", "Tamar Wojtowicz", "Sharri Runner", "Brinda Garriott", "Barbara Hiltz", "Beaulah Alexandria", "Teofila Hilgefort", "Hilma Fetty", "Ignacio Eastin", "Na Waren", "Carmina Bellanger", "Vernia Linker", "Devona Noecker", "Pennie Roby", "Laurice Delacruz", "Cesar Swaby", "Lura Mulder", "Rosann Saavedra", "Bettie Rommel", "Sook Caffrey", "Fran Saragosa", "Jesusita Mistretta", "Corinna Malone", "Migdalia Castleberry", "Doreatha Amar", "Tommie Baley", "Janita Baltimore", "Williams Hillin", "Kum Girardin", "Kati Yarger", "Bonita Bartell", "Antonetta Phillippe", "Dovie Gunnels", "Francis Winegar", "Lilli Wayt", "Marceline Rivenbark", "Jeni Muir", "Ulysses Holtzman", "Jamee Vangorder", "Lezlie Paulette", "Loma Tischler", "Doretta Lattin", "Luetta Delrio", "Reyes Pridmore", "Vina Mcgibbon", "Elaina Ouk", "Wynona Ibanez", "Remedios Oba", "Jonna Cruz", "Sandi Jobin", "Lashonda Defilippo", "Arla Rasnick", "Alyse Caprio", "Cassaundra Kulpa", "Andree Salvaggio", "Michael Notter", "Jolynn Kai", "Juli Luby", "Deadra Wiley", "Lashon Hendrie", "Erline Elkington", "Porsche Whiteman", "Dominga Ridout", "Kira Whitley", "Alba Sellner", "Denae Shontz", "Clotilde Rider", "Yessenia Maughan", "Antwan Damiano", "Ruthie Malbrough", "Maryetta Garneau", "Rosemarie Macha", "Alma Mizelle", "Lakendra Olmo", "Dorathy Kaup", "Kraig Kirwin", "Alethea Eisenmann", "Caryn Grassi", "Charity Remley", "Arletha Marney", "Ai Dewald", "Odell Sandavol", "Diana Conners", "Anjelica Carner", "Ardelia Paulk", "Laurinda Chivers", "Rima Stretch", "Keva Valls", "Rolando Wickham", "Annalisa Forester", "Lupe Bouchard", "Lin Wyche", "Harmony Cervantez", "Krishna Arbuckle", "Drema Mcgarrity", "Laurie Gottschalk", "Laurene Mcminn", "Tanna Desmarais", "Lyman Koons", "Bebe Press", "Janean Sander", "Krysta Maravilla", "Tijuana Dubiel", "Misti Bonnell", "Shantelle Winchenbach", "Lynda Ruther", "Neta Twyman", "Marybelle Shatzer", "Han Frick", "Robbyn Hoy", "Lorilee Polasek", "Mi Barnwell", "Anissa Byam", "Raguel Shive", "Kiara Lymon", "Sima Zych", "Sherise Seth", "Andrew Coolbaugh", "Priscilla Weidenbach", "Eleanore Eckel", "Donnell Rothrock", "Kaylee Tift", "Alene Morison", "Donita Wilde", "Chong Winrow", "Tami Tate", "Marvis Sheffler", "Jena Doiron", "Sharice Ortmann", "Genna Mankin", "Norbert Monger", "Nena Abasta", "Kellie Earley", "Hye Leary", "Mercy Berlin", "Twanna Comeaux", "Ricki Curatolo", "Jerrica Lukens", "Stephani Schmidtke", "Mittie Sharper", "Franklyn Warner", "Verdie Fink", "Oscar Spurr", "Nida Stimac", "Lanny Sisto", "Elvis Blansett", "Juana Derry", "Particia Kaczynski", "Danilo Ogg", "Veronique Germano", "Shanell Prejean")

    private fun generateName(): String {
        return names[random.nextInt(names.size)]
    }

    private fun loadDatabase(name: String) {
        stopUsingDataBase()
        while (executor.getRunningThreads() > 0) {
            Thread.sleep(100) // UI freezes we are waiting to finish work with current database
            continue
        }
        if (name.length < 3 && !name.matches(Regex("[^A-Za-z0-9]"))) {
            Toast.makeText(this, "Database name is too short or invalid", Toast.LENGTH_LONG).show()
            return
        }
        database?.close()
        database = Database(this, name)
        loadDatabaseEditText?.clearFocus()
        (getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(loadDatabaseEditText?.windowToken, 0)
    }
}
