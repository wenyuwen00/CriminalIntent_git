package edu.vt.cs.cs5254.criminalintent

import androidx.lifecycle.LiveData
import androidx.room.*
import java.util.*

@Dao
interface CrimeDao {
    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: UUID): LiveData<Crime?>

    @Insert
    fun addCrime(crime: Crime)

    @Update
    fun updateCrime(crime: Crime)

    @Delete
    fun deleteCrime(crime: Crime)

    // WARNING: Nuclear option!
    @Query("DELETE FROM crime")
    fun deleteAllCrimesInDatabase()
}