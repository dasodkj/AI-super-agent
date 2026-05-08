package com.yupi.yuaiagent.demo.invoke;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;

import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

/**
 * 用spring ai框架方式调用
 */
public class SpringAiAiInvoke {

    public static void main(String[] args) throws Exception {
// 创建模型实例
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(TestApiKey.apikey)
                .build();
        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .build();

// 创建 Agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .instruction("You are a helpful weather forecast assistant.")
                .build();

// 运行 Agent
//        AssistantMessage call = agent.call("我现在的地理位置是重庆市巴南区江南水岸，这的天气如何？");
        AssistantMessage call = agent.call("我上一次问的问题是什么，你重复一遍");
        System.out.println(call.getText());
    }
}