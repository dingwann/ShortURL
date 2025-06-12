package org.example.template.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短链接视图对象
 */
@Data
public class ShortLinkVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 原始链接
     */
    private String originalUrl;

    /**
     * 短链接码
     */
    private String shortCode;

    /**
     * 完整短链接
     */
    private String shortUrl;

    /**
     * 是否自定义短码
     */
    private Boolean customCode;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 链接描述
     */
    private String description;

    /**
     * 点击次数
     */
    private Long clickCount;

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}