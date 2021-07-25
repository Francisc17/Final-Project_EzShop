package pt.ipc.estgoh.francisco_luis.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.auth.jwt.*
import io.ktor.features.*
import io.ktor.http.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.json.simple.ItemList
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import pt.ipc.estgoh.francisco_luis.data.List
import pt.ipc.estgoh.francisco_luis.data.ListItem
import pt.ipc.estgoh.francisco_luis.services.ListService
import pt.ipc.estgoh.francisco_luis.utils.JSend
import java.sql.BatchUpdateException
import java.sql.SQLIntegrityConstraintViolationException
import java.util.NoSuchElementException


fun Route.listController() {

    val listService by closestDI().instance<ListService>()

    route("/lists") {

        authenticate {

            post {
                val listsRequests = call.receive<List>()

                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()

                try {
                    //static user id for now
                    listService.addList(listsRequests, id)
                    call.respond(JSend.success(201, null))
                } catch (e: ExposedSQLException) {
                    call.respond(JSend.error(400, "Problema ao adicionar lista", null))
                }
            }

            post("/{id}/items") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val itemList = call.receive<ListItem>()

                try {
                    listService.addNewItemToList(id, itemList)
                    call.respond(JSend.success(201, null))
                } catch (e: ExposedSQLException) {
                    when (e.cause) {
                        is BatchUpdateException -> {
                            listService.associateItemToList(id, itemList)
                            call.respond(JSend.success(201, null))
                        }
                        else -> {
                            call.respond(JSend.error(400, "Problema a inserir o item na lista", null))
                        }
                    }
                }
            }

            patch("/{id}/items") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val itemList = call.receive<ListItem>()

                try {
                    val entity = listService.associateItemToList(id, itemList)
                    call.respond(JSend.success(201, entity.toListItem()))
                } catch (e: ExposedSQLException) {
                    call.respond(JSend.error(400, "Problema ao associar item à lista", null))
                }
            }

            put("/{listID}/items") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val itemList = call.receive<ListItem>()

                try {
                    listService.updateItemInList(id, itemList)
                    call.respond(JSend.success(200, null))
                } catch (e: ExposedSQLException) {
                    call.respond(JSend.error(400, "Problema ao alterar o item da lista", null))
                }
            }

            put("/{id}") {

                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val listReceived = call.receive<List>()

                try {
                    listService.updateList(id,listReceived)
                    call.respond(JSend.success(200,null))
                } catch (e: ExposedSQLException) {
                    call.respond(JSend.error(400, "Problema ao alterar lista", null))
                }
            }

            post("/{id}/share"){
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val listId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                val dataReceived = call.receiveParameters()["email"] ?: throw NotFoundException()

                try {
                    listService.shareList(id,dataReceived,listId)
                    call.respond(JSend.success(200,null))
                } catch (ex: ExposedSQLException) {
                    call.respond(JSend.error(400,"Utilizador já associado à lista",null))
                } catch (ex: NoSuchElementException) {
                    call.respond(JSend.error(404,"Utilizador não existe",null))
                }
            }

            get("{id}") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val listId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                try {
                    call.respond(JSend.success(200, listService.getUserListById(id, listId)))
                } catch (ex: NoSuchElementException) {
                    call.respond(HttpStatusCode.NotFound,JSend.error(404,null,null))
                } catch (ex: ExposedSQLException) {
                    call.respond(JSend.error(400, "Ocorreu um erro ao obter lista", null))
                }
            }

            get {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()

                try {
                    val listsRequested = listService.getListsOfUser(id)

                    call.respond(JSend.success(200, listsRequested))
                } catch (ex: ExposedSQLException) {
                    call.respond(JSend.error(400, "ocorreu um erro", null))
                }
            }

            delete("/{id}") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val listId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                try {
                    listService.removeList(id,listId)
                    call.respond(JSend.success(200, null))
                } catch (ex: ExposedSQLException) {
                    call.respond(JSend.error(400, "ocorreu um erro a eliminar a lista", null))
                }
            }

            delete("/{id}/items/{itemId}") {
                val principal = call.authentication.principal<JWTPrincipal>()
                val id = principal!!.payload.subject.removePrefix("Bearer").toInt()
                val listId = call.parameters["id"]?.toIntOrNull() ?: throw NotFoundException()
                val itemId = call.parameters["itemId"]?.toIntOrNull() ?: throw NotFoundException()


                try {
                    listService.removeItemFromList(id,listId,itemId)
                    call.respond(JSend.success(200,null))
                }catch (ex: ExposedSQLException){
                    call.respond(JSend.error(400,"Ocorreu um erro ao eliminar o item",null))
                }
            }
        }
    }

}
