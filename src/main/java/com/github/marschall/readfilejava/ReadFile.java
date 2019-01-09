package com.github.marschall.readfilejava;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
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
    var csvParser = new CsvParser('|');;
    var path = Paths.get(args[0]);
    var parseContext = new ParseContext();
    
    csvParser.parse(path, US_ASCII, parseContext);
    
    MutableBag<String> firstNames = parseContext.getFirstNames();
    ObjectIntPair<String> topOccurance = firstNames.topOccurrences(1).getFirst();
    System.out.println("The most common first name is: " + topOccurance.getOne()
      + " and it occurs: " + topOccurance.getTwo() + " times.");
    MutableBag<YearMonth> donations = parseContext.getDonations();
    donations.forEachWithOccurrences((donation, occurrence) -> {
      System.out.println("Donations per month and year: "
          + donation + " and donation count: " + occurrence);
    });

  }
  
  static final class ParseContext implements Consumer<Row> {

    private static final DateTimeFormatter DONATION_PARSER = new DateTimeFormatterBuilder()
        .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
        .appendValue(MONTH_OF_YEAR, 2)
        .toFormatter();
    
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
    
    long getLineCount() {
      return this.lineCount;
    }
    

    @Override
    public void accept(Row row) {
      this.incrementLineCount();
      CellSet cellSet = row.getCellSet();
      while (cellSet.next()) {
        int columnIndex = cellSet.getColumnIndex();
        if (columnIndex == 4) {
          YearMonth dondationMonth = YearMonth.parse(cellSet.getCharSequence().subSequence(0, 6), DONATION_PARSER); // a custom parser would be faster
          this.addDonation(dondationMonth);
        } else if (columnIndex == 7) {
          String firstName = extractFirstName(cellSet.getCharSequence());
          if (firstName != null) {
            this.addFristName(firstName);
          }
        } else if (columnIndex > 7) {
          break;
        }
      }
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
