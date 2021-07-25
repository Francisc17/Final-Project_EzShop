package pt.ipc.estgoh.ezshop.data.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Item implements Serializable {
    private long id;
    private String name;
    @SerializedName("category_id")
    private long category;

    public Item() {
    }

    public Item(final String aName, final long aCategory) {
        this.name = aName;
        this.category = aCategory;
    }

    public void setName(final String aName) {
        this.name = aName;
    }

    public String getName() {
        return this.name;
    }

    public void setCategory(final long aCategory) {
        this.category = aCategory;
    }

    public long getCategory() {
        return this.category;
    }

    @Override
    public String toString() {
        return "Item{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                '}';
    }
}
