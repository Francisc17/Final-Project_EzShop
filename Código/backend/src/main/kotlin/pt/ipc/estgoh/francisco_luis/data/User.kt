package pt.ipc.estgoh.francisco_luis.data

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

//Representation of the database table
object Users: IntIdTable(){
    val name = varchar("name", 50)
    val email = varchar("email", 254).uniqueIndex()
    val password = varchar("password",254)
}


class UserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserEntity>(Users)

    var name by Users.name
    var email by Users.email
    var password by Users.password
    var lists by ListEntity via UserLists

    override fun toString(): String = "User($name, $email)" //do not put password here

    fun toUser() = User(id.value, name, email, password)
}

@Serializable
data class User(
    //id and token is not mandatory
    val id: Int? = null,
    val name: String,
    val email: String,

    //password can be changed = use var
    var password: String
)