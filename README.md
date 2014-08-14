##Management of museum visits

###Programming and Data Analysis

Author: Valeriya Slovikovskaya



Project is divided into four packages: *storage*, *console*, *gui*, *generator*.

The core part is the storage. (*Storage*.java)

It keeps visits sorted by date and indexed by name. Java TreeMap is used to create storage instances. TreeMap implements Red-Black balanced tree and, therefore assures good scalability and minimizes lookup time.

*Visits*.java class represents visit entity.

*ImportFromCsv*.java allows to import data from .csv file. This functionality is useful for tests purposes if used together with tests data generator GeneratorCsv.java
.

*GeneratorCsv*.java
 generates tests visits, randomly taking names from nomi.txt file and dates from two years interval, and write them into .csv file.

*Client*.java is console client for storage access. (It contains main method.)

And finally, *BookAndLook*.java, *BookPanel*.java and *LookPanel*.java
 implement GUI: tabbed frame with two panels: Book and Look. Book panel serves to book visits. Look panel is for searching and cancellation. (BookAndLook contains main method).

List of files

- src/
 - console/
 - Client.java
- generator/ 
 - GeneratorCsv.java
- gui/
 - BookAndLook.java
 - BookPanel.java
 - LookPanel.java
- storage/
 - ImportFromCsv.java
 - StorageException.java
 - Storage.java
 - Visit.java
- data.csv 
- nomi.txt

