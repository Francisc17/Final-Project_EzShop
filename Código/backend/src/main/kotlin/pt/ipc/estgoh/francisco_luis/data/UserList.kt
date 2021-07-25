package pt.ipc.estgoh.francisco_luis.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

//Representation of the database table
object UserLists : IntIdTable() {
    val userId = reference("userId", Users)
    val listId = reference("listId", Lists)
    init {
        index(true, userId, listId)
    }
}

class UserListEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserListEntity>(UserLists)

    var userId by UserLists.userId
    var listId by UserLists.listId

    fun toList() = UserList(id.value, listId.value, userId.value)
}

@Serializable
data class UserList(
    val id: Int,
    val listId: Int,
    val userId: Int
)
