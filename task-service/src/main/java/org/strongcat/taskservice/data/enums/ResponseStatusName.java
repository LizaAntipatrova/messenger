package org.strongcat.taskservice.data.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum ResponseStatusName {
    PENDING("PENDING"),
    SENT("SENT"),
    ACCEPTED("ACCEPTED"),
    DECLINED("DECLINED"),
    EXPIRED("EXPIRED");

    private final String databaseName;

    public static ResponseStatusName fromDatabaseName(String databaseName) {
        return Arrays.stream(values())
                .filter(status -> status.databaseName.equals(databaseName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown response status: " + databaseName));
    }
}
