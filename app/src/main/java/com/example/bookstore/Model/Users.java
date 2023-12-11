package com.example.bookstore.Model;

public class Users {
    private String username, email, phone, idUser;
    private  double password;

    public Users(){

    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public Users(String username, String email, String phone, String idUser, double password) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.idUser = idUser;
        this.phone = phone;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public double getPassword() {
        return password;
    }

    public void setPassword(double password) {
        this.password = password;
    }
}
