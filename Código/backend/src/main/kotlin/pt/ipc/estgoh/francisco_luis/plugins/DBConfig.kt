package pt.ipc.estgoh.francisco_luis.plugins

import com.zaxxer.hikari.HikariDataSource
import io.ktor.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import pt.ipc.estgoh.francisco_luis.data.*

//const val HIKARI_CONFIG_KEY = "ktor.hikariconfig"

fun Application.initDB() {
    //val configPath = "dbconfig.properties"
    //val dbConfig = HikariConfig(configPath)
    val dataSource = HikariDataSource().apply {
        username = "projeto"
        password = "projeto"
        driverClassName = "com.mysql.cj.jdbc.Driver"
        jdbcUrl = "jdbc:mysql://localhost:3306/projeto"
    }
    Database.connect(dataSource)
    createTables()
    LoggerFactory.getLogger(Application::class.simpleName).info("Initialized Database")
}

private fun createTables() = transaction {
    SchemaUtils.create(
        Users,
        Items,
        Categories,
        UserLists,
        ListItems
    )
}