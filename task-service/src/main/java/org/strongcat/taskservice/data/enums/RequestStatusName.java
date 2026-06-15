package org.strongcat.taskservice.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum RequestStatusName {
    CREATED("CREATED"),
    ANALYZING("ANALYZING"),
    ADDRESSED("ADDRESSED"),
    NO_CANDIDATES("NO_CANDIDATES"),
    COMPLETED("COMPLETED"),
    ACCEPTED("ACCEPTED"),
    CANCELLED("CANCELLED"),
    FAILED("FAILED");

    private final String databaseName;

    public static RequestStatusName fromDatabaseName(String databaseName) {
        return Arrays.stream(values())
                .filter(status -> status.databaseName.equals(databaseName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown request status: " + databaseName));
    }
}
