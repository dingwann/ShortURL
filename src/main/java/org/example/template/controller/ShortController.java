package org.example.template.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.template.annotation.AuthCheck;
import org.example.template.common.BaseResponse;
import org.example.template.common.DeleteRequest;
import org.example.template.common.ResultUtils;
import org.example.template.exception.BusinessException;
import org.example.template.exception.ErrorCode;
import org.example.template.exception.ThrowUtils;
import org.example.template.model.dto.shortlink.ShortLinkCreateRequest;
import org.example.template.model.entity.User;
import org.example.template.model.vo.ShortLinkVO;
import org.example.template.service.ShortLinkService;
import org.example.template.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/short")
public class ShortController {

    @Resource
    private ShortLinkService shortLinkService;
    
    @Resource
    private UserService userService;
    
    /**
     * 创建短链接
     */
    @PostMapping("/create")
    public BaseResponse<ShortLinkVO> createShortLink(@RequestBody ShortLinkCreateRequest request, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        ShortLinkVO shortLinkVO = shortLinkService.createShortLink(request, loginUser.getId());
        return ResultUtils.success(shortLinkVO);
    }

    /**
     * 短链接跳转
     */
    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable("shortCode") String shortCode, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String originalUrl = shortLinkService.getOriginalUrl(shortCode, request);
        response.sendRedirect(originalUrl);
    }
    
    /**
     * 获取用户的短链接列表
     */
    @GetMapping("/list")
    public BaseResponse<List<ShortLinkVO>> listUserShortLinks(HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        List<ShortLinkVO> shortLinkVOList = shortLinkService.listUserShortLinks(loginUser.getId());
        return ResultUtils.success(shortLinkVOList);
    }
    
    /**
     * 删除短链接
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteShortLink(@RequestBody DeleteRequest deleteRequest, HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        boolean result = shortLinkService.deleteShortLink(deleteRequest.getId(), loginUser.getId());
        return ResultUtils.success(result);
    }
}
