/* Copyright (c) 2020 LibJ
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * You should have received a copy of The MIT License (MIT) along with this
 * program. If not, see <http://opensource.org/licenses/MIT/>.
 */

package gnu.java.math;

import java.math.BigInteger;

import org.junit.Test;
import org.libj.math.BigIntegers;

public class BigIntConstructorTest extends BigIntTest {
  @Test
  public void testUnsignedInt() {
    testRange("<init>(int,int)",
      i("BigInteger", a -> a % 2 == 0 ? -1 : 1, (int a, int b) -> BigIntegers.valueOf(a, b), String::valueOf),
      i("BigInt", a -> a % 2 == 0 ? -1 : 1, (int a, int b) -> new BigInt(a, b), String::valueOf),
      i("int[]", a -> a % 2 == 0 ? -1 : 1, (int a, int b) -> BigInt.valueOf(a, b), BigInt::toString)
    );
  }

  @Test
  public void testSignedInt() {
    testRange("<init>(int)",
      i("BigInteger", (int a, int b) -> BigInteger.valueOf(a), String::valueOf),
      i("BigInt", (int a, int b) -> new BigInt(a), String::valueOf),
      i("int[]", (int a, int b) -> BigInt.valueOf(a), BigInt::toString)
    );
  }

  @Test
  public void testUnsignedLong() {
    testRange("<init>(int,long)",
      l("BigInteger", a -> a % 2 == 0 ? -1 : 1, (long a, long b) -> BigIntegers.valueOf((int)a, b), String::valueOf),
      l("BigInt", a -> a % 2 == 0 ? -1 : 1, (long a, long b) -> new BigInt((int)a, b), String::valueOf),
      l("int[]", a -> a % 2 == 0 ? -1 : 1, (long a, long b) -> BigInt.valueOf((int)a, b), BigInt::toString)
    );
  }

  @Test
  public void testSignedLong() {
    testRange("<init>(long)",
      l("BigInteger", (long a, long b) -> BigInteger.valueOf(a), String::valueOf),
      l("BigInt", (long a, long b) -> new BigInt(a), String::valueOf),
      l("int[]", (long a, long b) -> BigInt.valueOf(a), BigInt::toString)
    );
  }

  @Test
  public void testBig() {
    testRange("<init>(String)",
      s("BigInteger", (String a, String b) -> new BigInteger(a), String::valueOf),
      s("BigInt", (String a, String b) -> new BigInt(a), String::valueOf),
      s("int[]", (String a, String b) -> BigInt.valueOf(a), BigInt::toString)
    );
  }
}