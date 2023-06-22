package top.dreamlike.lib;

import io.vertx.core.Vertx;

public class Person {
    public String name;
    public String password;

    public Vertx vertx = Vertx.vertx();
}
