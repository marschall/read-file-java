package com.github.marschall.readfilejava;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.util.function.Consumer;

import org.eclipse.collections.api.bag.MutableBag;
import org.eclipse.collections.api.tuple.primitive.ObjectIntPair;
import org.eclipse.collections.impl.bag.mutable.HashBag;

import com.github.marschall.charsequences.CharSequences;
import com.github.marschall.minicsv.CellSet;
import com.github.marschall.minicsv.CsvParser;
import com.github.marschall.minicsv.Row;

public class ReadFile {

  public static void main(String[] args) throws IOException {
    if (args.length != 1) {
      System.err.println("usage: file");
      System.exit(-1);
    }

    var csvParser = new CsvParser('|');
    var path = Paths.get(args[0]);
    var parseContext = new ParseContext();

    csvParser.parse(path, US_ASCII, parseContext);

    System.out.println("Total file line count: " + parseContext.getLineCount());

    MutableBag<YearMonth> donations = parseContext.getDonations();
    donations.forEachWithOccurrences((donation, occurrence) -> {
      System.out.println("Donations per month and year: "
          + donation + " and donation count: " + occurrence);
    });

    ObjectIntPair<String> mostCommonName = parseContext.getMostCommonName();
    System.out.println("The most common first name is: " + mostCommonName.getOne()
      + " and it occurs: " + mostCommonName.getTwo() + " times.");
  }

  static final class ParseContext implements Consumer<Row> {

    private long lineCount;

    private MutableBag<YearMonth> donations;

    private MutableBag<String> firstNames;

    ParseContext() {
      this.lineCount = 0L;
      this.donations = new HashBag<>();
      this.firstNames = new HashBag<>();
    }

    MutableBag<String> getFirstNames() {
      return this.firstNames.asUnmodifiable();
    }

    MutableBag<YearMonth> getDonations() {
      return this.donations.asUnmodifiable();
    }

    private void incrementLineCount() {
      this.lineCount++;
    }

    private void addDonation(YearMonth donationMonth) {
      this.donations.add(donationMonth);
    }

    private void addFristName(String firstName) {
      this.firstNames.add(firstName);
    }

    private long getLineNumber() {
      return this.lineCount;
    }

    long getLineCount() {
      return this.lineCount;
    }

    ObjectIntPair<String> getMostCommonName() {
      return this.firstNames.topOccurrences(1).getFirst();
    }


    @Override
    public void accept(Row row) {
      this.incrementLineCount();

      CellSet cellSet = row.getCellSet();
      while (cellSet.next()) {
        int columnIndex = cellSet.getColumnIndex();
        if (columnIndex == 4) {
          readDonationMonth(cellSet.getCharSequence());
        } else if (columnIndex == 7) {
          readName(cellSet.getCharSequence());
        } else if (columnIndex > 7) {
          break;
        }
      }
    }

    private void readName(CharSequence name) {
      long lineNumber = this.getLineNumber(); // 1 based
      if (lineNumber == 432L || lineNumber == 43243L) {
        System.out.println("Name: " + name + " at index: " + lineNumber);
      }

      String firstName = extractFirstName(name);
      if (firstName != null) {
        this.addFristName(firstName);
      }
    }

    private void readDonationMonth(CharSequence cellValue) {
      YearMonth dondationMonth = parseDonation(cellValue);
      this.addDonation(dondationMonth);
    }

    static YearMonth parseDonation(CharSequence donation) {
      // use this because of DateTimeFormatterBuilder because it allocates a lot less
      int year = Integer.parseInt(donation, 0, 4, 10);
      int month = Integer.parseInt(donation, 4, 6, 10);
      return YearMonth.of(year, month);
    }

    static String extractFirstName(CharSequence name) {
      // "PEREZ, JOHN A" -> "JOHN"
      int semiColon = CharSequences.indexOf(name, ',');
      if (semiColon == -1) {
        return null;
      }
      int start = semiColon + 1;
      while (start < name.length() && name.charAt(start) == ' ') {
        start += 1;
      }
      if (start == name.length() - 1) {
        return null;
      }
      int end = CharSequences.indexOf(name, ' ', start);
      if (end != -1) {
        return name.subSequence(start, end).toString();
      } else {
        return name.subSequence(start, name.length()).toString();
      }
    }

  }

}
