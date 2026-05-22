package org.gms.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.gms.constants.api.ApiConstant;
import org.gms.model.dto.ResultBody;
import org.gms.model.dto.SubmitBody;
import org.gms.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 【控制器】AuthController（class），包 {@code org.gms.controller}。
 *
 * 处理用户认证相关操作，包括登录获取JWT令牌、登出及令牌刷新。
 * 所有API接口均以 {@code /auth} 为前缀，是后台管理系统的认证入口。
 *
 * @author 萧曵
 */
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Tag(name = "/auth/" + ApiConstant.LATEST)
    @Operation(summary = "登录")
    @PostMapping("/" + ApiConstant.LATEST + "/login")
    public ResultBody<Map<String, String>> login(@RequestBody SubmitBody<Map<String, String>> data) {
        return ResultBody.success(authService.getToken(data.getData().get("username"), data.getData().get("password")));
    }

    @Tag(name = "/auth/" + ApiConstant.LATEST)
    @Operation(summary = "登出")
    @DeleteMapping("/" + ApiConstant.LATEST + "/logout")
    public ResultBody<Object> logout() {
        return ResultBody.success();
    }

    @Tag(name = "/auth/" + ApiConstant.LATEST)
    @Operation(summary = "刷新token")
    @GetMapping("/" + ApiConstant.LATEST + "/refreshToken")
    public ResultBody<Map<String, String>> refreshToken(@RequestHeader("Authorization") String token) {
        return ResultBody.success(authService.refreshToken(token));
    }
}
