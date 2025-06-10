package com.av.avmessenger;

public class RandomUser {
    public Name name;
    public String email;
    public Picture picture;

    public static class Name {
        public String first;
        public String last;
    }
    public static class Picture {
        public String large;
    }
}