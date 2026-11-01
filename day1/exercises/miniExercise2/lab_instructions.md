# Lab: Build Your Own Validator with Annotations and Reflection

## Scenario / Context

Your team maintains a registration form, and bad data keeps sneaking into the system: empty names, users aged 3, the occasional `null` where nobody expected one. Every developer wrote their own `if` checks, and each form validates slightly differently.

The tech lead wants a small, shared validation library in the style of Bean Validation: put an annotation on a field, call one method, and get a list of everything that is wrong. In this lab you build that library yourself. It is also a rehearsal for this afternoon, because the container you start building later follows the same pattern: read an annotation, inspect an object, apply behavior.

---

## Learning Goals

- Define custom annotations with the right retention and target
- Implement a validator that reads annotations and field values through reflection
- Debug an annotation that is silently ignored at runtime
- Evaluate which mistakes the compiler catches and which only show up at runtime
- Design the validator so that it works for any class, not only `User`
- Justify when reflection is an appropriate tool and when it is not

---

## Prerequisites

- Java 21 and Maven installed
- Basic JUnit 5 knowledge
- The morning sections on annotations and reflection
- The `morning-starter` project from the previous lab, or any Maven project with JUnit 5 (put your code in a new package, for example `nl.topicus.validator`)

---

# Lab Parts

The lab contains **3 parts**.

---

## Part 1: Define the annotations

### What you will do

Create two annotations, `@NotBlank` for `String` fields and `@Min` for `int` fields. `@Min` takes the minimum value as its single element.

### Success criteria

- Both annotations exist and can be placed on a field
- `@Min(18)` compiles without naming the element
- Placing `@NotBlank` on a class gives a compile error
- Both annotations can be read from a running program

### Hints

<details>
<summary>Hint 1</summary>

An annotation is declared like an interface, with an `@` in front of the keyword. Think about who needs to read the annotation, and when.

</details>

<details>
<summary>Hint 2</summary>

Two meta-annotations decide the last two criteria. One controls how long the annotation survives. The other controls where it may be placed.

</details>

<details>
<summary>Hint 3</summary>

An annotation element named `value` can be set without writing its name. Use that for the minimum in `@Min`.

</details>

<details>
<summary>Hint 4</summary>

The shape of `@Min` is:

```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Min {
    int value();
}
```

`@NotBlank` has the same two meta-annotations and no elements.

</details>

---

## Part 2: Write the validator

### What you will do

Create a `User` class with public fields `name` and `age`, annotated with `@NotBlank` and `@Min(18)`. Then write `Validator.validate(Object obj)`, which returns a `List<String>` with one message per violation.

### Success criteria

- A valid user (for example "Ada", 36) gives an empty list
- A user with a blank name and age 12 gives two messages
- Each message mentions the name of the field that failed
- A `null` name is reported the same way as an empty name
- `validate` works for a second class that you invent, without changing the validator

### Hints

<details>
<summary>Hint 1</summary>

The validator should not know anything about `User`. It receives an `Object` and asks that object's class what it looks like.

</details>

<details>
<summary>Hint 2</summary>

Look at `getClass().getDeclaredFields()`. For each `Field` you can ask whether an annotation is present, and you can read the value of the annotation itself.

</details>

<details>
<summary>Hint 3</summary>

`field.get(obj)` returns an `Object`, so you need to cast it before comparing. It also throws a checked exception, which you have to handle. For a validator, wrapping it in a runtime exception with a clear message is fine.

</details>

<details>
<summary>Hint 4</summary>

The skeleton of the loop is:

```java
for (Field field : obj.getClass().getDeclaredFields()) {
    if (field.isAnnotationPresent(NotBlank.class)) {
        // read the value, check for null or blank, add a message with field.getName()
    }
    if (field.isAnnotationPresent(Min.class)) {
        int min = field.getAnnotation(Min.class).value();
        // read the value, compare with min, add a message
    }
}
```

</details>

---

## Part 3: Test it and then break it

### What you will do

Write JUnit tests for the validator. After that, remove `@Retention(RUNTIME)` from `@Min` and see what happens to your tests.

### Success criteria

- A test with a valid user passes and a test with an invalid user passes
- The invalid-user test checks the number of messages and the field names in them
- Without `@Retention(RUNTIME)` on `@Min`, the age check no longer reports an error
- You can point to the line in your validator that no longer sees the annotation
- After restoring the annotation, all tests are green again

### Hints

<details>
<summary>Hint 1</summary>

Write the tests first for the cases from Part 2. They describe your validator better than comments do.

</details>

<details>
<summary>Hint 2</summary>

When you remove the retention, watch for what the compiler and the test runner say. Compare that with what you expected.

</details>

<details>
<summary>Hint 3</summary>

Check the result of `field.isAnnotationPresent(Min.class)` for the `age` field, with and without the retention. That single boolean explains what you see.

</details>

<details>
<summary>Hint 4</summary>

Without the retention, the default policy keeps the annotation in the class file but not at runtime. Reflection can only see annotations with `RetentionPolicy.RUNTIME`. Add a test that asserts the annotation is present on `User.age`, so this mistake fails loudly next time.

</details>

---

# Bonus Challenge (Optional)

Make the validator more general and more robust:

1. Add `@MaxLength(int value)` for `String` fields
2. Make the validator work for `private` fields without getters, using `setAccessible(true)`
3. Restructure the code so that adding a new annotation means adding a new class, and the `validate` method itself does not change

Think about what happens to your design when you have twenty annotations.

---

# Reflection Questions

### Implementation & Trade-offs

1. `@Target(ElementType.FIELD)` stops you from annotating a class, but it does not stop you from putting `@Min` on a `String` field. Where would you detect that mistake, and what does each option cost?
2. Your validator returns a `List<String>` instead of throwing on the first violation. What does the caller gain, and what does the validator have to do differently?
3. You used `getDeclaredFields()`. Which fields does it not return, and when would that matter for a `User` that extends another class?

### Production Readiness

4. The validator runs reflection on every call. What could you cache, and what would you have to watch out for when you do?
5. `setAccessible(true)` lets the validator read private fields. What are the risks when this library is used on classes you did not write?
6. A new developer adds `@NotBlank` to a field and nothing happens, because the retention is missing. What test or build check would catch this before it reaches production?

### Debugging & Problem Solving

7. In Part 3 the tests turned green for the wrong reason, or an expected error disappeared. How did you trace it back to a single annotation, and which signal was easiest to follow?
8. Which exception did you have to handle around `field.get`, and what would the message look like to the person reading the log? Rewrite it so it helps them.

### Adaptation / Transfer

9. Jackson, JPA and JUnit all read annotations at runtime. Pick one and describe which "annotation, inspect, behavior" steps it performs, in the same terms as your validator.
10. How would your design change if you needed to validate method parameters instead of fields, or if rules depended on other fields (for example, `endDate` after `startDate`)?

---
