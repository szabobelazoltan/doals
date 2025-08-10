package hu.szbz.hbc.doals.utils;

import java.time.OffsetDateTime;

public class JaxbDateTimeConverter {

    public static OffsetDateTime parse(String input) {
        return OffsetDateTime.parse(input);
    }

    public static String print(OffsetDateTime input) {
        return input.toString();
    }
}
