package org.example.template.service;

import com.baomidou.mybatisplus.extension.service.IService;
import jakarta.servlet.http.HttpServletRequest;
import org.example.template.model.dto.shortlink.ShortLinkCreateRequest;
import org.example.template.model.entity.ShortLink;
import org.example.template.model.vo.ShortLinkVO;

import java.util.List;

/**
 * 短链接服务
 */
public interface ShortLinkService extends IService<ShortLink> {

    /**
     * 创建短链接
     * @param request 创建请求
     * @param userId 当前用户ID
     * @return 短链接视图对象
     */
    ShortLinkVO createShortLink(ShortLinkCreateRequest request, Long userId);

    /**
     * 根据短码获取原始链接并记录访问
     * @param shortCode 短链接码
     * @param request HTTP请求对象，用于获取访问信息
     * @return 原始链接
     */
    String getOriginalUrl(String shortCode, HttpServletRequest request);

    /**
     * 获取用户的短链接列表
     * @param userId 用户ID
     * @return 短链接列表
     */
    List<ShortLinkVO> listUserShortLinks(Long userId);

    /**
     * 删除短链接
     * @param id 短链接ID
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteShortLink(Long id, Long userId);
}