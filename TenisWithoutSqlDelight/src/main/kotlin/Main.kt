package org.example

import org.example.cache.Cache
import org.example.config.AppConfig
import org.example.database.DatabaseManager
import org.example.tenist.models.Tenist
import org.example.tenist.repository.TenistRepositoryImpl
import org.example.tenist.service.database.TenistServiceImpl
import org.example.tenist.service.storage.TenistStorageImpl
import org.example.tenist.service.storage.csv.TenistStorageCsvImpl
import org.example.tenist.service.storage.json.TenistStorageJsonImpl
import org.example.tenist.service.storage.xml.TenistStorageXmlImpl
import org.example.tenist.validator.TenisValidatorImpl
import java.io.File
import java.nio.file.Paths

fun main(args: Array<String>) {

    var input : File
    val output : File? = null

    /*
    fun validateArgs(args: Array<String>): Boolean {
        if (!args[0].contains(".csv")) return false
        try {
            input = File(args[0])
        }catch (e : Exception){
            return false
        }
        return true
    }

    if (args.isEmpty() && !validateArgs(args)) {
        throw IllegalArgumentException("No se puede iniciar el programa sin un fichero csv")
    }
     */

    val config = AppConfig()
    val service = TenistServiceImpl(
        Cache<Int,Tenist>(config),
        TenistRepositoryImpl(
            DatabaseManager(config)
        ),
        TenisValidatorImpl()
    )
    val storage = TenistStorageImpl(
        TenistStorageCsvImpl(),
        TenistStorageJsonImpl(),
        TenistStorageXmlImpl()
    )

    val uri = ClassLoader.getSystemResource("data.csv").toURI()
    val file = Paths.get(uri).toFile()

    val list = storage.importFromCsv(file).value
    list.forEach { tenist ->
        service.save(tenist)
    }

    val tenists = service.findAll().value

    println("\n\nCONSULTAS:")
    println("==========")

    // 1. Tenistas ordenados con ranking (puntos) de mayor a menor
    println("\nTenistas ordenados por puntos:")
    val orderedByRanking = tenists.sortedByDescending { it.points }
    orderedByRanking.forEach { println(it) }

    // 2. Media de altura de los tenistas
    val averageHeight = tenists.map { it.height }.average()
    println("\nMedia de altura de los tenistas: $averageHeight m")

    // 3. Media de peso de los tenistas
    val averageWeight = tenists.map { it.weight }.average()
    println("\nMedia de peso de los tenistas: $averageWeight kg")

    // 4. Tenista más alto
    val tallestTenist = tenists.maxByOrNull { it.height }
    println("\nTenista más alto: ${tallestTenist?.name} con altura de ${tallestTenist?.height} m")

    // 5. Tenistas de España
    println("\nTenistas de España:")
    val spanishTenists = tenists.filter { it.country == "España" }
    spanishTenists.forEach { println(it) }

    // 6. Tenistas agrupados por país
    println("\nTenistas agrupados por país:")
    val groupedByCountry = tenists.groupBy { it.country }
    groupedByCountry.forEach { (country, tenists) ->
        println("$country: ${tenists.joinToString { it.name }}")
    }

    // 7. Número de tenistas agrupados por país y ordenados por puntos descendente
    println("\nNúmero de tenistas agrupados por país (ordenados por puntos):")
    val countByCountry = tenists.groupBy { it.country }
        .mapValues { (_, tenists) -> tenists.size }
        .toList()
        .sortedByDescending { it.second }
    countByCountry.forEach { (country, count) ->
        println("$country: $count")
    }

    // 8. Número de tenistas agrupados por mano dominante y puntuación media
    println("\nNúmero de tenistas agrupados por mano dominante y puntuación media:")
    val averagePointsByDexterity = tenists.groupBy { it.dominantHand }
        .mapValues { (_, tenists) -> tenists.map { it.points }.average() }
    averagePointsByDexterity.forEach { (hand, avgPoints) ->
        println("$hand: $avgPoints")
    }

    // 9. Puntuación total de los tenistas agrupados por país
    println("\nPuntuación total de los tenistas agrupados por país:")
    val totalPointsByCountry = tenists.groupBy { it.country }
        .mapValues { (_, tenists) -> tenists.sumOf { it.points } }
    totalPointsByCountry.forEach { (country, totalPoints) ->
        println("$country: $totalPoints")
    }

    // 10. País con más puntuación total
    val countryWithMostPoints = totalPointsByCountry.maxByOrNull { it.value }
    println("\nPaís con más puntuación total: ${countryWithMostPoints?.key} con ${countryWithMostPoints?.value} puntos")

    // 11. Tenista con mejor ranking de España
    val bestRankedSpanishTenist = spanishTenists.maxByOrNull { it.points }
    println("\nTenista con mejor ranking de España: ${bestRankedSpanishTenist?.name} con ${bestRankedSpanishTenist?.points} puntos")
}

