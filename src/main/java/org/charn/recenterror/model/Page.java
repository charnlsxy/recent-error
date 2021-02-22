package org.charn.recenterror.model;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class Page {
    @Min(1)
    private Integer page;
    @NotNull
    private Integer pageSize;
    private Long total;
}
