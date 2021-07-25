package pt.ipc.estgoh.ezshop.data.model;

public class User {
    private String name;
    private String email;
    private String password;

    public User(final String aName, final String aEmail, final String aPassword) {
        this.name = aName;
        this.email = aEmail;
        this.password = aPassword;
    }
}
