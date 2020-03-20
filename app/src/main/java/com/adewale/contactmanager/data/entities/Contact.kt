package com.adewale.contactmanager.data.entities


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.adewale.contactmanager.data.DateConverter
import java.util.Date

@Entity(tableName = "contact_table")
@TypeConverters(DateConverter::class)
data class Contact(
    var first_name: String?,
    var last_name: String?,
    var email: String?,
    var phone_home: String?,
    var phone_cell: String?,
    var phone_work: String?,
    var address: String?,
    var birthday: Date?,
    var profileImageName: String?
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}