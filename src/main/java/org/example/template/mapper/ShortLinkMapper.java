package org.example.template.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.example.template.model.entity.ShortLink;

/**
 * 短链接 Mapper
 */
public interface ShortLinkMapper extends BaseMapper<ShortLink> {
    
    /**
     * 原子性地增加短链接的点击次数
     * @param linkId 短链接ID
     * @return 影响的行数
     */
    @Update("UPDATE short_link SET clickCount = clickCount + 1 WHERE id = #{linkId} AND clickCount = #{clickCount}")
    int incrementClickCount(@Param("linkId") Long linkId, @Param("clickCount") Long clickCount);
}