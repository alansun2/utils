package com.tj.model.util;

import com.tj.util.StringUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.InputStream;
import java.security.InvalidParameterException;

/**
 * @author alan
 * @createtime 18-8-10 下午2:22 *
 */
@Getter
@Setter
@ToString
public class AliOssParams {

    public String bucketName;

    public String objectName;

    public InputStream inputStream;

    /**
     * bucket 域名
     */
    private String endPoint;

    private String accessKeyId;

    private String accessKeySecret;

    public void valid() {
        if (StringUtils.hasEmpty(this.endPoint, this.accessKeyId, this.accessKeySecret)) {
            throw new InvalidParameterException("参数有误");
        }
    }
}
