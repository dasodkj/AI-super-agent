package com.yupi.yuaiagent.app;

import com.yupi.yuaiagent.advisor.MyLoggerAdvisor;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.*;

import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.function.Consumer;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;


    private static final String SYSTEM_PROMPT =
                    "扮演精通茶叶知识与品鉴的专家。开场向用户表明身份，告知用户可咨询选茶、泡茶、品茶及茶叶保存等问题。" +
                    "围绕绿茶、红茶、乌龙茶、白茶、黄茶、黑茶六大茶类进行提问：选购时询问口味偏好、饮用场景及预算；" +
                    "冲泡时询问茶叶种类、茶具、水温、投茶量及冲泡时间；品鉴时询问茶汤香气、滋味、汤色及叶底表现；" +
                    "保存时询问包装方式、存放环境及保存期限。" +
                    "引导用户详述茶叶名称、产地、年份、外观和实际需求，以便提供专业且个性化的建议。";


    //初始化
    public LoveApp(ChatModel dashscopeChatModel) {
        // 1. 创建内存仓库（存储层）
        InMemoryChatMemoryRepository repository = new InMemoryChatMemoryRepository();

        // 2. 创建滑动窗口记忆管理者，设置最多保留20条消息
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(20)
                .build();

        // 3. 构建 ChatClient，使用新的 Advisor 构造方式
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        //检索内存并将其作为一组消息添加到提示中
                        MessageChatMemoryAdvisor.builder(chatMemory).build(),
                        //自定义Advisor，可按需开启
                        new MyLoggerAdvisor()

                        //自定义推理增强Advisor，可以开启，但是token会翻倍，因为同一个问题会询问两遍
                        //new ReReadingAdvisor()

                        //系统提供的日志Advisor
                        //new SimpleLoggerAdvisor()

                )
                .build();
    }

    //基础对话
    public String doChat(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param("max_history", 10))
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
        log.info("content: {}", content);
        return content;
    }

    record LoveReport(String title, List<String> suggestions) {
    }


    //加入生成报告功能
    public LoveReport doChatWithReport(String message, String chatId) {
        LoveReport loveReport = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成结果，标题为{用户名}的报告，内容为建议列表，每条占一行")
                .user(message)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, chatId)
                        .param("max_history", 10))
                .call()
                .entity(LoveReport.class);
        log.info("loveReport: {}", loveReport);
        return loveReport;
    }


    @Resource
    private VectorStore LoveAppVectorStore;

    //加入Rag功能
    public String doChatWithRag(String message, String chatId) {
        ChatResponse response = chatClient
                .prompt()
                .advisors(new Consumer<ChatClient.AdvisorSpec>() {
                    @Override
                    public void accept(ChatClient.AdvisorSpec advisorSpec) {
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, chatId)
                                .param("max_history", 10);
                    }
                })
                .advisors(QuestionAnswerAdvisor.builder(LoveAppVectorStore).build())
                .user(message)
                .call()
                .chatResponse();
        String content = response.getResult().getOutput().getText();
//        log.info("loveReport: {}", content);
        return content;
    }



}
