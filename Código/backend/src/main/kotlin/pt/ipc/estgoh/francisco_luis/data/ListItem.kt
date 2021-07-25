package pt.ipc.estgoh.francisco_luis.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object ListItems : IntIdTable() {
    val listId = reference("listId", Lists)
    val itemId = reference("itemId", Items)
    val itemQuantity = integer("quantity")
    val additionalInfo = varchar("additionalInfo", 512).nullable()
    val isChecked = bool("isChecked").default(false)
}

class ListItemEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ListItemEntity>(ListItems)

    var listId by ListItems.listId
    var itemId by ListItems.itemId
    var itemQuantity by ListItems.itemQuantity
    var additionalInfo by ListItems.additionalInfo
    var isChecked by ListItems.isChecked

    fun toListItem() = ListItem(id.value,listId.value, itemId.value, itemQuantity, additionalInfo,isChecked)
}


@Serializable
data class ListItem(
    val id: Int? = null,
    val listId: Int,
    val itemId: Int? = null,
    val itemQuantity: Int,
    val additionalInfo: String? = null,
    val isChecked: Boolean = false,
    var item: Item? = null
)
