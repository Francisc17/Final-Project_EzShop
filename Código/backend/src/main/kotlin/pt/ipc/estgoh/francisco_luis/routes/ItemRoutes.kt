package pt.ipc.estgoh.francisco_luis.routes

import io.ktor.application.*
import io.ktor.auth.*
import io.ktor.http.content.*
import io.ktor.request.*
import io.ktor.response.*
import io.ktor.routing.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI
import pt.ipc.estgoh.francisco_luis.data.Item
import pt.ipc.estgoh.francisco_luis.services.ItemService
import pt.ipc.estgoh.francisco_luis.utils.JSend
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.util.*

fun Route.itemController() {

    val itemService by closestDI().instance<ItemService>()

    route("/items") {

        authenticate {

            get("/categories") {
                try {
                    call.respond(JSend.success(200, itemService.getAllCategories()))
                } catch (e: ExposedSQLException) {
                    call.respond(JSend.error("Erro ao obter categorias"))
                }
            }

            get {
                val nameRequested = call.request.queryParameters["name"]

                val results: Iterable<Item> = if (nameRequested == null)
                    itemService.getAllItems()
                else
                    itemService.searchItems(nameRequested)

                call.respond(JSend.success(results))
            }

            post("/uploadPhoto") {

                // WARNING -> absolute path!
                //val path = "C:\\Users\\Lenovo_Legion_Y520\\PycharmProjects\\ProjectModel\\food_photos"

                val path = "/root/images/"

                val multipartData = call.receiveMultipart()

                var fileBytes: ByteArray? = null
                var productName = ""
                var fileName = ""

                multipartData.forEachPart { part ->
                    when (part) {
                        is PartData.FormItem -> {
                            productName = part.value
                        }
                        is PartData.FileItem -> {
                            fileName = part.originalFileName as String
                            fileBytes = part.streamProvider().readBytes()
                        }
                    }
                }

                val directory = File(Paths.get(path, productName).toAbsolutePath().toString())
                if (!directory.exists())
                    directory.mkdir()

                fileBytes?.let { it1 ->
                    File(
                        directory,
                        UUID.randomUUID().toString() + fileName
                    ).writeBytes(it1)
                }

                call.respond(JSend.success(null))
            }
        }
    }
}