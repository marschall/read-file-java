package com.github.marschall.readfilejava;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.github.marschall.readfilejava.ReadFile.ParseContext;

class ReadFileTest {

  @Test
  void extractFirstName() {
    assertEquals(ParseContext.extractFirstName("PEREZ, JOHN A"), "JOHN");
    assertEquals(ParseContext.extractFirstName("PEREZ, JOHN"), "JOHN");
  }

}
