package com.d4rk.cartcalculator.core.data.database.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
@Entity
data class ShoppingCartTable(
    @PrimaryKey(autoGenerate = true)
    val cartId : Int = 0 ,
    val name : String ,
    val date : Long,
    val sharedCart : Boolean = false ,
) {
    fun toDate() : Date = Date(date)
}