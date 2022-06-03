package org.idnp.danp_sample_room

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.room.*
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val room: PeopleDB = Room.databaseBuilder(this, PeopleDB::class.java, "people2").build()

        lifecycleScope.launch { val people = room.personDao().getAll(); }


        var btnSave = findViewById<Button>(R.id.btnSave)
        var btnRead = findViewById<Button>(R.id.btnRead)
        var edtName = findViewById<EditText>(R.id.edtName)
        var edtAge = findViewById<EditText>(R.id.edtEdad)
        var edtAddress = findViewById<EditText>(R.id.edtAddress)


        btnSave.setOnClickListener {

            val p: Person = MainActivity.Person(
                edtName.text.toString(),
                Integer.parseInt(edtAge.text.toString()),
                edtAddress.text.toString()
            )

            lifecycleScope.launch {
                room.personDao().insert(p)
            }

            Log.d("MainActivity", "Saved")
        }


        btnRead.setOnClickListener {
            lifecycleScope.launch {
                val people = room.personDao().getAll();
                for (p in people) {
                    Log.d("Person:", p.name + "," + p.age + "," + p.address)
                }

            }
        }

    }

    @Database(
        entities = [Person::class],
        version = 2
    )
    abstract class PeopleDB : RoomDatabase() {
        abstract fun personDao(): PersonDao
    }

    @Entity
    data class Person(
//        @PrimaryKey(autoGenerate = true)
//        val id: Int,
        @PrimaryKey
        val name: String,
        val age: Int,
        val address: String
    )

    @Dao
    interface PersonDao {
        @Query("SELECT * from Person")
        suspend fun getAll(): List<Person>

        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(people: Person)


    }
}