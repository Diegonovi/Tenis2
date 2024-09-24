package org.example.tenist.models

import java.time.LocalDate
import java.time.LocalDateTime

data class Tenist (
    val id : Int = tenistCount,
    val name : String,
    val country : String,
    var weight : Int,
    val height : Double,
    val dominantHand : Dexterity?,
    val points : Int,
    val birthDate : LocalDate,
    val createdAt : LocalDateTime = LocalDateTime.now(),
    val updatedAt : LocalDateTime = LocalDateTime.now(),
    val isDeleted : Boolean = false
){
    init {
        tenistCount++
    }

    companion object{
        //Añade a uno al contador cuando se creo una nueva
        //clase para imitar el autoincrement de una base de datos
        private var tenistCount : Int = 0
    }
}

enum class Dexterity {
    DIESTRO ,ZURDO , AMBIDIESTRO
}
