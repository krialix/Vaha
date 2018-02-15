package com.vaha.server.listener;

public class TestClass {

  int a = 5;

  public TestClass() {
    if (a == 5) {
      throw new RuntimeException("Yooo");
    }
  }
}
