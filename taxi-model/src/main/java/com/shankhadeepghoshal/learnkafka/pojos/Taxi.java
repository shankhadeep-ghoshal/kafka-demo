package com.shankhadeepghoshal.learnkafka.pojos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record Taxi(
    @NotNull @Min(1L) Long id,
    @NotNull
        @Range(
            min = -90,
            max = 90,
            message = "Should not be empty and be between 90 and -90 inclusive")
        Double latitude,
    @NotNull
        @Range(
            min = -180,
            max = 180,
            message = "Should not be empty and be between 180 and -180 inclusive")
        Double longitude) {}
