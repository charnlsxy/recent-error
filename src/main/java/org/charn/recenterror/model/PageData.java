package org.charn.recenterror.model;

import lombok.Data;

import java.util.List;

@Data
public class PageData<T> {
    private List<T> data;
    private Page page;
}
