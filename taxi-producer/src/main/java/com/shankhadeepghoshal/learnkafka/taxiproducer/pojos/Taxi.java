package com.shankhadeepghoshal.learnkafka.taxiproducer.pojos;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.Range;

public record Taxi(
    @NotNull @Min(1L) Long id,
    @NotNull @Range(min = -90, max = 90) Double latitude,
    @NotNull @Range(min = -180, max = 180) Double longitude) {}
