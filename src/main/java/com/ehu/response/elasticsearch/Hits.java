package com.ehu.response.elasticsearch;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Hits<T> {
    private List<HitsDetail<T>> hits;
    private Integer total;
    private Integer max_score;
}