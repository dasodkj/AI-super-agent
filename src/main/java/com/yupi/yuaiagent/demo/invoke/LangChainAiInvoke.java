package com.yupi.yuaiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.chat.ChatModel;

/**
 * 用lanchain4j 框架方式调用
 */
public class LangChainAiInvoke {
    public static void main(String[] args) {
        ChatModel qwenModel = QwenChatModel.builder()
                .apiKey(TestApiKey.apikey)
                .modelName("qwen-max")
                .build();
        String chat = qwenModel.chat("咕咕嘎嘎！！你是臭企鹅");
        System.out.println(chat);
    }
}
