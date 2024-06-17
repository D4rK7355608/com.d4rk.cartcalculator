package com.d4rk.cartcalculator.data.database.table

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.d4rk.cartcalculator.data.repository.DateConverter
import java.util.Date

@Entity
@TypeConverters(DateConverter::class)
data class ShoppingCartTable(
    @PrimaryKey(autoGenerate = true) val cartId: Int = 0, val name: String, val date: Date
)