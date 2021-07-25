package pt.ipc.estgoh.francisco_luis

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.features.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import org.mindrot.jbcrypt.BCrypt
import pt.ipc.estgoh.francisco_luis.data.User
import pt.ipc.estgoh.francisco_luis.plugins.generateToken
import pt.ipc.estgoh.francisco_luis.services.AuthenticationService
import pt.ipc.estgoh.francisco_luis.utils.JSend

fun Route.userController() {

    val authService by closestDI().instance<AuthenticationService>()

    route("/users") {

        post("/login") {
            val dataReceived = call.receiveParameters()

            val user = authService.getUser(dataReceived["email"]!!)

            if (user != null)
                if (BCrypt.checkpw(dataReceived["password"], user.password)) {
                    call.respond(JSend.success(200, generateToken(user.id!!)))
                    return@post
                }


            call.respond(JSend.error(401, "Email ou password incorreto", null))
        }

        //insert user
        post {
            val userRequest = call.receive<User>()

            try {
                val user = authService.addUser(userRequest).toUser()
                call.respond(JSend.success(201, generateToken(user.id!!)))
            } catch (e: ExposedSQLException) {
                call.respond(JSend.error(406, "Email fornecido já está a ser utilizado", null))
            }
        }

        authenticate {
            //get all users
            get {
                call.respond(JSend.success(authService.getAllUsers()))
            }


            //get user
            get("/{id}") {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                try {
                    val user = authService.getUser(userId)
                    call.respond(JSend.success(user))
                } catch (e: EntityNotFoundException) {
                    call.respond(JSend.error("Não foi encontrado nenhum utilizador com o id $userId"))
                }
            }


            //change password
            put("/{id}/password") {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                val newPassword = call.receiveParameters()["password"]

                if (newPassword.isNullOrEmpty())
                    call.respond(JSend.error("Não foi forecida a nova password"))

                try {
                    authService.changeUserPassword(newPassword!!, userId)
                    call.respond(JSend.success(null))
                } catch (e: EntityNotFoundException) {
                    call.respond(JSend.error("Não existe nenhuma entidade com o id fornecido"))
                }
            }
        }
    }


}