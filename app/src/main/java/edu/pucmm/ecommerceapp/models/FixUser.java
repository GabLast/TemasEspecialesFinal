package edu.pucmm.ecommerceapp.models;


import java.io.Serializable;
import java.util.Objects;

public class FixUser implements Serializable {

    public enum ROL {SELLER, CUSTOMER}

    private int uid;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private ROL rol;
    private String contact;
    private String birthday;
    private String photo;

    public FixUser() {
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public FixUser setFirstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public String getLastName() {
        return lastName;
    }

    public FixUser setLastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public FixUser setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public FixUser setPassword(String password) {
        this.password = password;
        return this;
    }

    public ROL getRol() {
        return rol;
    }

    public FixUser setRol(ROL rol) {
        this.rol = rol;
        return this;
    }

    public String getContact() {
        return contact;
    }

    public FixUser setContact(String contact) {
        this.contact = contact;
        return this;
    }

    public String getBirthday() {
        return birthday;
    }

    public FixUser setBirthday(String birthday) {
        this.birthday = birthday;
        return this;
    }

    public String getPhoto() {
        return photo;
    }

    public FixUser setPhoto(String photo) {
        this.photo = photo;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FixUser)) return false;
        FixUser fixUser = (FixUser) o;
        return getUid() == fixUser.getUid();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUid());
    }

    @Override
    public String toString() {
        return "Userr{" +
                "uid=" + uid +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", rol=" + rol +
                ", contact='" + contact + '\'' +
                ", birthday='" + birthday + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
