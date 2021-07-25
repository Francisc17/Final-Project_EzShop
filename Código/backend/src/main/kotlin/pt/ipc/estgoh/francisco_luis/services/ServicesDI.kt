package pt.ipc.estgoh.francisco_luis.services

import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

fun DI.MainBuilder.bindServices() {
    //make RegisterService available in all the project
    bind<AuthenticationService>() with singleton { AuthenticationService() }
    bind<ItemService>() with singleton { ItemService() }
    bind<ListService>() with singleton { ListService() }
}