package edu.vt.cs.cs5254.criminalintent

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import java.lang.IllegalStateException
import java.util.UUID
import java.util.concurrent.Executors

private const val DATABASE_NAME = "crime-database"

class CrimeRepository private constructor(context: Context) {

    private val initializeCrimeDatabaseCallback: RoomDatabase.Callback =
        object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                executor.execute {
                    deleteAllCrimesInDatabase()

                    for (i in 0..100) {
                        val crime = Crime()

                        crime.title = "Crime #$i"
                        crime.isSolved = i % 2 == 0
                        addCrime(crime)
                    }
                }
            }
        }

    private val database: CrimeDatabase =
        Room.databaseBuilder(context.applicationContext, CrimeDatabase::class.java, DATABASE_NAME)
            .addCallback(initializeCrimeDatabaseCallback).build()

    private val crimeDao = database.crimeDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    fun updateCrime(crime: Crime){
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }
    fun deleteAllCrimesInDatabase() {
        executor.execute {
            crimeDao.deleteAllCrimesInDatabase()
        }
    }

    fun getCrimes(): LiveData<List<Crime>> {
        val crimes = crimeDao.getCrimes()
        return crimes
    }
    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {

        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw  IllegalStateException("CrimeRepository must be initialized")
        }
    }
}