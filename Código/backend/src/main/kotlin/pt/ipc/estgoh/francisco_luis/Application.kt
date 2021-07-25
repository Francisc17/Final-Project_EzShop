package pt.ipc.estgoh.francisco_luis

import io.ktor.application.*
import io.ktor.routing.*
import org.kodein.di.ktor.di
import pt.ipc.estgoh.francisco_luis.plugins.configureSecurity
import pt.ipc.estgoh.francisco_luis.plugins.configureSerialization
import pt.ipc.estgoh.francisco_luis.plugins.initDB
import pt.ipc.estgoh.francisco_luis.routes.itemController
import pt.ipc.estgoh.francisco_luis.routes.listController
import pt.ipc.estgoh.francisco_luis.services.bindServices

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

fun Application.module(testing: Boolean = false) {
    initDB()

    di {
        bindServices()
    }


    configureSecurity()

    routing {
        userController()
        itemController()
        listController()
    }

    configureSerialization()
}
