package org.example.template.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.example.template.exception.BusinessException;
import org.example.template.exception.ErrorCode;
import org.example.template.exception.ThrowUtils;
import org.example.template.mapper.ShortLinkAccessMapper;
import org.example.template.mapper.ShortLinkMapper;
import org.example.template.model.dto.shortlink.ShortLinkCreateRequest;
import org.example.template.model.entity.ShortLink;
import org.example.template.model.entity.ShortLinkAccess;
import org.example.template.model.vo.ShortLinkVO;
import org.example.template.service.ShortLinkService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 短链接服务实现
 */
@Service
public class ShortLinkServiceImpl extends ServiceImpl<ShortLinkMapper, ShortLink> implements ShortLinkService {

    @Resource
    private ShortLinkAccessMapper shortLinkAccessMapper;

    @Resource
    private ShortLinkMapper shortLinkMapper;

    /**
     * 生成短链接码
     * @return 短链接码
     */
    private String generateShortCode() {
        // 使用Hutool的IdUtil生成6位随机字符串作为短码
        return IdUtil.fastSimpleUUID().substring(0, 6);
    }

    /**
     * 创建短链接
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ShortLinkVO createShortLink(ShortLinkCreateRequest request, Long userId) {
        // 参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(request.getOriginalUrl()), ErrorCode.PARAMS_ERROR, "原始链接不能为空");
        
        // 创建短链接实体
        ShortLink shortLink = new ShortLink();
        shortLink.setUserId(userId);
        shortLink.setOriginalUrl(request.getOriginalUrl());
        shortLink.setDescription(request.getDescription());
        shortLink.setExpireTime(request.getExpireTime());
        shortLink.setClickCount(0L);
        
        // 处理短码
        String shortCode;
        if (StrUtil.isNotBlank(request.getCustomShortCode())) {
            // 使用自定义短码
            shortCode = request.getCustomShortCode();
            shortLink.setCustomCode(1);
            
            // 检查自定义短码是否已存在
            QueryWrapper<ShortLink> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("shortCode", shortCode);
            ThrowUtils.throwIf(this.baseMapper.exists(queryWrapper), ErrorCode.OPERATION_ERROR, "自定义短码已存在");
        } else {
            // 生成随机短码
            shortCode = generateShortCode();
            shortLink.setCustomCode(0);
            
            // 确保生成的短码唯一
            int retryCount = 0;
            while (retryCount < 5) {
                QueryWrapper<ShortLink> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("shortCode", shortCode);
                if (!this.baseMapper.exists(queryWrapper)) {
                    break;
                }
                shortCode = generateShortCode();
                retryCount++;
            }
            
            if (retryCount >= 5) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "短码生成失败，请稍后重试");
            }
        }
        
        shortLink.setShortCode(shortCode);
        
        // 保存短链接
        boolean saveResult = this.save(shortLink);
        ThrowUtils.throwIf(!saveResult, ErrorCode.SYSTEM_ERROR, "短链接创建失败");
        
        // 转换为VO对象返回
        ShortLinkVO shortLinkVO = new ShortLinkVO();
        BeanUtils.copyProperties(shortLink, shortLinkVO);
        shortLinkVO.setCustomCode(shortLink.getCustomCode() == 1);
        shortLinkVO.setShortUrl("http://localhost:8123/" + "short/" + shortCode); // 上线后localhost:8123替换为实际的域名或者配置文件读取
        
        return shortLinkVO;
    }

    /**
     * 根据短码获取原始链接并记录访问
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String getOriginalUrl(String shortCode, HttpServletRequest request) {
        // 参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(shortCode), ErrorCode.PARAMS_ERROR, "短码不能为空");
        
        // 查询短链接
        QueryWrapper<ShortLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("shortCode", shortCode);
        queryWrapper.eq("logicDelete", 0);
        ShortLink shortLink = this.getOne(queryWrapper);
        ThrowUtils.throwIf(shortLink == null, ErrorCode.NOT_FOUND_ERROR, "短链接不存在或已失效");
        
        // 检查是否过期
        if (shortLink.getExpireTime() != null && shortLink.getExpireTime().before(new Date())) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "短链接已过期");
        }

        Long clickCount = shortLink.getClickCount();

        // 原子性地增加点击次数
        shortLinkMapper.incrementClickCount(shortLink.getId(), clickCount);
        // 重新获取最新的短链接信息（包含更新后的点击次数）
        shortLink = this.getById(shortLink.getId());
        
        // 记录访问信息
        ShortLinkAccess access = new ShortLinkAccess();
        access.setLinkId(shortLink.getId());
        access.setIp(request.getRemoteAddr());
        access.setUserAgent(request.getHeader("User-Agent"));
        access.setReferer(request.getHeader("Referer"));
        shortLinkAccessMapper.insert(access);
        
        return shortLink.getOriginalUrl();
    }

    /**
     * 获取用户的短链接列表
     */
    @Override
    public List<ShortLinkVO> listUserShortLinks(Long userId) {
        // 查询用户的短链接
        QueryWrapper<ShortLink> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userId", userId);
        queryWrapper.eq("logicDelete", 0);
        queryWrapper.orderByDesc("createTime");
        List<ShortLink> shortLinks = this.list(queryWrapper);
        
        // 转换为VO列表
        return shortLinks.stream().map(shortLink -> {
            ShortLinkVO vo = new ShortLinkVO();
            BeanUtils.copyProperties(shortLink, vo);
            vo.setCustomCode(shortLink.getCustomCode() == 1);
            vo.setShortUrl("http://localhost:8123/" + "short/" + shortLink.getShortCode()); // 替换为实际域名
            return vo;
        }).collect(Collectors.toList());
    }

    /**
     * 删除短链接
     */
    @Override
    public boolean deleteShortLink(Long id, Long userId) {
        // 查询短链接
        ShortLink shortLink = this.getById(id);
        ThrowUtils.throwIf(shortLink == null, ErrorCode.NOT_FOUND_ERROR, "短链接不存在");
        
        // 验证所有权
        ThrowUtils.throwIf(!shortLink.getUserId().equals(userId), ErrorCode.NO_AUTH_ERROR, "无权操作此短链接");
        
        // 逻辑删除
        return this.removeById(id);
    }
}