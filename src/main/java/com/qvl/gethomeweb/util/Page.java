package com.qvl.gethomeweb.util;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {
    private Integer limit;
    private Integer offset;
    private Integer total;
    private List<T> results;
}//class end
