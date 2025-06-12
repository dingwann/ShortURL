package org.example.template.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短链接
 * @TableName short_link
 */
@TableName(value = "short_link")
@Data
public class ShortLink implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 原始链接
     */
    private String originalUrl;

    /**
     * 短链接码
     */
    private String shortCode;

    /**
     * 是否自定义短码(0-否, 1-是)
     */
    private Integer customCode;

    /**
     * 过期时间(null表示永不过期)
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

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除(逻辑)
     */
    @TableLogic
    private Integer logicDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}