package pt.ipc.estgoh.ezshop.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    private final long id;
    private final String name;
    @SerializedName("items")
    private List<ListItem> categoryItems;

    public Category(final long aId, final String aName) {
        this.id = aId;
        this.name = aName;
    }

    public Category(final long aId, final String aName, final List<ListItem> aCategoryItems) {
        this.id = aId;
        this.name = aName;
        this.categoryItems = aCategoryItems;
    }

    public long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public List<ListItem> getCategoryItems() {
        return categoryItems;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", categoryItems=" + categoryItems +
                '}';
    }
}