package pt.ipc.estgoh.francisco_luis.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

//Representation of the database table
object Items : IntIdTable() {
    val name = varchar("name", 50).uniqueIndex()
    val categoryId = integer("category_id").references(Categories.id)
}


class ItemEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ItemEntity>(Items)

    var name by Items.name
    var category by Items.categoryId

    override fun toString(): String = "Item($name, $category)"

    fun toItem() = Item(id.value, name, category)
}

@Serializable
data class Item(
    //id is not mandatory
    val id: Int? = null,
    val name: String,
    val category_id: Int? = null,
)