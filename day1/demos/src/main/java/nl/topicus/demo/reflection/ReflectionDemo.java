package nl.topicus.demo.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class ReflectionDemo {

    public static void main(String[] args) throws Exception {
        step1Inspect();
        Object person = step2CreateObject();
        step3ReadPrivateField(person);
        step4InvokeMethod(person);
        step5WrongConstructor();
        step6InvocationTargetException(person);
    }

    private static void step1Inspect() {
        header("Step 1: inspect the class");
        Class<?> type = Person.class;
        System.out.println("Name: " + type.getName());

        System.out.println("Fields:");
        for (Field field : type.getDeclaredFields()) {
            System.out.println("  " + field.getType().getSimpleName() + " " + field.getName());
        }

        System.out.println("Constructors:");
        for (Constructor<?> constructor : type.getDeclaredConstructors()) {
            System.out.println("  " + constructor.getParameterCount() + " parameter(s)");
        }

        System.out.println("Methods:");
        for (Method method : type.getDeclaredMethods()) {
            String info = "  " + method.getReturnType().getSimpleName() + " " + method.getName();
            if (Modifier.isStatic(method.getModifiers())) {
                info += " (static)";
            }
            if (method.isAnnotationPresent(Deprecated.class)) {
                info += " (@Deprecated)";
            }
            System.out.println(info);
        }
    }

    private static Object step2CreateObject() throws Exception {
        header("Step 2: create an object with newInstance");
        Constructor<?> constructor = Person.class.getDeclaredConstructor(String.class, int.class);
        Object person = constructor.newInstance("Ada", 36);
        System.out.println("Created: " + person.getClass().getSimpleName());
        return person;
    }

    private static void step3ReadPrivateField(Object person) throws Exception {
        header("Step 3: read a private field");
        Field field = Person.class.getDeclaredField("name");
        try {
            field.get(person);
        } catch (IllegalAccessException e) {
            System.out.println("Without setAccessible: " + e.getClass().getSimpleName());
        }
        field.setAccessible(true);
        System.out.println("With setAccessible(true): name = " + field.get(person));
    }

    private static void step4InvokeMethod(Object person) throws Exception {
        header("Step 4: call a method with invoke");
        Method greet = Person.class.getDeclaredMethod("greet");
        System.out.println("greet() -> " + greet.invoke(person));

        Method birthday = Person.class.getDeclaredMethod("birthdayMessage", int.class);
        System.out.println("birthdayMessage(1) -> " + birthday.invoke(person, 1));
    }

    private static void step5WrongConstructor() {
        header("Step 5: ask for a constructor that does not exist");
        try {
            Person.class.getDeclaredConstructor(String.class);
        } catch (NoSuchMethodException e) {
            System.out.println("Caught " + e.getClass().getSimpleName() + ": " + e.getMessage());
        }
    }

    private static void step6InvocationTargetException(Object person) throws Exception {
        header("Step 6: the real error is wrapped");
        Method explode = Person.class.getDeclaredMethod("explode");
        try {
            explode.invoke(person);
        } catch (InvocationTargetException e) {
            System.out.println("Outer exception: " + e);
            System.out.println("Message of outer exception: " + e.getMessage());
            System.out.println("getCause(): " + e.getCause());
        }
    }

    private static void header(String title) {
        System.out.println();
        System.out.println("=== " + title + " ===");
    }
}
