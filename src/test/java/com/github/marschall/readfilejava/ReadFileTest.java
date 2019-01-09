package com.github.marschall.readfilejava;

import static org.junit.jupiter.api.Assertions.*;

import java.time.YearMonth;

import org.junit.jupiter.api.Test;

import com.github.marschall.readfilejava.ReadFile.ParseContext;

class ReadFileTest {

  @Test
  void extractFirstName() {
    assertNull(ParseContext.extractFirstName("PEREZ"));
    assertEquals(ParseContext.extractFirstName("PEREZ, JOHN A"), "JOHN");
    assertEquals(ParseContext.extractFirstName("PEREZ, JOHN"), "JOHN");
  }
  
  @Test
  void parseDonation() {
    assertEquals(ParseContext.parseDonation("201701230300133512"), YearMonth.of(2017, 1));
    assertEquals(ParseContext.parseDonation("201702039042410894"), YearMonth.of(2017, 2));
  }

}
