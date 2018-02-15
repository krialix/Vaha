package com.vaha.server;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DemoTest {

  @Test
  public void testListToString() throws Exception {
    System.out.println(calculateRating(3, 5));
  }

  private float calculateRating(int totalRater, int rating) {
    return ((rating * totalRater) + rating) / ++totalRater;
  }
}
