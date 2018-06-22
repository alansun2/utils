package com.ehu.response.elasticsearch;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HitsDetail<T> {
    private String _index;
    private String _type;
    private String _id;
    private Integer _score;
    private T _source;
}