package pt.ipc.estgoh.francisco_luis.services

import org.jetbrains.exposed.sql.transactions.transaction
import org.mindrot.jbcrypt.BCrypt
import pt.ipc.estgoh.francisco_luis.data.User
import pt.ipc.estgoh.francisco_luis.data.UserEntity
import pt.ipc.estgoh.francisco_luis.data.Users

class AuthenticationService {

    fun getAllUsers(): Iterable<User> = transaction {
        UserEntity.all().map(UserEntity::toUser)
    }

    fun getUser(id : Int): User = transaction {
        UserEntity[id].toUser()
    }

    fun getUser(email: String): User? = transaction {
        UserEntity.find {
            Users.email eq email
        }.map(UserEntity::toUser).firstOrNull()
    }

    fun addUser(user: User) = transaction {
        UserEntity.new {
            this.name = user.name
            this.email = user.email
            this.password = BCrypt.hashpw(user.password,BCrypt.gensalt())
        }
    }

    fun changeUserPassword(aPassword: String, userId: Int) = transaction {
        UserEntity[userId].password = aPassword
    }
}