import org.example.config.AppConfig
import org.example.database.DatabaseManager
import org.example.tenist.models.Dexterity
import org.example.tenist.models.Tenist
import org.example.tenist.repository.TenistRepositoryImpl
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.junit.jupiter.MockitoSettings
import org.mockito.kotlin.whenever
import org.mockito.quality.Strictness
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.test.assertNull

@ExtendWith(MockitoExtension::class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TenistRepositoryImplTest {

    @Mock
    private lateinit var config : AppConfig

    private lateinit var databaseManager: DatabaseManager
    private lateinit var tenistRepository: TenistRepositoryImpl

    @BeforeEach
    fun setUp() {
        // Mock the configuration to simulate database settings
        whenever(config.databaseUrl).thenReturn("jdbc:sqlite:test.db")
        whenever(config.databaseInMemory).thenReturn(false)
        whenever(config.databaseInit).thenReturn(true)
        whenever(config.databaseRemoveData).thenReturn(true)

        // Initialize the database manager and repository
        databaseManager = DatabaseManager(config)
        tenistRepository = TenistRepositoryImpl(databaseManager)
    }

    @Test
    fun `create should return the saved tenist when inserted successfully`() {
        // Arrange
        val tenist = createTestTenist()

        // Act
        val result = tenistRepository.create(tenist)

        // Assert
        assertNotNull(result)
        assertEquals(tenist.name, result?.name)
    }

    @Test
    fun `delete should return null when the tenist does not exist`() {
        // Arrange
        val id = 1

        // Act
        val result = tenistRepository.delete(id, false)

        // Assert
        assertNull(result)
    }

    @Test
    fun `delete should return the tenist when deleted successfully`() {
        // Arrange
        val tenist = createTestTenist()
        tenistRepository.create(tenist)

        // Act
        val result = tenistRepository.delete(tenist.id, true)

        // Assert
        assertNotNull(result)
        assertEquals(tenist.id, result?.id)
    }

    @Test
    fun `get should return null when the tenist does not exist`() {
        // Arrange
        val id = 1

        // Act
        val result = tenistRepository.get(id)

        // Assert
        assertNull(result)
    }

    @Test
    fun `get should return the tenist when it exists`() {
        // Arrange
        val tenist = createTestTenist()
        tenistRepository.create(tenist)

        // Act
        val result = tenistRepository.get(tenist.id)

        // Assert
        assertNotNull(result)
        assertEquals(tenist.name, result?.name)
    }

    @Test
    fun `getAll should return an empty list when no tenists exist`() {
        // Act
        val result = tenistRepository.getAll()

        // Assert
        assertTrue(result.isEmpty())
    }

    @Test
    fun `getAll should return all tenists in the database`() {
        // Arrange
        val tenist1 = createTestTenist()
        val tenist2 = createTestTenist()
        tenistRepository.create(tenist1)
        tenistRepository.create(tenist2)

        // Act
        val result = tenistRepository.getAll()

        // Assert
        assertEquals(2, result.size)
        assertTrue(result.any { it.id == tenist1.id })
        assertTrue(result.any { it.id == tenist2.id })
    }

    @Test
    fun `update should return null when the tenist does not exist`() {
        // Arrange
        val tenist = createTestTenist()

        // Act
        val result = tenistRepository.update(tenist)

        // Assert
        assertNull(result)
    }

    @Test
    fun `update should return the updated tenist when the tenist exists`() {
        // Arrange
        val tenist = createTestTenist()
        tenistRepository.create(tenist)

        val updatedTenist = tenist.copy(name = "Updated Name")

        // Act
        val result = tenistRepository.update(updatedTenist)

        // Assert
        assertNotNull(result)
        assertEquals(updatedTenist.name, result?.name)
    }

    private fun createTestTenist(
        name: String = "John Doe",
        country: String = "USA",
        weight: Int = 80,
        height: Double = 1.85,
        dominantHand: Dexterity = Dexterity.DIESTRO,
        points: Int = 1000,
        birthDate: LocalDate = LocalDate.of(1990, 1, 1),
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = LocalDateTime.now()
    ): Tenist {
        return Tenist(
            name = name,
            country = country,
            weight = weight,
            height = height,
            dominantHand = dominantHand,
            points = points,
            birthDate = birthDate,
            createdAt = createdAt,
            updatedAt = updatedAt,
            isDeleted = false
        )
    }
}
