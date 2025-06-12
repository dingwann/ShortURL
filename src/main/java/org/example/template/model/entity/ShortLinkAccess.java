package org.example.template.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 短链接访问记录
 * @TableName short_link_access
 */
@TableName(value = "short_link_access")
@Data
public class ShortLinkAccess implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 短链接id
     */
    private Long linkId;

    /**
     * 访问者IP
     */
    private String ip;

    /**
     * 用户代理
     */
    private String userAgent;

    /**
     * 来源页面
     */
    private String referer;

    /**
     * 访问时间
     */
    private Date accessTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}