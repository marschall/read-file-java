Java Large File / Data Reading & Performance Testing
====================================================

A reimplementation of [paigen11/read-file-java](https://github.com/paigen11/read-file-java) using [marschall/line-parser](https://github.com/marschall/line-parser), [marschall/charsequences](https://github.com/marschall/charsequences) and [marschall/mini-csv](https://github.com/marschall/mini-csv).

We use the following approach for parsing

* Use [marschall/mini-csv](https://github.com/marschall/mini-csv) for CSV parsing, which uses [marschall/line-parser](https://github.com/marschall/line-parser).
  * This allows us to drastically cut down on string allocations.
* Use an Eclipse Collections Bag or counting it occurrences of months and first names.
* Use YearMonth instead of a formatted String for representing a month.
  * Use Integer.parseInt for parsing the YearMonth instead of DateTimeFormatterBuilder because is drastically cuts down on allocations
