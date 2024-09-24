package org.example.tenist.repository

import org.example.database.DatabaseManager
import org.example.tenist.mappers.findDexteriry
import org.example.tenist.models.Tenist
import org.koin.core.annotation.Singleton
import org.lighthousegames.logging.logging
import java.time.LocalDate
import java.time.LocalDateTime

private val logger = logging()

@Singleton
class TenistRepositoryImpl(
    private val db: DatabaseManager
) : TenistRepository {

    /**
     * Inserta el tenista en la base de datos
     * @param tenist es el tenista que se quiere guardar en la base de datos
     * @return un objeto Tenista si ha podido meterlo en la base de datos o un null sino
     */
    override fun create(tenist: Tenist): Tenist? {
        logger.debug { "Intentando añadir eltenista con id: ${tenist.name}" }
        try {
            val sql = "INSERT INTO Tenist (id,name,country,weight,height,dominantHand,points,birthDate,createdAt,updatedAt,isDeleted) VALUES(?,?,?,?,?,?,?,?,?,?,?)"
            val preparedStatement = db.connection!!.prepareStatement(sql)
            preparedStatement.setInt(1,tenist.id)
            preparedStatement.setString(2, tenist.name)
            preparedStatement.setString(3, tenist.country)
            preparedStatement.setInt(4, tenist.weight)
            preparedStatement.setDouble(5, tenist.height)
            preparedStatement.setString(6, tenist.dominantHand!!.name)
            preparedStatement.setInt(7, tenist.points)
            preparedStatement.setString(8,tenist.birthDate.toString())
            preparedStatement.setString(9,tenist.createdAt.toString())
            preparedStatement.setString(10, tenist.updatedAt.toString())
            preparedStatement.setInt(11, if (tenist.isDeleted) 1 else 0)
            preparedStatement.executeUpdate()
            return tenist
        }catch (e : Exception){
            logger.error { "Error al insertar el tenista: ${e.message}" }
            return null
        }
        return null
    }

    /**
     * Elimina el tenista de la base de datos
     * @param id es el id del tenista que se quiere eliminar
     * @return un objeto Tenista si ha podido eliminarlo de la base de datos o un null sino
     */
    override fun delete(id: Int, logical : Boolean): Tenist? {
        logger.debug { "Intentando eliminar el tenista con id: $id" }
        try {
            if (get(id) == null) return null // Si no existe
            val sql : String
            sql = if (logical) "UPDATE Tenist SET isDeleted=true WHERE id=?"
            else "DELETE FROM Tenist WHERE id=?"
            val preparedStatement = db.connection!!.prepareStatement(sql)
            preparedStatement.setInt(1, id)
            preparedStatement.executeUpdate()
            return get(id)
        }catch (e : Exception) {
            logger.error { "Error al eliminar el tenista: ${e.message}" }
            return null
        }
        return null
    }

    /**
     * Actualiza el tenista en la base de datos
     * @param tenist es el tenista que se quiere actualizar en la base de datos
     * @return un objeto Tenista si ha podido actualizarlo en la base de datos o un null sino
     */
    override fun update(tenist: Tenist): Tenist? {
        logger.debug { "Intentando actualizar el tenista con id: ${tenist.id}" }
        try {
            if (get(tenist.id) == null) return null // Si no existe
            val timestamp = LocalDateTime.now()
            val sql = "UPDATE Tenist SET name=?, country=?, weight=?, height=?, dominantHand=?, points=?, birthDate=?, isDeleted=?, updatedAt=? WHERE id=?"
            val preparedStatement = db.connection!!.prepareStatement(sql)
            preparedStatement.setString(1, tenist.name)
            preparedStatement.setString(2, tenist.country)
            preparedStatement.setInt(3, tenist.weight)
            preparedStatement.setDouble(4, tenist.height)
            preparedStatement.setString(5, tenist.dominantHand!!.name)
            preparedStatement.setInt(6, tenist.points)
            preparedStatement.setString(7,tenist.birthDate.toString())
            preparedStatement.setString(8, tenist.updatedAt.toString())
            preparedStatement.setInt(9, if (tenist.isDeleted) 1 else 0)
            preparedStatement.setInt(10, tenist.id)
            preparedStatement.executeUpdate()
            return tenist.copy(updatedAt = timestamp)
        }catch (e: Exception){
            logger.error { "Error al actualizar el tenista: ${e.message}" }
            return null
        }
    }

    /**
     * Obtiene un tenista de la base de datos
     * @param id es el id del tenista que se quiere obtener
     * @return un objeto Tenista si ha podido obtenerlo de la base de datos o un null sino
     */
    override fun get(id: Int): Tenist? {
        logger.debug { "Buscando el tenista con id: $id" }
        try {
            val sql = "SELECT * FROM Tenist WHERE id=?"
            val preparedStatement = db.connection!!.prepareStatement(sql)
            preparedStatement.setInt(1, id)
            val result = preparedStatement.executeQuery()
            if (result.next()){
                return Tenist(
                    id = result.getInt("id"),
                    name = result.getString("name"),
                    country = result.getString("country"),
                    weight = result.getInt("weight"),
                    height = result.getDouble("height"),
                    dominantHand = findDexteriry(result.getString("dominantHand")),
                    points = result.getInt("points"),
                    birthDate = LocalDate.parse(result.getString("birthDate")),
                    createdAt = LocalDateTime.parse(result.getString("createdAt")),
                    updatedAt = LocalDateTime.parse(result.getString("updatedAt")),
                    isDeleted = result.getBoolean("isDeleted")
                )
            }
            return null
        }catch (e: Exception){
            logger.error { "Error al obtener el tenista: ${e.message}" }
            return null
        }
    }

    /**
     * Obtiene todos los tenistas de la base de datos
     * @return una lista de objetos Tenist si hay alguno y una lista vacia sino
     */
    override fun getAll(): List<Tenist> {
        logger.debug { "Buscando todos los tenistas en la base de datos" }
        try {
            val tenists = mutableListOf<Tenist>()
            val sql = "SELECT * FROM Tenist"
            val result = db.connection!!.createStatement().executeQuery(sql)
            while (result.next()){
                tenists.add(
                    Tenist(
                        id = result.getInt("id"),
                        name = result.getString("name"),
                        country = result.getString("country"),
                        weight = result.getInt("weight"),
                        height = result.getDouble("height"),
                        dominantHand = findDexteriry(result.getString("dominantHand")),
                        points = result.getInt("points"),
                        birthDate = LocalDate.parse(result.getString("birthDate")),
                        createdAt = LocalDateTime.parse(result.getString("createdAt")),
                        updatedAt = LocalDateTime.parse(result.getString("updatedAt")),
                        isDeleted = result.getBoolean("isDeleted")
                    )
                )
            }
            return tenists
        }catch (e: Exception){
            logger.error { "Error al obtener todos los tenistas: ${e.message}" }
            return emptyList()
        }
    }
}