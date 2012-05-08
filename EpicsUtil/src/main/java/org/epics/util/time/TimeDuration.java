/**
 * Copyright (C) 2010-12 Brookhaven National Laboratory
 * All rights reserved. Use is subject to license terms.
 */
package org.epics.util.time;

import java.text.DecimalFormat;

/**
 * A duration of time (such as 3 seconds, 30 ms, 1 nanosec) at the nanosecond precision.
 * The duration is stored as 96 bits, 64 for seconds and 32 for nanoseconds within
 * the second. This makes the representation equivalent to the {@link TimeStamp} class.
 * <p>
 * This class can be used both to represent a span of time, or
 * as a relative timestamp (e.g. 3 seconds before or after a reference). As such,
 * it allows for "negative" durations. Even for negative values the nanoseconds
 * is positive. For example, -1.5 seconds will be stored as -2 seconds and
 * 500,000,000 nanoseconds.
 * <p>
 * Note that while TimeStamp are usually created according to system clocks which
 * takes into account leap seconds, all the math operations on TimeStamps do
 * not take leap seconds into account.
 * <h3>JSR 310 compatibility</h3>
 * This class is essentially equivalent to {@code javax.time.Duration}.
 * When it will be released, the plan is to phase out this class where appropriate.
 * 
 * @author carcassi
 */
public class TimeDuration {

    private final long sec;
    private final int nanoSec;
    
    private static final int NANOSEC_IN_SEC = 1000000000;

    private TimeDuration(long sec, int nanoSec) {
        if (nanoSec < 0 || nanoSec >= NANOSEC_IN_SEC)
            throw new IllegalArgumentException("Nanoseconds must be between 0 and 999,999,999. Was " + nanoSec);
        this.nanoSec = nanoSec;
        this.sec = sec;
    }

    /**
     * The amount of nanoseconds for the duration. This value is guaranteed to be between
     * 0 and 999,999,999.
     * 
     * @return the nanosecond part
     */
    public int getNanoSec() {
        return nanoSec;
    }

    /**
     * The amount of seconds for the duration. This can be both positive or negative.
     * 
     * @return the second part
     */
    public long getSec() {
        return sec;
    }
    
    /**
     * A new duration in hours.
     * 
     * @param hour hours
     * @return a new duration
     */
    public static TimeDuration ofHours(double hour) {
        return ofNanos((long) (hour * 60 * 60 * 1000000000));
    }
    
    /**
     * A new duration in minutes.
     * 
     * @param min minutes
     * @return a new duration
     */
    public static TimeDuration ofMinutes(double min) {
        return ofNanos((long) (min * 60 * 1000000000));
    }
    
    /**
     * A new duration in seconds.
     * 
     * @param sec seconds
     * @return a new duration
     */
    public static TimeDuration ofSeconds(double sec) {
        return ofNanos((long) (sec * 1000000000));
    }
    
    /**
     * A new duration in hertz, will convert to the length of the period.
     * 
     * @param hz frequency to be converted to a duration
     * @return a new duration
     */
    public static TimeDuration ofHertz(double hz) {
        if (hz <= 0.0) {
            throw new IllegalArgumentException("Frequency has to be greater than 0.0");
        }
        return ofNanos((long) (1000000000.0 / hz));
    }

    /**
     * A new duration in milliseconds.
     * @param ms milliseconds of the duration
     * @return a new duration
     */
    public static TimeDuration ofMillis(int ms) {
        return ofNanos(((long) ms) * 1000000);
    }

    private static TimeDuration ofNanos(double durationInNanos) {
        long sec = (long) (durationInNanos / NANOSEC_IN_SEC);
        int nanoSec = (int) (durationInNanos - sec * NANOSEC_IN_SEC);
        return new TimeDuration(sec, nanoSec);
    }

    
    /**
     * A new duration in nanoseconds.
     * @param nanoSec nanoseconds of the duration
     * @return a new duration
     * @throws IllegalArgumentException if the duration is negative
     */
    public static TimeDuration ofNanos(long nanoSec) {
        return createWithCarry(nanoSec / NANOSEC_IN_SEC, (int) (nanoSec % NANOSEC_IN_SEC));
    }

    /**
     * Returns a new duration which is smaller by the given factor.
     * 
     * @param factor constant to divide
     * @return a new duration
     */
    public TimeDuration dividedBy(int factor) {
        return createWithCarry(sec / factor, ((sec % factor) * NANOSEC_IN_SEC + (long) nanoSec) / factor);
    }

    /**
     * Returns a new duration which is bigger by the given factor.
     *
     * @param factor constant to multiply
     * @return a new duration
     */
    public TimeDuration multipliedBy(int factor) {
        return createWithCarry(sec * factor, ((long) nanoSec) * factor);
    }

    private static TimeDuration createWithCarry(long seconds, long nanos) {
        if (nanos > 999999999) {
            seconds = seconds + nanos / 1000000000;
            nanos = nanos % 1000000000;
        }

        if (nanos < 0) {
            long pastSec = nanos / 1000000000;
            pastSec--;
            seconds += pastSec;
            nanos -= pastSec * 1000000000;
        }

        return new TimeDuration(seconds, (int) nanos);
    }
    
    /**
     * Returns the sum of this duration with the given.
     * 
     * @param duration another duration
     * @return a new duration
     */
    public TimeDuration plus(TimeDuration duration) {
        return createWithCarry(sec + duration.getSec(), nanoSec + duration.getNanoSec());
    }
    
    /**
     * Returns the difference between this duration and the given.
     * 
     * @param duration another duration
     * @return a new duration
     */
    public TimeDuration minus(TimeDuration duration) {
        return createWithCarry(sec - duration.getSec(), nanoSec - duration.getNanoSec());
    }

    /**
     * Returns a time interval that lasts this duration and is centered
     * around the given timestamp.
     * 
     * @param reference a timestamp
     * @return a new time interval
     */
    public TimeInterval around(TimeStamp reference) {
        TimeDuration half = this.dividedBy(2);
        return TimeInterval.between(reference.minus(half), reference.plus(half));
    }

    /**
     * Returns a time interval that lasts this duration and starts from the
     * given timestamp.
     *
     * @param reference a timestamp
     * @return a new time interval
     */
    public TimeInterval after(TimeStamp reference) {
        return TimeInterval.between(reference, reference.plus(this));
    }

    /**
     * Returns a time interval that lasts this duration and ends at the
     * given timestamp.
     *
     * @param reference a timestamp
     * @return a new time interval
     */
    public TimeInterval before(TimeStamp reference) {
        return TimeInterval.between(reference.minus(this), reference);
    }

    private static final DecimalFormat format = new DecimalFormat("000000000");

    /**
     * The number of seconds concatenated with the number of nanoseconds (12.500000000
     * for 12.5 seconds).
     * 
     * @return the string representation
     */
    @Override
    public String toString() {
        return sec + "." + format.format(nanoSec);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Long.valueOf(nanoSec).hashCode();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj instanceof TimeDuration) {
            if (nanoSec == ((TimeDuration) obj).nanoSec)
                return true;
        }

        return false;
    }

}
