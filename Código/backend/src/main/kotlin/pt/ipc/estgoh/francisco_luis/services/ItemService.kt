package pt.ipc.estgoh.francisco_luis.services

import org.jetbrains.exposed.sql.transactions.transaction
import pt.ipc.estgoh.francisco_luis.data.*

class ItemService {

    fun getAllCategories(): List<Category> = transaction {
        CategoryEntity.all().map(CategoryEntity::toCategory).toList()
    }


    //this is not needed because there is no item creation without associate to a list
    fun addItem(aItem: Item) = transaction {
        ItemEntity.new {
            this.name = aItem.name
            //cast nullable int to a non-nullable int - (!!)
            this.category = aItem.category_id!!
        }
    }

    fun getAllItems(): Iterable<Item> = transaction {
        ItemEntity.all().map(ItemEntity::toItem)
    }

    fun searchItems(aName: String): Iterable<Item> = transaction {
        ItemEntity.find {
            Items.name like "%$aName%"
        }.map(ItemEntity::toItem)
    }

}