# Csanszi – Budget

Egy költségvetés-kezelő rendszer, ami hasonlít az [Actual Budget](https://actualbudget.org/) működéséhez, de számos olyan funkcióval egészül ki, ami nekem hiányzott más rendszerekből.

## Alapfunkciók (mint az Actual-ban)

- Manuális adatfeltöltés (pl. Excelből)
- Bármilyen típusú tétel kezelése: debit, credit, asset
- Könnyű kategorizálás
- Nem kategorizált sorok külön táblázatban jelennek meg a kategorizáló alatt
- Dupla kattintással szabály létrehozása bármely mezőre:
  - duplán kattintva a dátumra → `transaction_date = 2025.MM.dd`
  - duplán kattintva a közleményre → `note = 'Spar Magyarország'`
- Tranzakciók feldarabolása (split) – még nincs kész

## Amit pluszban tud, és nekem kellett

- Automatikusan felismeri, ha két saját számla között történik utalás, és azt nem számolja sem bevételnek, sem kiadásnak (esetleg tranzakciós díjként, de nálam az sincs)
- Ezeket automatikusan egy csoportba teszi
- Tranzakciók nem csak darabolhatók, hanem csoportosíthatók is:
  - például ha veszek 10 múzeumjegyet, de csak 3 a miénk, a többiek visszafizetik: nem jelenik meg 10x kiadás és 7x bevételként, hanem egy csoportban a végső egyenleg -3 tételként jelenik meg
- Magyar Államkincstár integráció normálisan működik
- Egészségpénztári utalásoknál díj generálás is kezelve van
- Az adatbázis helyben fut PostgreSQL-ben, így Power BI-ból vagy más adatvizualizációs eszközből könnyen betölthető

## Excel feltöltő

- Egyszerű Excel alapú feltöltő, ahol beállítható, hogy melyik oszlop melyik budget mezőnek felel meg
- Csak inkrementális feltöltést támogat érdemben
- Ha két fájl tartalmaz közös időszakot (pl. utolsó 3 nap), akkor mindkettőt feltölti
- A felületen lehet megoldani az esetleges duplikációk levágását

## Kategóriahierarchia

A rendszer hierarchikus kategóriákat használ, például:

Közlekedés


├── Autó

│   ├── Üzemanyag

│   └── Parkolás

└── Tömegközlekedés


    ├── BKV
    
    
    └── Vonat
    
    

Élelmiszer

├── Tesco

└── SPAR


## Eredmények felhasználása

Az adatokat betöltöm Power BI-ba, ahol kényelmesen tudok belőlük diagramokat és statisztikákat készíteni.


# Csani-Budget

This project can be used as a starting point to create your own Vaadin application with Spring Boot.
It contains all the necessary configuration and some placeholder files to get you started.

## Running the application

Open the project in an IDE. You can download the [IntelliJ community edition](https://www.jetbrains.com/idea/download) if you do not have a suitable IDE already.
Once opened in the IDE, locate the `Application` class and run the main method using "Debug".

For more information on installing in various IDEs, see [how to import Vaadin projects to different IDEs](https://vaadin.com/docs/latest/getting-started/import).

If you install the Vaadin plugin for IntelliJ, you should instead launch the `Application` class using "Debug using HotswapAgent" to see updates in the Java code immediately reflected in the browser.

## Deploying to Production

The project is a standard Maven project. To create a production build, call 

```
./mvnw clean package -Pproduction
```

If you have Maven globally installed, you can replace `./mvnw` with `mvn`.

This will build a JAR file with all the dependencies and front-end resources,ready to be run. The file can be found in the `target` folder after the build completes.
You then launch the application using 
```
java -jar target/csani-budget-1.0-SNAPSHOT.jar
```

## Project structure

- `MainLayout.java` in `src/main/java` contains the navigation setup (i.e., the
  side/top bar and the main menu). This setup uses
  [App Layout](https://vaadin.com/docs/components/app-layout).
- `views` package in `src/main/java` contains the server-side Java views of your application.
- `views` folder in `src/main/frontend` contains the client-side JavaScript views of your application.
- `themes` folder in `src/main/frontend` contains the custom CSS styles.

## Useful links

- Read the documentation at [vaadin.com/docs](https://vaadin.com/docs).
- Follow the tutorial at [vaadin.com/docs/latest/tutorial/overview](https://vaadin.com/docs/latest/tutorial/overview).
- Create new projects at [start.vaadin.com](https://start.vaadin.com/).
- Search UI components and their usage examples at [vaadin.com/docs/latest/components](https://vaadin.com/docs/latest/components).
- View use case applications that demonstrate Vaadin capabilities at [vaadin.com/examples-and-demos](https://vaadin.com/examples-and-demos).
- Build any UI without custom CSS by discovering Vaadin's set of [CSS utility classes](https://vaadin.com/docs/styling/lumo/utility-classes). 
- Find a collection of solutions to common use cases at [cookbook.vaadin.com](https://cookbook.vaadin.com/).
- Find add-ons at [vaadin.com/directory](https://vaadin.com/directory).
- Ask questions on [Stack Overflow](https://stackoverflow.com/questions/tagged/vaadin) or join our [Forum](https://vaadin.com/forum).
- Report issues, create pull requests in [GitHub](https://github.com/vaadin).
# csani-budget
<img width="1303" height="780" alt="struct" src="https://github.com/user-attachments/assets/6a173d22-ed06-442c-b112-54425d556095" />

<img width="1889" height="948" alt="accounts" src="https://github.com/user-attachments/assets/5683910e-af77-4602-878b-779bb2565d46" />
<img width="1855" height="948" alt="rule" src="https://github.com/user-attachments/assets/4a4d6bb9-7ad1-4a35-8f75-ab39842701c2" />
<img width="1845" height="948" alt="upload" src="https://github.com/user-attachments/assets/337cae00-b0f6-4af1-9b1f-f9083d51526f" />

<img width="1485" height="948" alt="stackedchart" src="https://github.com/user-attachments/assets/052a789f-448a-4531-a747-916caac1b45f" />
<img width="1920" height="1040" alt="runingsum_vs_sum" src="https://github.com/user-attachments/assets/ce80b4b8-7f50-488a-8679-f98929b65140" />
<img width="1920" height="1040" alt="income" src="https://github.com/user-attachments/assets/5ab9f651-f25f-4218-b764-ffa4391daf98" />



