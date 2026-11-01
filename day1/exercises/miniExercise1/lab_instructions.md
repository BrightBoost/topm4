# Lab: Wiring by Hand — Constructor Injection Without a Container

## Scenario / Context

The finance team gets a nightly report, built by `ReportService`. Last week the build server went down for two hours, and the cause turned out to be a unit test for that service: it tried to open a real database connection that no longer existed.

Your team lead has asked you to make `ReportService` testable without a database, and to do it without adding a framework. You will fix the problem with nothing more than constructors, and along the way you will discover how much work it is to connect objects yourself. That second part is the reason a container exists.

---

## Learning Goals

- Refactor classes that create their own collaborators to constructor injection
- Wire an object graph by hand in `main`, in a valid creation order
- Write a unit test for `ReportService` using a fake collaborator
- Analyze what changes in your wiring code when a class gets a new dependency
- Evaluate which parts of manual wiring a container could take over
- Justify why a circular dependency cannot be solved by constructor injection alone

---

## Prerequisites

- Java 21 and Maven installed
- Basic JUnit 5 knowledge (`@Test`, `assertEquals`)
- Familiarity with interfaces
- The follow-along demo from this morning (from `new` to constructor injection)
- The starter project in `module4/day1/exercises/morning-starter`

The starter contains four classes in package `nl.topicus.wiring` that create each other with `new`:

```
ReportService -> ReportRepository -> DataSourceProvider
              -> Formatter
```

`Main` builds a `ReportService` and prints a report. You can run it with:

```bash
mvn -q compile exec:java -Dexec.mainClass=nl.topicus.wiring.Main
```

---

# Lab Parts

The lab contains **3 parts**.

---

## Part 1: Refactor to constructor injection

### What you will do

Change `ReportService` and `ReportRepository` so that they receive their collaborators instead of creating them. Then build the object graph yourself in `Main`.

### Success criteria

- No class creates one of its collaborators with `new`, except `Main`
- The collaborator fields are `final`
- `Main` prints the same report as before (a `NAME;AGE` header and three rows)
- You can tell which object `Main` has to create first, without trying

### Hints

<details>
<summary>Hint 1</summary>

For each class, ask two questions. What does it need? And who should decide which instance it gets?

</details>

<details>
<summary>Hint 2</summary>

Look at the fields in `ReportService` and `ReportRepository` that are initialized with `new`.

</details>

<details>
<summary>Hint 3</summary>

Turn each of those fields into a constructor parameter. In `Main`, create the classes without dependencies first, and work your way up to `ReportService`.

</details>

<details>
<summary>Hint 4</summary>

A constructor for `ReportService` could look like this:

```java
public ReportService(ReportRepository repository, Formatter formatter) {
    this.repository = repository;
    this.formatter = formatter;
}
```

In `Main`, the order is: `DataSourceProvider`, then `ReportRepository`, then `Formatter`, then `ReportService`.

</details>

---

## Part 2: Test with a fake repository

### What you will do

Write a unit test for `ReportService.buildReport()` that runs without a database. Use a fake repository that returns rows you choose.

### Success criteria

- A JUnit test for `ReportService` exists and passes with `mvn test`
- The test does not set `report.db.url`
- The test checks that the report contains the header and the fake rows
- The test never touches `DataSourceProvider`

### Hints

<details>
<summary>Hint 1</summary>

The subject of this test is `ReportService`. The repository is just a source of rows, and you decide which rows.

</details>

<details>
<summary>Hint 2</summary>

A fake has to be something that `ReportService` accepts as its repository. Check what type your constructor asks for.

</details>

<details>
<summary>Hint 3</summary>

You have two options. You can make a subclass of `ReportRepository`, or you can extract an interface and let the fake implement it. Try the subclass first and see what its constructor requires. Then compare it with the interface route.

</details>

<details>
<summary>Hint 4</summary>

If you extract an interface, give the real class a new name, for example `JdbcReportRepository`:

```java
public interface ReportRepository {
    List<String> findRows();
}

class FakeReportRepository implements ReportRepository {
    public List<String> findRows() {
        return List.of("Test;1");
    }
}
```

In the test, build the service with `new ReportService(new FakeReportRepository(), new Formatter())`.

</details>

---

## Part 3: Add a dependency and read the graph

### What you will do

Add an `AuditLog` class that prints a line to the console. `ReportRepository` should use it to log that it is fetching rows. Keep the project compiling and the tests green, and note what you had to touch.

### Success criteria

- `AuditLog` exists and `ReportRepository` receives it through its constructor
- The project compiles and all tests pass
- `Main` contains a comment with the creation order of all objects
- The same comment lists every file you had to change after adding `AuditLog`

### Hints

<details>
<summary>Hint 1</summary>

Let the compiler tell you what is broken. Every red line is a place that created a `ReportRepository` or relied on its old constructor.

</details>

<details>
<summary>Hint 2</summary>

Objects without dependencies of their own come first. Each object can only be created after everything it needs exists.

</details>

<details>
<summary>Hint 3</summary>

Draw the dependencies as arrows between classes. The creation order is a walk through that drawing, from the leaves towards `ReportService`.

</details>

<details>
<summary>Hint 4</summary>

Your graph should now have this shape:

```
ReportService -> ReportRepository -> DataSourceProvider
                                  -> AuditLog
              -> Formatter
```

Write the order in `Main` from the bottom up, and count how many places had to change.

</details>

---

# Bonus Challenge (Optional)

Add a field to `A` of type `B`, and a field to `B` of type `A`, both through constructor injection. Try to wire them in `Main`.

Then redesign the two classes so that the cycle disappears, for example by extracting a third class that both depend on. Write down what responsibility you moved into that third class.

---

# Reflection Questions

### Implementation & Trade-offs

1. You made the repository replaceable in Part 2, either through a subclass or an interface. Which route did you pick, and what did the other route cost you?
2. `ReportService` now needs two constructor parameters. At what number of parameters would you start to doubt the design of the class itself?
3. Part 1 placed all `new` calls in `Main`. What are the downsides of having one place that knows how everything is built?

### Production Readiness

4. In a real application with 50 services, who maintains the creation order in `Main`? What happens to that file when two developers add a dependency on the same day?
5. `DataSourceProvider` throws when no database is configured. Where in your wired application does this error show up now, at startup or at the first report? What would you prefer, and why?

### Debugging & Problem Solving

6. When you added `AuditLog`, which compiler errors helped you most, and which were noise? How did you find the right creation order?
7. If your wiring in `Main` has a mistake, such as passing the wrong implementation, how would you find out? Which kind of test would catch it?

### Adaptation / Transfer

8. Imagine the constructor of `DataSourceProvider` needs a configuration object that itself needs a logger. Describe how the manual wiring changes, and which part of it you would want a tool to do for you.
9. You tried to make `A` and `B` depend on each other in the bonus. Describe two other situations in a real code base where a cycle like this could sneak in, and how you would spot it.
