package com.github.pgasync.impl.conversion;

import com.github.pgasync.SqlException;
import com.github.pgasync.impl.Oid;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE;
import static java.time.format.DateTimeFormatter.ISO_LOCAL_TIME;

/**
 * @author Antti Laisi
 * <p>
 */
enum TemporalConversions {
    ;
    static final DateTimeFormatter TIMESTAMP_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .toFormatter();

    static final DateTimeFormatter TIMESTAMPZ_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE)
            .appendLiteral(' ')
            .append(ISO_LOCAL_TIME)
            .appendOffset("+HH:mm", "")
            .toFormatter();

    static final DateTimeFormatter TIMEZ_FORMAT = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_TIME)
            .appendOffset("+HH:mm", "")
            .toFormatter();

    static LocalDate toDate(Oid oid, byte[] value) {
        switch (oid) {
            case UNSPECIFIED: // fallthrough
            case DATE:
                String date = new String(value, UTF_8);
                try {
                    return LocalDate.parse(date, ISO_LOCAL_DATE);
                } catch (DateTimeParseException e) {
                    throw new SqlException("Invalid date: " + date);
                }
            default:
                throw new SqlException("Unsupported conversion " + oid.name() + " -> Date");
        }
    }

    static LocalTime toTime(Oid oid, byte[] value) {
        String time = new String(value, UTF_8);
        try {
            switch (oid) {
                case UNSPECIFIED: // fallthrough
                case TIME:
                    return LocalTime.parse(time, ISO_LOCAL_TIME);
                case TIMETZ:
                    return OffsetTime.parse(time, TIMEZ_FORMAT).toLocalTime();
                default:
                    throw new SqlException("Unsupported conversion " + oid.name() + " -> Time");
            }
        } catch (DateTimeParseException e) {
            throw new SqlException("Invalid time: " + time);
        }
    }

    static LocalDateTime toTimestamp(Oid oid, byte[] value) {
        String time = new String(value, UTF_8);
        try {
            switch (oid) {
                case UNSPECIFIED: // fallthrough
                case TIMESTAMP:
                    return LocalDateTime.parse(time, TIMESTAMP_FORMAT);
                case TIMESTAMPTZ:
                    return OffsetDateTime.parse(time, TIMESTAMPZ_FORMAT).toLocalDateTime();
                default:
                    throw new SqlException("Unsupported conversion " + oid.name() + " -> Time");
            }
        } catch (DateTimeParseException e) {
            throw new SqlException("Invalid time: " + time);
        }
    }

    static byte[] fromTime(LocalTime time) {
        return ISO_LOCAL_TIME.format(time).getBytes(UTF_8);
    }

    static byte[] fromDate(LocalDate date) {
        return ISO_LOCAL_DATE.format(date).getBytes(UTF_8);
    }

    static byte[] fromTimestamp(LocalDateTime ts) {
        return TIMESTAMP_FORMAT.format(ts).getBytes(UTF_8);
    }
}
