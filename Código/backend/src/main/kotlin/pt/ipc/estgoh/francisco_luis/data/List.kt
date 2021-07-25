package pt.ipc.estgoh.francisco_luis.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable


//Representation of the database table
object Lists : IntIdTable() {
    val name = varchar("name", 30)
    val description = varchar("description", 150).nullable()
}


class ListEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ListEntity>(Lists)

    var name by Lists.name
    var description by Lists.description
    var items by ItemEntity via ListItems

    override fun toString(): String = "List($name, $description)"

    fun toList() = List(id.value, name, description)
}


@Serializable
data class List(
    //id is not mandatory
    val id: Int? = null,
    val name: String,
    val description: String?,
    var itemsGroupByCategory: kotlin.collections.List<Category>? = null,
    var shared: Boolean? = false,
    var users: kotlin.collections.List<String> ? = null
)