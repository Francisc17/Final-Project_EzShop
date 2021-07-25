package pt.ipc.estgoh.ezshop.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import pt.ipc.estgoh.ezshop.data.anotations.Exclude;

public class ShoppingList implements Serializable {
    private long id;
    private String name;
    private String description;
    @SerializedName("shared")
    private boolean isShared;
    private List<String> users;
    private List<Category> itemsGroupByCategory;

    public ShoppingList() {
    }

    public ShoppingList(final String aName, final String aDescription) {
        this.name = aName;
        this.description = aDescription;
    }

    public ShoppingList(final long aId, final String aName, final String aDescription, final boolean aIsShared) {
        this.id = aId;
        this.name = aName;
        this.description = aDescription;
        this.isShared = aIsShared;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String aName) {
        this.name = aName;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String aDescription) {
        this.description = aDescription;
    }

    public void setShared(final boolean aShared) {
        isShared = aShared;
    }

    public boolean getIsShared() {
        return this.isShared;
    }

    public long getItemCounter() {
        return this.calculateHowManyItems();
    }

    public long getItemsChecked() {
        return this.itemsGroupByCategory.stream().map(Category::getCategoryItems)
                .mapToLong(listItems -> listItems.stream().filter(ListItem::isChecked).count()).sum();
    }

    public List<Category> getItemsGroupByCategory() {
        return this.itemsGroupByCategory;
    }

    public List<String> getUsers() {
        return this.users;
    }

    private long calculateHowManyItems() {
        return this.itemsGroupByCategory.stream().map(Category::getCategoryItems).mapToInt(List::size).sum();
    }

    @Override
    public String toString() {
        return "ShoppingList{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", isShared=" + isShared +
                ", itemsChecked=" + getItemsChecked() +
                ", itemsGroupByCategory=" + itemsGroupByCategory +
                '}';
    }
}
