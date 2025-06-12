package org.example.template.model.dto.shortlink;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建短链接请求
 */
@Data
public class ShortLinkCreateRequest implements Serializable {
    /**
     * 原始链接
     */
    private String originalUrl;

    /**
     * 自定义短码（可选）
     */
    private String customShortCode;

    /**
     * 过期时间（可选，null表示永不过期）
     */
    private Date expireTime;

    /**
     * 链接描述（可选）
     */
    private String description;

    private static final long serialVersionUID = 1L;
}