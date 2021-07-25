package pt.ipc.estgoh.ezshop.data.model;

import java.io.Serializable;

public class ListItem implements Serializable {
    private long id;
    private long listId;
    private Long itemId;
    private int itemQuantity;
    private String additionalInfo;
    private boolean isChecked;
    private Item item;

    public ListItem() {
    }

    public ListItem(long listId, Long itemId, int itemQuantity, String additionalInfo, Item item) {
        this.listId = listId;
        this.itemId = itemId;
        this.itemQuantity = itemQuantity;
        this.additionalInfo = additionalInfo;
        this.isChecked = false;
        this.item = item;
    }

    public void setId(final long aId) {
        this.id = aId;
    }

    public long getId() {
        return id;
    }

    public long getListId() {
        return listId;
    }

    public void setListId(long listId) {
        this.listId = listId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public int getItemQuantity() {
        return itemQuantity;
    }

    public void setItemQuantity(int itemQuantity) {
        this.itemQuantity = itemQuantity;
    }

    public String getAdditionalInfo() {
        return additionalInfo;
    }

    public void setAdditionalInfo(String additionalInfo) {
        this.additionalInfo = additionalInfo;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    @Override
    public String toString() {
        return "ListItem{" +
                "id=" + id +
                ", listId=" + listId +
                ", itemId=" + itemId +
                ", itemQuantity=" + itemQuantity +
                ", additionalInfo='" + additionalInfo + '\'' +
                ", isChecked=" + isChecked +
                ", item=" + item +
                '}';
    }
}
