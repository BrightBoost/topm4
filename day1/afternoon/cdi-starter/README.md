# Mini-CDI Starter Project

Dit is het startproject voor het middagproject van dag 1 (blok 4, dag 15).

## Draaien

```bash
mvn test
```

Alle tests falen bij de start. Dat klopt: de `Container` is nog leeg.
Het doel is dat ze aan het eind van de middag groen zijn.

## Wat staat er al

- `Container`: de publieke methodes staan vast, de implementatie is aan jou
- `ContainerException`: basisklasse voor fouten, je mag er subklassen van maken
- Voorbeeldklassen in `nl.topicus.cdi.example`, zonder annotaties
- Tests voor Part 1, 3, 4 en 5

## Wat doe je zelf

- Part 2 heeft geen tests: schrijf ze zelf (annotaties maken, toepassen en met reflection uitlezen)
- Zet je annotaties in een eigen package, bijvoorbeeld `nl.topicus.cdi.annotation`
- Voeg de annotaties toe aan de voorbeeldklassen (zoek op `TODO Part 2`)

## Projectstructuur

```
src/main/java/nl/topicus/cdi/
├── Container.java               <- hier bouw je
├── ContainerException.java
└── example/
    ├── order/                   <- scenario: alles gaat goed
    │   ├── OrderService         (PaymentService + InvoiceService)
    │   ├── PaymentService       (PaymentRepository)
    │   ├── InvoiceService       (PaymentRepository)
    │   └── PaymentRepository    (geen dependencies)
    ├── missing/                 <- scenario: dingen die misgaan bij het aanmaken
    │   ├── ReportService        (AuditLog, wordt niet geregistreerd)
    │   ├── AuditLog
    │   └── BrokenService        (constructor gooit een exception)
    └── cycle/                   <- scenario: circulaire dependencies
        ├── CycleA <-> CycleB
        └── ChainA -> ChainB -> ChainC -> ChainA

src/test/java/nl/topicus/cdi/
├── RegistryTest                 (Part 1)
├── ConstructorInjectionTest     (Part 3)
├── MissingDependencyTest        (Part 4)
└── CircularDependencyTest       (Part 5)
```
