package com.yupi.yuaiagent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 示例 Controller，演示 Knife4j (OpenAPI 3) 注解的使用
 */
@Tag(name = "示例接口", description = "用于演示 Knife4j 文档生成的接口")
@RestController
@RequestMapping("/api/example")
public class BodyController {

    @Operation(summary = "打招呼", description = "根据传入的名称返回问候语")
    @GetMapping("/hello")
    public String sayHello(@Parameter(description = "用户名", example = "World")
                           @RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    @Operation(summary = "获取用户信息", description = "返回模拟的用户数据")
    @GetMapping("/user/{id}")
    public Map<String, Object> getUser(@Parameter(description = "用户ID", required = true, example = "1001")
                                       @PathVariable Long id) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", id);
        user.put("name", "张三");
        user.put("email", "zhangsan@example.com");
        return user;
    }

    @Operation(summary = "创建数据", description = "模拟 POST 请求创建资源")
    @PostMapping("/data")
    public String createData(@RequestBody Map<String, Object> data) {
        return "收到数据: " + data;
    }
}