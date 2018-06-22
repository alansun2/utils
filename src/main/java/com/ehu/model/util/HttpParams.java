package com.ehu.model.util;

import lombok.Data;
import org.apache.http.message.BasicHeader;

@Data
public class HttpParams {
    private String url;
    private BasicHeader[] headers;
    private String strEntity;
}
