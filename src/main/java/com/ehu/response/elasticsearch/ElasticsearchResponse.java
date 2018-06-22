package com.ehu.response.elasticsearch;

import lombok.Getter;
import lombok.Setter;

/**
 * @author alan
 * @createtime 17-12-28 * elasticsearch返回类
 */
@Getter
@Setter
public class ElasticsearchResponse<T> {
    private Hits<T> hits;
}
