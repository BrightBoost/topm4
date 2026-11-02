package nl.topicus.erasure;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class TypeErasureDemo {

    public static void main(String[] args) throws ReflectiveOperationException {
        demonstrateErasure();
        inspectFields();
        printFieldTypeArguments();
        inspectCheckoutConstructor();
        demonstrateNestedGenericTypes();
    }

    private static void demonstrateErasure() {
        System.out.println("=== Type erasure ===");

        List<String> names = new ArrayList<>();
        List<Integer> numbers = new ArrayList<>();

        System.out.println("String list class:  " + names.getClass());
        System.out.println("Integer list class: " + numbers.getClass());

        // The JVM sees both instances as ArrayList; their element types are erased.
        System.out.println("Same class: " + (names.getClass() == numbers.getClass()));
    }

    private static void inspectFields() {
        System.out.println("\n=== Field types ===");

        for (Field field : TypeHolder.class.getDeclaredFields()) {
            // getType() reports the raw class, while getGenericType() reads
            // the generic declaration retained in the class file.
            System.out.println(field.getName()
                    + " | raw: " + field.getType().getTypeName()
                    + " | generic: " + field.getGenericType().getTypeName());
        }
    }

    private static void printFieldTypeArguments() throws NoSuchFieldException {
        System.out.println("\n=== Field type arguments ===");

        for (String fieldName : List.of("names", "scores", "title")) {
            printTypeArguments(TypeHolder.class.getDeclaredField(fieldName));
        }
    }

    private static void printTypeArguments(Field field) {
        Type type = field.getGenericType();

        // Types such as String are not parameterized, so check before casting.
        if (!(type instanceof ParameterizedType parameterizedType)) {
            System.out.println(field.getName() + " -> no type arguments");
            return;
        }

        for (Type argument : parameterizedType.getActualTypeArguments()) {
            System.out.println(field.getName() + " -> " + argument.getTypeName());
        }
    }

    private static void inspectCheckoutConstructor() throws NoSuchMethodException {
        System.out.println("\n=== Checkout constructor ===");

        Constructor<Checkout> constructor =
                Checkout.class.getDeclaredConstructor(List.class, List.class);

        // A Class-keyed registry sees List for both constructor parameters.
        System.out.println("Raw parameter types: "
                + Arrays.toString(constructor.getParameterTypes()));

        // Generic declarations preserve which element type each list expects.
        System.out.println("Generic parameter types: "
                + Arrays.toString(constructor.getGenericParameterTypes()));
    }

    private static void demonstrateNestedGenericTypes() throws NoSuchFieldException {
        System.out.println("\n=== Nested generic types (bonus) ===");

        for (String fieldName : List.of("nested", "limits")) {
            Type type = TypeHolder.class.getDeclaredField(fieldName).getGenericType();
            System.out.println(fieldName + " -> " + describeType(type));
        }

        // Standard reflection does not expose a method's local variable types.
        List<String> localNames = List.of("Ada");
        System.out.println("Local variable example has " + localNames.size() + " value");
    }

    private static String describeType(Type type) {
        if (type instanceof ParameterizedType parameterizedType) {
            String arguments = Arrays.stream(parameterizedType.getActualTypeArguments())
                    .map(TypeErasureDemo::describeType)
                    .reduce((left, right) -> left + ", " + right)
                    .orElse("");

            return describeType(parameterizedType.getRawType()) + "<" + arguments + ">";
        }

        if (type instanceof WildcardType wildcardType) {
            Type[] lowerBounds = wildcardType.getLowerBounds();
            if (lowerBounds.length > 0) {
                return "? super " + describeType(lowerBounds[0]);
            }

            Type[] upperBounds = wildcardType.getUpperBounds();
            if (upperBounds.length == 0 || upperBounds[0] == Object.class) {
                return "?";
            }

            return "? extends " + describeType(upperBounds[0]);
        }

        if (type instanceof GenericArrayType arrayType) {
            return describeType(arrayType.getGenericComponentType()) + "[]";
        }

        return type.getTypeName();
    }

    private static class TypeHolder {
        private List<String> names;
        private Map<String, Integer> scores;
        private String title;

        // These declarations exercise nested types and wildcard bounds.
        private Map<String, List<Integer>> nested;
        private List<? extends Number> limits;
    }

    private static class PaymentService {
    }

    private static class EmailSender {
    }

    private static class Checkout {
        private Checkout(List<PaymentService> payments, List<EmailSender> senders) {
        }
    }
}
