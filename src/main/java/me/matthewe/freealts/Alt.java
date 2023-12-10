package me.matthewe.freealts;

/**
 * Created by Matthew E on 10/30/2017.
 */
public class Alt {
    private String email;
    private String password;

    public Alt(String line) {
        this.email = line.split(":")[0];
        this.password = line.split(":")[1];
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
