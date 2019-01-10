Java Large File / Data Reading & Performance Testing
====================================================

A reimplementation of [paigen11/read-file-java](https://github.com/paigen11/read-file-java) using [marschall/line-parser](https://github.com/marschall/line-parser), [marschall/charsequences](https://github.com/marschall/charsequences) and [marschall/mini-csv](https://github.com/marschall/mini-csv).

We use the following approach for parsing

* Use [marschall/mini-csv](https://github.com/marschall/mini-csv) for CSV parsing, which uses [marschall/line-parser](https://github.com/marschall/line-parser).
  * This allows us to drastically cut down on string allocations as just a reused CharSequence view is created for every line instead of a full String.
  * Since the file is in ASCI we can safe us the decoding and turn every byte into a char.
* Use an [Eclipse Collections](https://www.eclipse.org/collections/) [Bag](https://github.com/eclipse/eclipse-collections/blob/master/docs/guide.md#-bag) or counting it occurrences of months and first names.
  * This allows us to not to have to hold on to every first name and is more efficient than a `HashMap<String, Integer>`.
  * Unfortunately this adds about 10 MB.
* Use YearMonth instead of a formatted String for representing a month.
  * Use Integer.parseInt for parsing the YearMonth instead of DateTimeFormatterBuilder because is drastically cuts down on allocations. This causes a noticeable speed improvement.

```
time java -Xmx16m -cp target/read-file-java-0.1.0-SNAPSHOT-shaded.jar com.github.marschall.readfilejava.ReadFile /path/to/file
``

```
time java -XX:+UnlockExperimentalVMOptions -XX:+EnableJVMCI -XX:+UseJVMCICompiler
``