package pt.ipc.estgoh.francisco_luis.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import kotlin.collections.List

//Representation of the database table
object Categories : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
}

class CategoryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CategoryEntity>(Categories)

    var name by Categories.name

    override fun toString(): String = "Category($name)" //do not put password here

    fun toCategory() = Category(id.value, name)
}

@Serializable
data class Category(
    //id is not mandatory
    val id: Int? = null,
    val name: String,
    var items: List<ListItem>? = null
)