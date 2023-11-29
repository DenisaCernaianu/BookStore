package com.example.bookstore.Model;

public class Books {
    private String author, description, image, title,type, ownerNumber, price;

    public Books(){

    }

    public Books(String title, String author,String type, String description, String image, String price, String ownerNumber){
        this.title = title;
        this.author=author;
        this.type=type;
        this.description=description;
        this.image=image;
        this.price=price;
        this.ownerNumber=ownerNumber;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getOwnerNumber() {
        return ownerNumber;
    }

    public void setOwnerNumber(String ownerNumber) {
        this.ownerNumber = ownerNumber;
    }
}
