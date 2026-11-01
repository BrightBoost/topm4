# Lab: Type Erasure in Practice — What Is Left of Your Generics at Runtime?

## Scenario / Context

A colleague is designing the dependency container for your team and wants it to support constructors like `Checkout(List<PaymentService> services)`. They store beans in a `Map<Class, Object>` and assume the container can tell a `List<PaymentService>` from a `List<EmailSender>`. A quick code review comment from you could save them a week of debugging.

Before you write that comment, you want hard evidence. In this lab you find out what the JVM really knows about generic types, where that information still exists, and where it is gone for good. The answer decides what a container can and cannot inject.

---

## Learning Goals

- Demonstrate type erasure with a runnable experiment
- Use reflection to read generic type information from fields and constructor parameters
- Implement a helper that extracts the type arguments of a parameterized type
- Explain why a `Class`-keyed registry cannot distinguish `List<PaymentService>` from `List<EmailSender>`
- Evaluate which generic information survives in a class file and which does not
- Propose a design that supports generic dependencies in a container

---

## Prerequisites

- Java 21 and Maven installed
- The morning sections on generics, type erasure and reflection
- The `morning-starter` project from the first lab, or any Maven project (put your code in a new package, for example `nl.topicus.erasure`)
- Two empty classes `PaymentService` and `EmailSender` in your project, which you can use in Part 4

---

# Lab Parts

The lab contains **4 parts**.

---

## Part 1: Prove that erasure happens

### What you will do

Write a small `main` that shows that a `List<String>` and a `List<Integer>` have the same class at runtime.

### Success criteria

- The program prints the classes of both lists
- The program prints the result of comparing the two classes with `==`
- The output matches your prediction, which you wrote down before running it
- You can explain the result in one sentence

### Hints

<details>
<summary>Hint 1</summary>

Write down your prediction first. Then run the code and compare.

</details>

<details>
<summary>Hint 2</summary>

Every object can tell you its class at runtime. Ask both lists.

</details>

<details>
<summary>Hint 3</summary>

Compare the two `Class` objects with `==`. There is only one `Class` object per loaded class, so reference equality is meaningful here.

</details>

<details>
<summary>Hint 4</summary>

```java
List<String> strings = new ArrayList<>();
List<Integer> numbers = new ArrayList<>();
System.out.println(strings.getClass());
System.out.println(numbers.getClass());
System.out.println(strings.getClass() == numbers.getClass());
```

</details>

---

## Part 2: getType versus getGenericType

### What you will do

Create a class `TypeHolder` with the fields `List<String> names` and `Map<String, Integer> scores`. Use reflection to print what `getType()` and `getGenericType()` return for each field.

### Success criteria

- The output shows both methods for both fields
- `getType()` shows only the raw type (`List`, `Map`)
- `getGenericType()` shows the full declaration, including the type arguments
- You can describe the difference between the two in your own words

### Hints

<details>
<summary>Hint 1</summary>

Fields are part of the class declaration. Ask the class for them.

</details>

<details>
<summary>Hint 2</summary>

`getDeclaredFields()` returns the fields. Each `Field` has both methods you want to compare.

</details>

<details>
<summary>Hint 3</summary>

Loop over the fields and print the name, `getType()` and `getGenericType()` on one line each. The two printed types will look quite different for the same field.

</details>

<details>
<summary>Hint 4</summary>

```java
for (Field field : TypeHolder.class.getDeclaredFields()) {
    System.out.println(field.getName()
        + " | " + field.getType()
        + " | " + field.getGenericType());
}
```

</details>

---

## Part 3: Extract the type arguments

### What you will do

Write a method `printTypeArguments(Field f)` that prints the type arguments of a field with a parameterized type. Use it on both fields from Part 2.

### Success criteria

- For `names` it prints `String`
- For `scores` it prints `String` and `Integer`
- For a field without type arguments, for example `String title`, it prints a clear "no type arguments" message and does not crash
- The method works for any field, not only the two in `TypeHolder`

### Hints

<details>
<summary>Hint 1</summary>

`getGenericType()` returns a `Type`, which is a very general interface. Only some of its implementations carry type arguments.

</details>

<details>
<summary>Hint 2</summary>

Look at `java.lang.reflect.ParameterizedType`. It has a method that returns the actual type arguments.

</details>

<details>
<summary>Hint 3</summary>

Check with `instanceof` before you cast, so that a plain `String` field does not throw a `ClassCastException`.

</details>

<details>
<summary>Hint 4</summary>

```java
Type type = f.getGenericType();
if (type instanceof ParameterizedType parameterized) {
    for (Type argument : parameterized.getActualTypeArguments()) {
        System.out.println(f.getName() + " -> " + argument.getTypeName());
    }
} else {
    // field has no type arguments: say so
}
```

</details>

---

## Part 4: What does a container see?

### What you will do

Create a class `Checkout` with a constructor that takes `List<PaymentService> payments` and `List<EmailSender> senders`. Inspect that constructor with reflection, and compare what a registry keyed by `Class` can see with what the generic declaration still contains.

### Success criteria

- The output shows the parameter types of the constructor as raw `Class` objects
- The output shows the generic parameter types of the same constructor
- Both parameters look identical in the first output and different in the second
- You can state in one or two sentences why a `Map<Class, Object>` cannot choose the right list

### Hints

<details>
<summary>Hint 1</summary>

A container looking at a constructor sees its parameters. Ask the constructor for the types of those parameters.

</details>

<details>
<summary>Hint 2</summary>

`getParameterTypes()` returns `Class<?>[]`. There is a second method on `Constructor` with the word "generic" in its name.

</details>

<details>
<summary>Hint 3</summary>

`getDeclaredConstructors()[0]` is enough here, since `Checkout` has one constructor. Print both arrays, one after the other, and compare.

</details>

<details>
<summary>Hint 4</summary>

```java
Constructor<?> constructor = Checkout.class.getDeclaredConstructors()[0];
System.out.println(Arrays.toString(constructor.getParameterTypes()));
System.out.println(Arrays.toString(constructor.getGenericParameterTypes()));
```

</details>

---

# Bonus Challenge (Optional)

Write a method `describeType(Type type)` that prints nested generic types properly, for example `Map<String, List<Integer>>`. Think about what your method should do when it meets a wildcard like `List<? extends Number>`.

Then try to get the generic type of a local variable inside a method with reflection. Explain why this cannot work.

---

# Reflection Questions

### Implementation & Trade-offs

1. Generics were designed with erasure to stay compatible with older code. What does that choice cost a framework author today, and what did it save the Java ecosystem?
2. In Part 3 you check with `instanceof` before casting. What kinds of `Type` could `getGenericType()` return that are not a `ParameterizedType`?
3. A `Map<Class, Object>` is simple and fast. What would a registry keyed by full generic type look like, and what would it make harder?

### Production Readiness

4. A developer injects `List<PaymentService>` into a bean and your container silently passes a `List<EmailSender>`. At which point would this fail in production, and how would you want the container to report it?
5. Frameworks use type tokens such as `TypeLiteral` for this problem. What does such a token capture that a plain `Class` cannot, and what does it cost the person writing the code?

### Debugging & Problem Solving

6. In Part 1 your prediction and the output either matched or did not. Which wrong assumption about generics did you have to fix, if any?
7. If `printTypeArguments` crashes with a `ClassCastException` on a plain field, what does that tell you about the difference between `getType()` and `getGenericType()`?

### Adaptation / Transfer

8. Jackson can fill a `List<Customer>` field, but needs a `TypeReference` when you ask it to read a `List<Customer>` from JSON without a field. How does what you saw in Part 2 and Part 4 explain that difference?
9. Your container only supports non-generic beans today. Sketch how you would extend it to inject a `List<PaymentService>` constructor parameter, and which part of the reflection API from this lab you would use.

---
