/* Copyright (c) 2008 Seva Safris, LibJ
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

package org.libj.math;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;
import org.libj.lang.BigDecimals;
import org.libj.test.TestAide;

import ch.obermuhlner.math.big.BigDecimalMath;

public class SafeMathITest {
  private static final int numTests = 10000;
  private static final Random random = new Random();
  private static final MathContext mc = MathContext.DECIMAL64;

  private static double d10() {
    return (Math.random() < 0.5 ? -1 : 1) * (random.nextDouble() + 1);
  }

  private static double d0() {
    return (Math.random() < 0.5 ? -1 : 1) * random.nextDouble();
  }

  @SuppressWarnings("unchecked")
  private static <N extends Number>N cast(final boolean upscale, final double d, final Class<N> cls) {
    if (float.class == cls)
      return (N)Float.valueOf((float)d);

    if (double.class == cls)
      return (N)Double.valueOf(d);

    if (byte.class == cls)
      return (N)Byte.valueOf((byte)(0 < d && d < 1 ? 0 : upscale ? d * 100 : d));

    if (short.class == cls)
      return (N)Short.valueOf((short)(0 < d && d < 1 ? 0 : upscale ? d * 10000 : d));

    if (int.class == cls)
      return (N)Integer.valueOf((int)(0 < d && d < 1 ? 0 : upscale ? d * 100000000 : d));

    if (long.class == cls)
      return (N)Long.valueOf((long)(0 < d && d < 1 ? 0 : upscale ? d * 100000000000000000L : d));

    if (BigInteger.class.isAssignableFrom(cls))
      return (N)new BigInteger(String.valueOf(0 < d && d < 1 ? 0 : (long)(upscale ? d * 100000000000000000L : d)));

    if (BigDecimal.class.isAssignableFrom(cls))
      return (N)new BigDecimal(String.valueOf(0 < d && d < 1 ? d : upscale ? d * 100000000000000000L : d));

    throw new UnsupportedOperationException("Unsupported type: " + cls.getName());
  }

  private static <N extends Number>void test(final double value, final Class<N> cls, final Function<N,N> test, final Function<N,N> control) {
    test(value, cls, cls, test, control);
  }

  private static <I extends Number,O extends Number>void test(final double value, final Class<I> in, final Class<O> out, final Function<I,O> test, final Function<I,O> control) {
    test0(true, value, in, out, test, control);
  }

  private static <I extends Number,O extends Number>void test1(final double value, final Class<I> in, final Class<O> out, final Function<I,O> test, final Function<I,O> control) {
    test0(false, value, in, out, test, control);
  }

  @SuppressWarnings("unused")
  private static <I extends Number,O extends Number>void test0(final boolean upscale, final double value, final Class<I> in, final Class<O> out, final Function<I,O> test, final Function<I,O> control) {
    final I n = cast(upscale, value, in);
    final O expected = control.apply(n);
    final O actual = test.apply(n);
    if (expected instanceof Double)
      assertEquals(((Double)expected).doubleValue(), ((Double)actual).doubleValue(), 0.00000000000001d);
    else if (expected instanceof BigDecimal)
      assertEquals(expected + " != " + actual, 0, ((BigDecimal)expected).compareTo((BigDecimal)actual));
    else
      assertEquals(expected, actual);
  }

  private static <I1 extends Number,I2 extends Number,O extends Number>void test(final double v1, final double v2, final Class<I1> in1, final Class<I2> in2, final Class<O> out, final BiFunction<I1,I2,O> test, final BiFunction<I1,I2,O> control) {
    test0(true, v1, v2, in1, in2, out, test, control);
  }

  private static <I1 extends Number,I2 extends Number,O extends Number>void test1(final double v1, final double v2, final Class<I1> in1, final Class<I2> in2, final Class<O> out, final BiFunction<I1,I2,O> test, final BiFunction<I1,I2,O> control) {
    test0(false, v1, v2, in1, in2, out, test, control);
  }

  @SuppressWarnings("unused")
  private static <I1 extends Number,I2 extends Number,O extends Number>void test0(final boolean upscale, final double v1, final double v2, final Class<I1> in1, final Class<I2> in2, final Class<O> out, final BiFunction<I1,I2,O> test, final BiFunction<I1,I2,O> control) {
    final I1 n1 = cast(upscale, v1, in1);
    final I2 n2 = cast(upscale, v2, in2);
    final O expected = control.apply(n1, n2);
    final O actual = test.apply(n1, n2);
    if (expected instanceof Double)
      assertEquals(((Double)expected).doubleValue(), ((Double)actual).doubleValue(), 0.0000000001d);
    else
      assertEquals(expected, actual);
  }

  @Test
  public void testAbsByte() {
    test(0, short.class, SafeMath::abs, n -> (short)Math.abs(n));
    test(1, short.class, SafeMath::abs, n -> (short)Math.abs(n));
    test(-1, short.class, SafeMath::abs, n -> (short)Math.abs(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, short.class, SafeMath::abs, n -> (short)Math.abs(n));
  }

  @Test
  public void testAbsShort() {
    test(0, short.class, SafeMath::abs, n -> (short)Math.abs(n));
    test(1, short.class, SafeMath::abs, n -> (short)Math.abs(n));
    test(-1, short.class, SafeMath::abs, n -> (short)Math.abs(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, short.class, SafeMath::abs, n -> (short)Math.abs(n));
  }

  @Test
  public void testAbsInt() {
    test(0, int.class, SafeMath::abs, n -> Math.abs(n));
    test(1, int.class, SafeMath::abs, n -> Math.abs(n));
    test(-1, int.class, SafeMath::abs, n -> Math.abs(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, int.class, SafeMath::abs, n -> Math.abs(n));
  }

  @Test
  public void testAbsLong() {
    test(0, long.class, SafeMath::abs, n -> Math.abs(n));
    test(1, long.class, SafeMath::abs, n -> Math.abs(n));
    test(-1, long.class, SafeMath::abs, n -> Math.abs(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, long.class, SafeMath::abs, n -> Math.abs(n));
  }

  @Test
  public void testAbsFloat() {
    test(0, float.class, SafeMath::abs, n -> Math.abs(n));
    test(1, float.class, SafeMath::abs, n -> Math.abs(n));
    test(-1, float.class, SafeMath::abs, n -> Math.abs(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, float.class, SafeMath::abs, n -> Math.abs(n));
  }

  @Test
  public void testAbsDouble() {
    test(0, double.class, SafeMath::abs, n -> Math.abs(n));
    test(1, double.class, SafeMath::abs, n -> Math.abs(n));
    test(-1, double.class, SafeMath::abs, n -> Math.abs(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, double.class, SafeMath::abs, n -> Math.abs(n));
  }

  @Test
  public void testAbsBigInteger() {
    test(0, BigInteger.class, SafeMath::abs, n -> n.abs());
    test(1, BigInteger.class, SafeMath::abs, n -> n.abs());
    test(-1, BigInteger.class, SafeMath::abs, n -> n.abs());
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigInteger.class, SafeMath::abs, n -> n.abs());
  }

  @Test
  public void testAbsBigDecimal() {
    test(0, BigDecimal.class, SafeMath::abs, n -> n.abs());
    test(1, BigDecimal.class, SafeMath::abs, n -> n.abs());
    test(-1, BigDecimal.class, SafeMath::abs, n -> n.abs());
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigDecimal.class, SafeMath::abs, n -> n.abs());
  }

  @Test
  public void testAcosDouble() {
    test(0, double.class, SafeMath::acos, n -> Math.acos(n));
    test(1, double.class, SafeMath::acos, n -> Math.acos(n));
    test(-1, double.class, SafeMath::acos, n -> Math.acos(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::acos, n -> Math.acos(n));
  }

  @Test
  public void testAcosBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.acos(n, mc), n -> BigDecimalMath.acos(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.acos(n, mc), n -> BigDecimalMath.acos(new BigDecimal(n), mc));
  }

  @Test
  public void testAcosBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.acos(n, mc), n -> BigDecimalMath.acos(n, mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.acos(n, mc), n -> BigDecimalMath.acos(n, mc));
  }

  @Test
  public void testAsinDouble() {
    test(0, double.class, SafeMath::asin, n -> Math.asin(n));
    test(1, double.class, SafeMath::asin, n -> Math.asin(n));
    test(-1, double.class, SafeMath::asin, n -> Math.asin(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::asin, n -> Math.asin(n));
  }

  @Test
  public void testAsinBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.asin(n, mc), n -> BigDecimalMath.asin(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.asin(n, mc), n -> BigDecimalMath.asin(new BigDecimal(n), mc));
  }

  @Test
  public void testAsinBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.asin(n, mc), n -> BigDecimalMath.asin(n, mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.asin(n, mc), n -> BigDecimalMath.asin(n, mc));
  }

  @Test
  public void testAtanDouble() {
    test(0, double.class, SafeMath::atan, n -> Math.atan(n));
    test(1, double.class, SafeMath::atan, n -> Math.atan(n));
    test(-1, double.class, SafeMath::atan, n -> Math.atan(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::atan, n -> Math.atan(n));
  }

  @Test
  public void testAtanBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.atan(n, mc), n -> BigDecimalMath.atan(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.atan(n, mc), n -> BigDecimalMath.atan(new BigDecimal(n), mc));
  }

  @Test
  public void testAtanBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.atan(n, mc), n -> BigDecimalMath.atan(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.atan(n, mc), n -> BigDecimalMath.atan(n, mc));
  }

  @Test
  public void testAtan2Double() {
    test(0, 0, double.class, double.class, double.class, (n1,n2) -> SafeMath.atan2(n1, n2), (n1,n2) -> Math.atan2(n1, n2));
    test(1, 1, double.class, double.class, double.class, (n1,n2) -> SafeMath.atan2(n1, n2), (n1,n2) -> Math.atan2(n1, n2));
    test(-1, -1, double.class, double.class, double.class, (n1,n2) -> SafeMath.atan2(n1, n2), (n1,n2) -> Math.atan2(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), double.class, double.class, double.class, (n1,n2) -> SafeMath.atan2(n1, n2), (n1,n2) -> Math.atan2(n1, n2));
  }

  @Test
  public void testAtan2BigDecimal() {
    test(1, 1, BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.atan2(n1, n2, mc), (n1,n2) -> BigDecimalMath.atan2(n1, n2, mc));
    test(-1, -1, BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.atan2(n1, n2, mc), (n1,n2) -> BigDecimalMath.atan2(n1, n2, mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.atan2(n1, n2, mc), (n1,n2) -> BigDecimalMath.atan2(n1, n2, mc));
  }

  @Test
  public void testCeilByte() {
    test(0, byte.class, SafeMath::ceil, n -> (byte)Math.ceil(n));
    test(1, byte.class, SafeMath::ceil, n -> (byte)Math.ceil(n));
    test(-1, byte.class, SafeMath::ceil, n -> (byte)Math.ceil(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, byte.class, SafeMath::ceil, n -> (byte)Math.ceil(n));
  }

  @Test
  public void testCeilShort() {
    test(0, short.class, SafeMath::ceil, n -> (short)Math.ceil(n));
    test(1, short.class, SafeMath::ceil, n -> (short)Math.ceil(n));
    test(-1, short.class, SafeMath::ceil, n -> (short)Math.ceil(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, short.class, SafeMath::ceil, n -> (short)Math.ceil(n));
  }

  @Test
  public void testCeilInt() {
    test(0, int.class, SafeMath::ceil, n -> (int)Math.ceil(n));
    test(1, int.class, SafeMath::ceil, n -> (int)Math.ceil(n));
    test(-1, int.class, SafeMath::ceil, n -> (int)Math.ceil(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, int.class, SafeMath::ceil, n -> (int)Math.ceil(n));
  }

  @Test
  public void testCeilLong() {
    test(0, long.class, SafeMath::ceil, n -> (long)Math.ceil(n));
    test(1, long.class, SafeMath::ceil, n -> (long)Math.ceil(n));
    test(-1, long.class, SafeMath::ceil, n -> (long)Math.ceil(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, long.class, SafeMath::ceil, n -> (long)Math.ceil(n));
  }

  @Test
  public void testCeilFloat() {
    test(0, float.class, SafeMath::ceil, n -> (float)Math.ceil(n));
    test(1, float.class, SafeMath::ceil, n -> (float)Math.ceil(n));
    test(-1, float.class, SafeMath::ceil, n -> (float)Math.ceil(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, float.class, SafeMath::ceil, n -> (float)Math.ceil(n));
  }

  @Test
  public void testCeilDouble() {
    test(0, double.class, SafeMath::ceil, n -> Math.ceil(n));
    test(1, double.class, SafeMath::ceil, n -> Math.ceil(n));
    test(-1, double.class, SafeMath::ceil, n -> Math.ceil(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, double.class, SafeMath::ceil, n -> Math.ceil(n));
  }

  @Test
  public void testCeilBigInteger() {
    test(0, BigInteger.class, SafeMath::ceil, n -> n);
    test(1, BigInteger.class, SafeMath::ceil, n -> n);
    test(-1, BigInteger.class, SafeMath::ceil, n -> n);
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigInteger.class, SafeMath::ceil, n -> n);
  }

  @Test
  public void testCeilBigDecimal() {
    test(0, BigDecimal.class, SafeMath::ceil, n -> n.setScale(0, RoundingMode.CEILING));
    test(1, BigDecimal.class, SafeMath::ceil, n -> n.setScale(0, RoundingMode.CEILING));
    test(-1, BigDecimal.class, SafeMath::ceil, n -> n.setScale(0, RoundingMode.CEILING));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigDecimal.class, SafeMath::ceil, n -> n.setScale(0, RoundingMode.CEILING));
  }

  @Test
  public void testCosLong() {
    test(0, long.class, double.class, SafeMath::cos, n -> Math.cos(n));
    test(1, long.class, double.class, SafeMath::cos, n -> Math.cos(n));
    test(-1, long.class, double.class, SafeMath::cos, n -> Math.cos(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), long.class, double.class, SafeMath::cos, n -> Math.cos(n));
  }

  @Test
  public void testCosDouble() {
    test(0, double.class, SafeMath::cos, n -> Math.cos(n));
    test(1, double.class, SafeMath::cos, n -> Math.cos(n));
    test(-1, double.class, SafeMath::cos, n -> Math.cos(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::cos, n -> Math.cos(n));
  }

  @Test
  public void testCosBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.cos(n, mc), n -> BigDecimalMath.cos(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.cos(n, mc), n -> BigDecimalMath.cos(new BigDecimal(n), mc));
  }

  @Test
  public void testCosBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.cos(n, mc), n -> BigDecimalMath.cos(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.cos(n, mc), n -> BigDecimalMath.cos(n, mc));
  }

  @Test
  public void testExpDouble() {
    test(0, double.class, SafeMath::exp, n -> Math.exp(n));
    test(1, double.class, SafeMath::exp, n -> Math.exp(n));
    test(-1, double.class, SafeMath::exp, n -> Math.exp(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::exp, n -> Math.exp(n));
  }

  @Test
  public void testExpBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.exp(n, mc), n -> BigDecimalMath.exp(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.exp(n, mc), n -> BigDecimalMath.exp(new BigDecimal(n), mc));
  }

  @Test
  public void testExpBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.exp(n, mc), n -> BigDecimalMath.exp(n, mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.exp(n, mc), n -> BigDecimalMath.exp(n, mc));
  }

  @Test
  public void testFloorByte() {
    test(0, byte.class, SafeMath::floor, n -> (byte)Math.floor(n));
    test(1, byte.class, SafeMath::floor, n -> (byte)Math.floor(n));
    test(-1, byte.class, SafeMath::floor, n -> (byte)Math.floor(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, byte.class, SafeMath::floor, n -> (byte)Math.floor(n));
  }

  @Test
  public void testFloorShort() {
    test(0, short.class, SafeMath::floor, n -> (short)Math.floor(n));
    test(1, short.class, SafeMath::floor, n -> (short)Math.floor(n));
    test(-1, short.class, SafeMath::floor, n -> (short)Math.floor(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, short.class, SafeMath::floor, n -> (short)Math.floor(n));
  }

  @Test
  public void testFloorInt() {
    test(0, int.class, SafeMath::floor, n -> (int)Math.floor(n));
    test(1, int.class, SafeMath::floor, n -> (int)Math.floor(n));
    test(-1, int.class, SafeMath::floor, n -> (int)Math.floor(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, int.class, SafeMath::floor, n -> (int)Math.floor(n));
  }

  @Test
  public void testFloorLong() {
    test(0, long.class, SafeMath::floor, n -> (long)Math.floor(n));
    test(1, long.class, SafeMath::floor, n -> (long)Math.floor(n));
    test(-1, long.class, SafeMath::floor, n -> (long)Math.floor(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, long.class, SafeMath::floor, n -> (long)Math.floor(n));
  }

  @Test
  public void testFloorFloat() {
    test(0, float.class, SafeMath::floor, n -> (float)Math.floor(n));
    test(1, float.class, SafeMath::floor, n -> (float)Math.floor(n));
    test(-1, float.class, SafeMath::floor, n -> (float)Math.floor(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, float.class, SafeMath::floor, n -> (float)Math.floor(n));
  }

  @Test
  public void testFloorDouble() {
    test(0, double.class, SafeMath::floor, n -> Math.floor(n));
    test(1, double.class, SafeMath::floor, n -> Math.floor(n));
    test(-1, double.class, SafeMath::floor, n -> Math.floor(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, double.class, SafeMath::floor, n -> Math.floor(n));
  }

  @Test
  public void testFloorBigInteger() {
    test(0, BigInteger.class, SafeMath::floor, n -> n);
    test(1, BigInteger.class, SafeMath::floor, n -> n);
    test(-1, BigInteger.class, SafeMath::floor, n -> n);
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigInteger.class, SafeMath::floor, n -> n);
  }

  @Test
  public void testFloorBigDecimal() {
    test(0, BigDecimal.class, SafeMath::floor, n -> n.setScale(0, RoundingMode.FLOOR));
    test(1, BigDecimal.class, SafeMath::floor, n -> n.setScale(0, RoundingMode.FLOOR));
    test(-1, BigDecimal.class, SafeMath::floor, n -> n.setScale(0, RoundingMode.FLOOR));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigDecimal.class, SafeMath::floor, n -> n.setScale(0, RoundingMode.FLOOR));
  }

  @Test
  public void testLogDouble() {
    test(0, double.class, SafeMath::log, n -> Math.log(n));
    test(1, double.class, SafeMath::log, n -> Math.log(n));
    test(-1, double.class, SafeMath::log, n -> Math.log(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::log, n -> Math.log(n));
  }

  @Test
  public void testLogBigInteger() {
    test(1, BigInteger.class, BigDecimal.class, n -> SafeMath.log(n, mc), n -> BigDecimalMath.log(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), BigInteger.class, BigDecimal.class, n -> SafeMath.log(n, mc), n -> BigDecimalMath.log(new BigDecimal(n), mc));
  }

  @Test
  public void testLogBigDecimal() {
    test(1, BigDecimal.class, BigDecimal.class, n -> SafeMath.log(n, mc), n -> BigDecimalMath.log(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), BigDecimal.class, BigDecimal.class, n -> SafeMath.log(n, mc), n -> BigDecimalMath.log(n, mc));
  }

  @Test
  public void testLogFloatFloat() {
    test(0, 0, float.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, float.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, float.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), float.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogDoubleFloat() {
    test(0, 0, float.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, float.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, float.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), float.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogIntFloat() {
    test(0, 0, float.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, float.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, float.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), float.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogLongFloat() {
    test(0, 0, float.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, float.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, float.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), float.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogFloatDouble() {
    test(0, 0, double.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, double.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, double.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), double.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogDoubleDouble() {
    test(0, 0, double.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, double.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, double.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), double.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogIntDouble() {
    test(0, 0, double.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, double.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, double.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), double.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogLongDouble() {
    test(0, 0, double.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, double.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, double.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), double.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogFloatInt() {
    test(0, 0, int.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, int.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, int.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), int.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogDoubleInt() {
    test(0, 0, int.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, int.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, int.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), int.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogIntInt() {
    test(0, 0, int.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, int.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, int.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), int.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogLongInt() {
    test(0, 0, int.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, int.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, int.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), int.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogFloatLong() {
    test(0, 0, long.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, long.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, long.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), long.class, float.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogDoubleLong() {
    test(0, 0, long.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, long.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, long.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), long.class, double.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogIntLong() {
    test(0, 0, long.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, long.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, long.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), long.class, int.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogLongLong() {
    test(0, 0, long.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(1, 1, long.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    test(-1, -1, long.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), long.class, long.class, double.class, (n1,n2) -> SafeMath.log(n1, n2), (n1,n2) -> Math.log(n2) / Math.log(n1));
  }

  @Test
  public void testLogBigDecimalBigInteger() {
    test(1, 1, BigDecimal.class, BigInteger.class, BigDecimal.class, (n1,n2) -> SafeMath.log(n1, n2, mc), (n1,n2) -> BigDecimalMath.log(new BigDecimal(n2), mc).divide(BigDecimalMath.log(n1, mc), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), Math.abs(d10()), BigDecimal.class, BigInteger.class, BigDecimal.class, (n1,n2) -> SafeMath.log(n1, n2, mc), (n1,n2) -> BigDecimalMath.log(new BigDecimal(n2), mc).divide(BigDecimalMath.log(n1, mc), mc));
  }

  @Test
  public void testLogBigIntegerBigDecimal() {
    test(1, 1, BigInteger.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.log(n1, n2, mc), (n1,n2) -> BigDecimalMath.log(n2, mc).divide(BigDecimalMath.log(new BigDecimal(n1), mc), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), Math.abs(d10()), BigInteger.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.log(n1, n2, mc), (n1,n2) -> BigDecimalMath.log(n2, mc).divide(BigDecimalMath.log(new BigDecimal(n1), mc), mc));
  }

  @Test
  public void testLogBigDecimalBigDecimal() {
    test(1, 1, BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.log(n1, n2, mc), (n1,n2) -> BigDecimalMath.log(n2, mc).divide(BigDecimalMath.log(n1, mc), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), Math.abs(d10()), BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.log(n1, n2, mc), (n1,n2) -> BigDecimalMath.log(n2, mc).divide(BigDecimalMath.log(n1, mc), mc));
  }

  @Test
  public void testPowDoubleDouble() {
    test(0, 0, double.class, double.class, double.class, (n1,n2) -> SafeMath.pow(n1, n2), (n1,n2) -> Math.pow(n1, n2));
    test(1, 1, double.class, double.class, double.class, (n1,n2) -> SafeMath.pow(n1, n2), (n1,n2) -> Math.pow(n1, n2));
    test(-1, -1, double.class, double.class, double.class, (n1,n2) -> SafeMath.pow(n1, n2), (n1,n2) -> Math.pow(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0(), d0(), double.class, double.class, double.class, (n1,n2) -> SafeMath.pow(n1, n2), (n1,n2) -> Math.pow(n1, n2));
  }

  @Test
  public void testPowBigIntegerBigInteger() {
    test(0, 0, BigInteger.class, BigInteger.class, BigInteger.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(new BigDecimal(n1), new BigDecimal(n2), mc).toBigInteger());
    for (int i = 0; i < numTests; ++i)
      test1(d0(), d0(), BigInteger.class, BigInteger.class, BigInteger.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(new BigDecimal(n1), new BigDecimal(n2), mc).toBigInteger());
  }

  @Test
  public void testPowBigDecimalBigInteger() {
    test(0, 0, BigDecimal.class, BigInteger.class, BigDecimal.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(n1, new BigDecimal(n2), mc));
    for (int i = 0; i < numTests; ++i)
      test1(d0(), d0(), BigDecimal.class, BigInteger.class, BigDecimal.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(n1, new BigDecimal(n2), mc));
  }

  @Test
  public void testPowBigIntegerBigDecimal() {
    test(0, 0, BigInteger.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(new BigDecimal(n1), n2, mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d0()), Math.abs(d0()), BigInteger.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(new BigDecimal(n1), n2, mc));
  }

  @Test
  public void testPowBigDecimalBigDecimal() {
    test(0, 0, BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(n1, n2, mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d0()), Math.abs(d0()), BigDecimal.class, BigDecimal.class, BigDecimal.class, (n1,n2) -> SafeMath.pow(n1, n2, mc), (n1,n2) -> BigDecimalMath.pow(n1, n2, mc));
  }

  @Test
  public void testLog10Double() {
    test(0, double.class, SafeMath::log10, n -> Math.log10(n));
    test(1, double.class, SafeMath::log10, n -> Math.log10(n));
    test(-1, double.class, SafeMath::log10, n -> Math.log10(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::log10, n -> Math.log10(n));
  }

  @Test
  public void testLog10BigInteger() {
    test(1, BigInteger.class, BigDecimal.class, n -> SafeMath.log10(n, mc), n -> BigDecimalMath.log10(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), BigInteger.class, BigDecimal.class, n -> SafeMath.log10(n, mc), n -> BigDecimalMath.log10(new BigDecimal(n), mc));
  }

  @Test
  public void testLog10BigDecimal() {
    test(1, BigDecimal.class, BigDecimal.class, n -> SafeMath.log10(n, mc), n -> BigDecimalMath.log10(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), BigDecimal.class, BigDecimal.class, n -> SafeMath.log10(n, mc), n -> BigDecimalMath.log10(n, mc));
  }

  @Test
  public void testLog2Double() {
    test(0, double.class, SafeMath::log2, n -> Math.log(n) / Math.log(2));
    test(1, double.class, SafeMath::log2, n -> Math.log(n) / Math.log(2));
    test(-1, double.class, SafeMath::log2, n -> Math.log(n) / Math.log(2));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::log2, n -> Math.log(n) / Math.log(2));
  }

  @Test
  public void testLog2BigInteger() {
    test(1, BigInteger.class, BigDecimal.class, n -> SafeMath.log2(n, mc), n -> BigDecimalMath.log2(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), BigInteger.class, BigDecimal.class, n -> SafeMath.log2(n, mc), n -> BigDecimalMath.log2(new BigDecimal(n), mc));
  }

  @Test
  public void testLog2BigDecimal() {
    test(1, BigDecimal.class, BigDecimal.class, n -> SafeMath.log2(n, mc), n -> BigDecimalMath.log2(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d10()), BigDecimal.class, BigDecimal.class, n -> SafeMath.log2(n, mc), n -> BigDecimalMath.log2(n, mc));
  }

  @Test
  public void testMaxByteByte() {
    test(0, 0, byte.class, byte.class, byte.class, SafeMath::max, (n1,n2) -> (byte)Math.max(n1, n2));
    test(1, 1, byte.class, byte.class, byte.class, SafeMath::max, (n1,n2) -> (byte)Math.max(n1, n2));
    test(-1, -1, byte.class, byte.class, byte.class, SafeMath::max, (n1,n2) -> (byte)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, byte.class, byte.class, SafeMath::max, (n1,n2) -> (byte)Math.max(n1, n2));
  }

  @Test
  public void testMaxByteShort() {
    test(0, 0, byte.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    test(1, 1, byte.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    test(-1, -1, byte.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
  }

  @Test
  public void testMaxByteInt() {
    test(0, 0, byte.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(1, 1, byte.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(-1, -1, byte.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
  }

  @Test
  public void testMaxByteLong() {
    test(0, 0, byte.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, byte.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, byte.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxByteFloat() {
    test(0, 0, byte.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, byte.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, byte.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxByteDouble() {
    test(0, 0, byte.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, byte.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, byte.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxShortByte() {
    test(0, 0, short.class, byte.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    test(1, 1, short.class, byte.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    test(-1, -1, short.class, byte.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, byte.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
  }

  @Test
  public void testMaxShortShort() {
    test(0, 0, short.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    test(1, 1, short.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    test(-1, -1, short.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, short.class, short.class, SafeMath::max, (n1,n2) -> (short)Math.max(n1, n2));
  }

  @Test
  public void testMaxShortInt() {
    test(0, 0, short.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(1, 1, short.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(-1, -1, short.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
  }

  @Test
  public void testMaxShortLong() {
    test(0, 0, short.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, short.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, short.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (long i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxShortFloat() {
    test(0, 0, short.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, short.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, short.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (float i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxShortDouble() {
    test(0, 0, short.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, short.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, short.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (double i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxIntByte() {
    test(0, 0, int.class, byte.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(1, 1, int.class, byte.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(-1, -1, int.class, byte.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, byte.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
  }

  @Test
  public void testMaxIntShort() {
    test(0, 0, int.class, short.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(1, 1, int.class, short.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(-1, -1, int.class, short.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, short.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
  }

  @Test
  public void testMaxIntInt() {
    test(0, 0, int.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(1, 1, int.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    test(-1, -1, int.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, int.class, int.class, SafeMath::max, (n1,n2) -> (int)Math.max(n1, n2));
  }

  @Test
  public void testMaxIntLong() {
    test(0, 0, int.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, int.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, int.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxIntFloat() {
    test(0, 0, int.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, int.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, int.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxIntDouble() {
    test(0, 0, int.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, int.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, int.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxLongByte() {
    test(0, 0, long.class, byte.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, long.class, byte.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, long.class, byte.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, byte.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxLongShort() {
    test(0, 0, long.class, short.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, long.class, short.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, long.class, short.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, short.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxLongInt() {
    test(0, 0, long.class, int.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, long.class, int.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, long.class, int.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, int.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxLongLong() {
    test(0, 0, long.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(1, 1, long.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    test(-1, -1, long.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, long.class, long.class, SafeMath::max, (n1,n2) -> (long)Math.max(n1, n2));
  }

  @Test
  public void testMaxLongFloat() {
    test(0, 0, long.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, long.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, long.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (float i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxLongDouble() {
    test(0, 0, long.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, long.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, long.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (double i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxFloatByte() {
    test(0, 0, float.class, byte.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, float.class, byte.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, float.class, byte.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, byte.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxFloatShort() {
    test(0, 0, float.class, short.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, float.class, short.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, float.class, short.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, short.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxFloatInt() {
    test(0, 0, float.class, int.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, float.class, int.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, float.class, int.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, int.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxFloatLong() {
    test(0, 0, float.class, long.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, float.class, long.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, float.class, long.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (long i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, long.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxFloatFloat() {
    test(0, 0, float.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, float.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, float.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, float.class, float.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxFloatDouble() {
    test(0, 0, float.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, float.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, float.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (double i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxDoubleByte() {
    test(0, 0, double.class, byte.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, double.class, byte.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, double.class, byte.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, byte.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxDoubleShort() {
    test(0, 0, double.class, short.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, double.class, short.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, double.class, short.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, short.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxDoubleInt() {
    test(0, 0, double.class, int.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, double.class, int.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, double.class, int.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, int.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxDoubleLong() {
    test(0, 0, double.class, long.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, double.class, long.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, double.class, long.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (long i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, long.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxDoubleFloat() {
    test(0, 0, double.class, float.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, double.class, float.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, double.class, float.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (float i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, float.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMaxDoubleDouble() {
    test(0, 0, double.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(1, 1, double.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    test(-1, -1, double.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, double.class, double.class, SafeMath::max, (n1,n2) -> Math.max(n1, n2));
  }

  @Test
  public void testMinByteByte() {
    test(0, 0, byte.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, byte.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, byte.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinByteShort() {
    test(0, 0, byte.class, short.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, byte.class, short.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, byte.class, short.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, short.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinByteInt() {
    test(0, 0, byte.class, int.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, byte.class, int.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, byte.class, int.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, int.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinByteLong() {
    test(0, 0, byte.class, long.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, byte.class, long.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, byte.class, long.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, long.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinByteFloat() {
    test(0, 0, byte.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, byte.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, byte.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinByteDouble() {
    test(0, 0, byte.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, byte.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, byte.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, byte.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinShortByte() {
    test(0, 0, short.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, short.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, short.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinShortShort() {
    test(0, 0, short.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(1, 1, short.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(-1, -1, short.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
  }

  @Test
  public void testMinShortInt() {
    test(0, 0, short.class, int.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(1, 1, short.class, int.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(-1, -1, short.class, int.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, int.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
  }

  @Test
  public void testMinShortLong() {
    test(0, 0, short.class, long.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(1, 1, short.class, long.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(-1, -1, short.class, long.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    for (long i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, long.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
  }

  @Test
  public void testMinShortFloat() {
    test(0, 0, short.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, short.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, short.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (float i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinShortDouble() {
    test(0, 0, short.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, short.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, short.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (double i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, short.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinIntByte() {
    test(0, 0, int.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, int.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, int.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinIntShort() {
    test(0, 0, int.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(1, 1, int.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(-1, -1, int.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
  }

  @Test
  public void testMinIntInt() {
    test(0, 0, int.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    test(1, 1, int.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    test(-1, -1, int.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
  }

  @Test
  public void testMinIntLong() {
    test(0, 0, int.class, long.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    test(1, 1, int.class, long.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    test(-1, -1, int.class, long.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, long.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
  }

  @Test
  public void testMinIntFloat() {
    test(0, 0, int.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, int.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, int.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinIntDouble() {
    test(0, 0, int.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, int.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, int.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, int.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinLongByte() {
    test(0, 0, long.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(1, 1, long.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    test(-1, -1, long.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, byte.class, byte.class, SafeMath::min, (n1,n2) -> (byte)Math.min(n1, n2));
  }

  @Test
  public void testMinLongShort() {
    test(0, 0, long.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(1, 1, long.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    test(-1, -1, long.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, short.class, short.class, SafeMath::min, (n1,n2) -> (short)Math.min(n1, n2));
  }

  @Test
  public void testMinLongInt() {
    test(0, 0, long.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    test(1, 1, long.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    test(-1, -1, long.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, int.class, int.class, SafeMath::min, (n1,n2) -> (int)Math.min(n1, n2));
  }

  @Test
  public void testMinLongLong() {
    test(0, 0, long.class, long.class, long.class, SafeMath::min, (n1,n2) -> (long)Math.min(n1, n2));
    test(1, 1, long.class, long.class, long.class, SafeMath::min, (n1,n2) -> (long)Math.min(n1, n2));
    test(-1, -1, long.class, long.class, long.class, SafeMath::min, (n1,n2) -> (long)Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, long.class, long.class, SafeMath::min, (n1,n2) -> (long)Math.min(n1, n2));
  }

  @Test
  public void testMinLongFloat() {
    test(0, 0, long.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, long.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, long.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (float i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinLongDouble() {
    test(0, 0, long.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, long.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, long.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (double i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, long.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinFloatByte() {
    test(0, 0, float.class, byte.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, float.class, byte.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, float.class, byte.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, byte.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinFloatShort() {
    test(0, 0, float.class, short.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, float.class, short.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, float.class, short.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, short.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinFloatInt() {
    test(0, 0, float.class, int.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, float.class, int.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, float.class, int.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, int.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinFloatLong() {
    test(0, 0, float.class, long.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, float.class, long.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, float.class, long.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (long i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, long.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinFloatFloat() {
    test(0, 0, float.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, float.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, float.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, float.class, float.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinFloatDouble() {
    test(0, 0, float.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, float.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, float.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (double i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, float.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinDoubleByte() {
    test(0, 0, double.class, byte.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, double.class, byte.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, double.class, byte.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, byte.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinDoubleShort() {
    test(0, 0, double.class, short.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, double.class, short.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, double.class, short.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, short.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinDoubleInt() {
    test(0, 0, double.class, int.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, double.class, int.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, double.class, int.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, int.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinDoubleLong() {
    test(0, 0, double.class, long.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, double.class, long.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, double.class, long.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (long i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, long.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinDoubleFloat() {
    test(0, 0, double.class, float.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, double.class, float.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, double.class, float.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (float i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, float.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testMinDoubleDouble() {
    test(0, 0, double.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(1, 1, double.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    test(-1, -1, double.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, d0() * 10, double.class, double.class, double.class, SafeMath::min, (n1,n2) -> Math.min(n1, n2));
  }

  @Test
  public void testRoundByte() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, byte.class, n -> SafeMath.round(n, s), n -> n);
      test(1, byte.class, n -> SafeMath.round(n, s), n -> n);
      test(-1, byte.class, n -> SafeMath.round(n, s), n -> n);
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, byte.class, n -> SafeMath.round(n, s), n -> n);
    }
  }

  @Test
  public void testRoundShort() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, short.class, n -> SafeMath.round(n, s), n -> n);
      test(1, short.class, n -> SafeMath.round(n, s), n -> n);
      test(-1, short.class, n -> SafeMath.round(n, s), n -> n);
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, short.class, n -> SafeMath.round(n, s), n -> n);
    }
  }

  @Test
  public void testRoundInt() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, int.class, n -> SafeMath.round(n, s), n -> n);
      test(1, int.class, n -> SafeMath.round(n, s), n -> n);
      test(-1, int.class, n -> SafeMath.round(n, s), n -> n);
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, int.class, n -> SafeMath.round(n, s), n -> n);
    }
  }

  @Test
  public void testRoundLong() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, long.class, n -> SafeMath.round(n, s), n -> n);
      test(1, long.class, n -> SafeMath.round(n, s), n -> n);
      test(-1, long.class, n -> SafeMath.round(n, s), n -> n);
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, long.class, n -> SafeMath.round(n, s), n -> n);
    }
  }

  @Test
  public void testRoundFloat() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, float.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
      test(1, float.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
      test(-1, float.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, float.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
    }
  }

  private static void testRoundFloatScale(final RoundingMode rm) {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, float.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
      test(1, float.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
      test(-1, float.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, float.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
    }
  }

  @Test
  public void testRoundFloatScaleDown() {
    testRoundFloatScale(RoundingMode.DOWN);
  }

  @Test
  public void testRoundFloatScaleUp() {
    testRoundFloatScale(RoundingMode.UP);
  }

  @Test
  public void testRoundFloatScaleFloor() {
    testRoundFloatScale(RoundingMode.FLOOR);
  }

  @Test
  public void testRoundFloatScaleCeiling() {
    testRoundFloatScale(RoundingMode.CEILING);
  }

  @Test
  public void testRoundFloatScaleHalfUp() {
    testRoundFloatScale(RoundingMode.HALF_UP);
  }

  @Test
  public void testRoundFloatScaleHalfDown() {
    testRoundFloatScale(RoundingMode.HALF_DOWN);
  }

  @Test
  public void testRoundFloatScaleHalfEven() {
    testRoundFloatScale(RoundingMode.HALF_EVEN);
  }

  @Test
  public void testRoundFloatScaleUnnecessary() {
    testRoundFloatScale(RoundingMode.UNNECESSARY);
  }

  @Test
  public void testRoundDouble() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, double.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
      test(1, double.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
      test(-1, double.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, double.class, n -> SafeMath.round(n, s), n -> SafeMath.round(n, s));
    }
  }

  private static void testRoundDoubleScale(final RoundingMode rm) {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, double.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
      test(1, double.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
      test(-1, double.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, double.class, n -> SafeMath.round(n, s, rm), n -> SafeMath.round(n, s, rm));
    }
  }

  @Test
  public void testRoundDoubleScaleDown() {
    testRoundDoubleScale(RoundingMode.DOWN);
  }

  @Test
  public void testRoundDoubleScaleUp() {
    testRoundDoubleScale(RoundingMode.UP);
  }

  @Test
  public void testRoundDoubleScaleFloor() {
    testRoundDoubleScale(RoundingMode.FLOOR);
  }

  @Test
  public void testRoundDoubleScaleCeiling() {
    testRoundDoubleScale(RoundingMode.CEILING);
  }

  @Test
  public void testRoundDoubleScaleHalfUp() {
    testRoundDoubleScale(RoundingMode.HALF_UP);
  }

  @Test
  public void testRoundDoubleScaleHalfDown() {
    testRoundDoubleScale(RoundingMode.HALF_DOWN);
  }

  @Test
  public void testRoundDoubleScaleHalfEven() {
    testRoundDoubleScale(RoundingMode.HALF_EVEN);
  }

  @Test
  public void testRoundDoubleScaleUnnecessary() {
    testRoundDoubleScale(RoundingMode.UNNECESSARY);
  }

  @Test
  public void testRoundBigInteger() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, BigInteger.class, n -> SafeMath.round(n, s), n -> n);
      test(1, BigInteger.class, n -> SafeMath.round(n, s), n -> n);
      test(-1, BigInteger.class, n -> SafeMath.round(n, s), n -> n);
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, BigInteger.class, n -> SafeMath.round(n, s), n -> n);
    }
  }

  @Test
  public void testRoundBigDecimal() {
    for (int j = 0; j < 3; ++j) {
      final int s = j;
      test(0, BigDecimal.class, n -> SafeMath.round(n, s), n -> BigDecimals.setScale(n, s, RoundingMode.DOWN));
      test(1, BigDecimal.class, n -> SafeMath.round(n, s), n -> BigDecimals.setScale(n, s, RoundingMode.DOWN));
      test(-1, BigDecimal.class, n -> SafeMath.round(n, s), n -> BigDecimals.setScale(n, s, RoundingMode.DOWN));
      for (int i = 0; i < numTests; ++i)
        test(d0() * 10, BigDecimal.class, n -> SafeMath.round(n, s), n -> BigDecimals.setScale(n, s, RoundingMode.DOWN));
    }
  }

  @Test
  public void testSignumByte() {
    test(0, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(1, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(-1, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
  }

  @Test
  public void testSignumShort() {
    test(0, short.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(1, short.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(-1, short.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, short.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
  }

  @Test
  public void testSignumInt() {
    test(0, int.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(1, int.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(-1, int.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, int.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
  }

  @Test
  public void testSignumLong() {
    test(0, long.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(1, long.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(-1, long.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, long.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
  }

  @Test
  public void testSignumFloat() {
    test(0, float.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(1, float.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(-1, float.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, float.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
  }

  @Test
  public void testSignumDouble() {
    test(0, double.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(1, double.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    test(-1, double.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, double.class, byte.class, SafeMath::signum, n -> (byte)Math.signum(n));
  }

  @Test
  public void testSignumBigInteger() {
    test(0, BigInteger.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
    test(1, BigInteger.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
    test(-1, BigInteger.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigInteger.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
  }

  @Test
  public void testSignumBigDecimal() {
    test(0, BigDecimal.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
    test(1, BigDecimal.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
    test(-1, BigDecimal.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
    for (int i = 0; i < numTests; ++i)
      test(d0() * 10, BigDecimal.class, byte.class, SafeMath::signum, n -> (byte)n.signum());
  }

  @Test
  public void testSinDouble() {
    test(0, double.class, SafeMath::sin, n -> Math.sin(n));
    test(1, double.class, SafeMath::sin, n -> Math.sin(n));
    test(-1, double.class, SafeMath::sin, n -> Math.sin(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::sin, n -> Math.sin(n));
  }

  @Test
  public void testSinBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.sin(n, mc), n -> BigDecimalMath.sin(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.sin(n, mc), n -> BigDecimalMath.sin(new BigDecimal(n), mc));
  }

  @Test
  public void testSinBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.sin(n, mc), n -> BigDecimalMath.sin(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.sin(n, mc), n -> BigDecimalMath.sin(n, mc));
  }

  @Test
  public void testSqrtDouble() {
    test(0, double.class, SafeMath::sqrt, n -> Math.sqrt(n));
    test(1, double.class, SafeMath::sqrt, n -> Math.sqrt(n));
    test(-1, double.class, SafeMath::sqrt, n -> Math.sqrt(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::sqrt, n -> Math.sqrt(n));
  }

  @Test
  public void testSqrtBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.sqrt(n, mc), n -> BigDecimalMath.sqrt(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d0()), BigInteger.class, BigDecimal.class, n -> SafeMath.sqrt(n, mc), n -> BigDecimalMath.sqrt(new BigDecimal(n), mc));
  }

  @Test
  public void testSqrtBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.sqrt(n, mc), n -> BigDecimalMath.sqrt(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(Math.abs(d0()), BigDecimal.class, BigDecimal.class, n -> SafeMath.sqrt(n, mc), n -> BigDecimalMath.sqrt(n, mc));
  }

  @Test
  public void testTanDouble() {
    test(0, double.class, SafeMath::tan, n -> Math.tan(n));
    test(1, double.class, SafeMath::tan, n -> Math.tan(n));
    test(-1, double.class, SafeMath::tan, n -> Math.tan(n));
    for (int i = 0; i < numTests; ++i)
      test(d0(), double.class, SafeMath::tan, n -> Math.tan(n));
  }

  @Test
  public void testTanBigInteger() {
    test(0, BigInteger.class, BigDecimal.class, n -> SafeMath.tan(n, mc), n -> BigDecimalMath.tan(new BigDecimal(n), mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigInteger.class, BigDecimal.class, n -> SafeMath.tan(n, mc), n -> BigDecimalMath.tan(new BigDecimal(n), mc));
  }

  @Test
  public void testTanBigDecimal() {
    test(0, BigDecimal.class, BigDecimal.class, n -> SafeMath.tan(n, mc), n -> BigDecimalMath.tan(n, mc));
    for (int i = 0; i < numTests; ++i)
      test(d0(), BigDecimal.class, BigDecimal.class, n -> SafeMath.tan(n, mc), n -> BigDecimalMath.tan(n, mc));
  }

  private static void testRoundFloat(final RoundingMode rm) {
    for (int i = 0; i < numTests * 100; ++i) {
      float v = i < 100 ? (i + 0.5f) * (random.nextBoolean() ? -1 : 1) : random.nextFloat();
      float r1 = 0, r2 = 0;
      final float delta = Math.ulp(v);
      int j = 0;
      do {
        if (j > 0) {
          if (TestAide.isInDebug())
            // TODO: Place breakpoint here to debug...
            System.console();
          else
            assertEquals(r1, r2, Math.ulp(v));
        }

        try {
          BigDecimal bd = new BigDecimal(v);
          bd = bd.setScale(0, rm);
          r2 = bd.floatValue();
        }
        catch (final ArithmeticException e) {
          r2 = Float.NaN;
        }

        r1 = SafeMath.round(v, rm);
      }
      while ((Float.isNaN(r1) ? !Float.isNaN(r2) : Float.isNaN(r2) || Math.abs(r1 - r2) > delta) && ++j < 100);
    }
  }

  @Test
  public void testRoundFloatDown() {
    testRoundFloat(RoundingMode.DOWN);
  }

  @Test
  public void testRoundFloatUp() {
    testRoundFloat(RoundingMode.UP);
  }

  @Test
  public void testRoundFloatFloor() {
    testRoundFloat(RoundingMode.FLOOR);
  }

  @Test
  public void testRoundFloatCeiling() {
    testRoundFloat(RoundingMode.CEILING);
  }

  @Test
  public void testRoundFloatHalfUp() {
    testRoundFloat(RoundingMode.HALF_UP);
  }

  @Test
  public void testRoundFloatHalfDown() {
    testRoundFloat(RoundingMode.HALF_DOWN);
  }

  @Test
  public void testRoundFloatHalfEven() {
    testRoundFloat(RoundingMode.HALF_EVEN);
  }

  @Test
  public void testRoundFloatUnnecessary() {
    testRoundFloat(RoundingMode.UNNECESSARY);
  }

  private static void testRoundDouble(final RoundingMode rm) {
    for (int i = 0; i < numTests * 100; ++i) {
      double v = i < 100 ? (i + 0.5d) * (random.nextBoolean() ? -1 : 1) : random.nextFloat();
      double r1 = 0, r2 = 0;
      final double delta = Math.ulp(v);
      int j = 0;
      do {
        if (j > 0) {
          if (TestAide.isInDebug())
            // TODO: Place breakpoint here to debug...
            System.err.println(r1 + " != " + r2);
          else
            assertEquals(r1, r2, Math.ulp(v));
        }

        try {
          BigDecimal bd = new BigDecimal(v);
          bd = bd.setScale(0, rm);
          r2 = bd.doubleValue();
        }
        catch (final ArithmeticException e) {
          r2 = Double.NaN;
        }

        r1 = SafeMath.round(v, rm);
      }
      while ((Double.isNaN(r1) ? !Double.isNaN(r2) : Double.isNaN(r2) || Math.abs(r1 - r2) > delta) && ++j < 100);
    }
  }

  @Test
  public void testRoundDoubleDown() {
    testRoundDouble(RoundingMode.DOWN);
  }

  @Test
  public void testRoundDoubleUp() {
    testRoundDouble(RoundingMode.UP);
  }

  @Test
  public void testRoundDoubleFloor() {
    testRoundDouble(RoundingMode.FLOOR);
  }

  @Test
  public void testRoundDoubleCeiling() {
    testRoundDouble(RoundingMode.CEILING);
  }

  @Test
  public void testRoundDoubleHalfUp() {
    testRoundDouble(RoundingMode.HALF_UP);
  }

  @Test
  public void testRoundDoubleHalfDown() {
    testRoundDouble(RoundingMode.HALF_DOWN);
  }

  @Test
  public void testRoundDoubleHalfEven() {
    testRoundDouble(RoundingMode.HALF_EVEN);
  }

  @Test
  public void testRoundDoubleUnnecessary() {
    testRoundDouble(RoundingMode.UNNECESSARY);
  }
}