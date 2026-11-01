package nl.topicus.demo.reflection;

public class Person {

    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String greet() {
        return "Hi, I am " + name;
    }

    public String birthdayMessage(int years) {
        return name + " turns " + (age + years);
    }

    @Deprecated
    public String oldGreeting() {
        return "Hello, " + name;
    }

    public void explode() {
        throw new IllegalStateException("Something went wrong inside Person");
    }
}
