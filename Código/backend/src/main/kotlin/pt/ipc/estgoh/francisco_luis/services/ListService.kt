package pt.ipc.estgoh.francisco_luis.services

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.json.simple.ItemList
import pt.ipc.estgoh.francisco_luis.data.*
import pt.ipc.estgoh.francisco_luis.data.List

class ListService {

    fun updateItemInList(aUserId: Int, aListItem: ListItem) = transaction {
        UserListEntity.find {
            UserLists.listId eq aListItem.listId and (UserLists.userId eq aUserId)
        }.first().apply {
            val listEntity = ListItemEntity[aListItem.id!!]
            listEntity.isChecked = aListItem.isChecked
            listEntity.additionalInfo = aListItem.additionalInfo
            listEntity.itemQuantity = aListItem.itemQuantity
            listEntity.isChecked = aListItem.isChecked
        }
    }

    fun addNewItemToList(aUserId: Int, aListItem: ListItem) = transaction {
        UserListEntity.find {
            UserLists.listId eq aListItem.listId and (UserLists.userId eq aUserId)
        }.first().apply {
            val listId = this.listId

            ItemEntity.new {
                this.name = aListItem.item!!.name
                this.category = aListItem.item?.category_id!!
            }.apply {
                val itemId = this.id

                ListItemEntity.new {
                    this.listId = listId
                    this.itemId = itemId
                    this.additionalInfo = aListItem.additionalInfo
                    this.itemQuantity = aListItem.itemQuantity
                    this.isChecked = false
                }
            }
        }
    }

    fun associateItemToList(aUserId: Int, aListItem: ListItem) = transaction {

        val listItemEntity: ListItemEntity

        UserListEntity.find {
            UserLists.userId eq aUserId and (UserLists.listId eq aListItem.listId)
        }.first().apply {
            val listId = this.listId
            ItemEntity.find {
                Items.name like aListItem.item!!.name
            }.first().apply {
                val itemId = this.id
                listItemEntity = ListItemEntity.new {
                    this.itemId = itemId
                    this.listId = listId
                    this.itemQuantity = aListItem.itemQuantity
                    this.additionalInfo = aListItem.additionalInfo
                    this.isChecked = false
                }
            }
        }

        listItemEntity
    }


    fun shareList(aUserId: Int, aUserMail: String, listId: Int) = transaction {

        UserListEntity.find {
            UserLists.userId eq aUserId and (UserLists.listId eq listId)
        }.first().apply {
            val listId = this.listId
            UserEntity.find {
                Users.email eq aUserMail
            }.first().apply {
                val userId = this.id
                UserListEntity.new {
                    this.userId = userId
                    this.listId = listId
                }
            }
        }
    }

    fun getUserListById(aUserId: Int, aListId: Int) = transaction {
        val query = Lists.innerJoin(UserLists)
            .slice(Lists.columns)
            .select {
                UserLists.userId eq aUserId and (UserLists.listId eq aListId)
            }.withDistinct()

        val list = ListEntity.wrapRows(query).first().toList()

        val items = Items.innerJoin(ListItems)
            .select {
                ListItems.listId eq list.id
            }.withDistinct()


        val itemsList = ItemEntity.wrapRows(items).toList().map(ItemEntity::toItem).toList()
        val listItems = ListItemEntity.wrapRows(items).toList().map(ListItemEntity::toListItem).toList()

        listItems.map { it.item = itemsList.first { item -> item.id == it.itemId } }

        val categoriesWithItemsGrouped = itemsList.map { CategoryEntity[it.category_id!!].toCategory() }.distinct()

        categoriesWithItemsGrouped.map {
            it.items = listItems.filter { listItem -> listItem.item?.category_id == it.id }
        }

        list.shared = UserLists.select{UserLists.listId eq list.id}.count() > 1

        val users = Users.innerJoin(UserLists).slice(Users.columns).select{
            UserLists.listId eq list.id and (UserLists.userId neq aUserId)
        }.withDistinct()

        list.users = UserEntity.wrapRows(users).toList().map(UserEntity::toUser).toList().map(User::name)

        list.itemsGroupByCategory = categoriesWithItemsGrouped

        list
    }

    fun getListsOfUser(aUserId: Int) = transaction {

        val query = Lists.innerJoin(UserLists)
            .slice(Lists.columns)
            .select {
                UserLists.userId eq aUserId
            }.withDistinct()

        val lists = ListEntity.wrapRows(query).toList().map(ListEntity::toList).toList()

        lists.forEach { list ->

            val items = Items.innerJoin(ListItems)
                .select {
                    ListItems.listId eq list.id
                }.withDistinct()


            val itemsList = ItemEntity.wrapRows(items).toList().map(ItemEntity::toItem).toList()
            val listItems = ListItemEntity.wrapRows(items).toList().map(ListItemEntity::toListItem).toList()

            listItems.map { it.item = itemsList.first { item -> item.id == it.itemId } }

            val categoriesWithItemsGrouped =
                itemsList.map { CategoryEntity[it.category_id!!].toCategory() }.distinct()

            categoriesWithItemsGrouped.map {
                it.items = listItems.filter { listItem -> listItem.item?.category_id == it.id }
            }

            list.shared = UserLists.select{UserLists.listId eq list.id}.count() > 1

            val users = Users.innerJoin(UserLists).slice(Users.columns).select{
                UserLists.listId eq list.id and (UserLists.userId neq aUserId)
            }.withDistinct()

            list.users = UserEntity.wrapRows(users).toList().map(UserEntity::toUser).toList().map(User::name)


            list.itemsGroupByCategory = categoriesWithItemsGrouped
        }

        lists
    }

    fun addList(aList: List, aUserId: Int) {
        val listCreated = transaction {
            ListEntity.new {
                this.name = aList.name
                this.description = aList.description
            }
        }

        transaction {
            UserEntity[aUserId].lists =
                SizedCollection(UserEntity[aUserId].lists + listCreated)
        }
    }

    fun updateList(aUserId: Int, aList: List) = transaction {
        UserListEntity.find {
            UserLists.userId eq aUserId and (UserLists.listId eq aList.id)
        }.first().apply {
            ListEntity.find {
                Lists.id eq aList.id
            }.first().apply {
                this.name = aList.name
                this.description = aList.description
            }
        }
    }


    fun removeItemFromList(aUserId: Int, aListId: Int, aId: Int) = transaction {
        UserListEntity.find {
            UserLists.listId eq aListId and (UserLists.userId eq aUserId)
        }.first().apply {
            ListItems.deleteWhere { ListItems.id eq aId }
        }
    }



    fun removeList(aUserId: Int, aId: Int) = transaction {
        UserListEntity.find {
            UserLists.listId eq aId and (UserLists.userId eq aUserId)
        }.first().apply {

            val isShared = UserLists.select{UserLists.listId eq aId}.count() > 1

            UserLists.deleteWhere { UserLists.listId eq aId and (UserLists.userId eq aUserId)}

            if (!isShared){
                ListItems.deleteWhere { ListItems.listId eq aId }
                Lists.deleteWhere { Lists.id eq aId }
            }
        }
    }
}