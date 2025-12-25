package com.example.typeburst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

@Service
public class AiQuestionService {

    // 【关键】填入你申请的 API Key
    private final String API_KEY = "sk-hbnrcukflzqljmjhgcbwzlzpexkowgielzalsoouxbexxfcm";
    private final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";

    public Question generateQuestion(String category, String difficulty) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String prompt = String.format(
                    "你是一个游戏出题助手。请生成1道关于'%s'的题目，难度为'%s'。" +
                            "必须只返回一个JSON对象，格式为：{\"content\":\"题目内容\", \"answer\":\"答案\"}。" +
                            "成语填空请用??代替，如'画蛇添??'。不要解释，不要输出Markdown代码块。",
                    category, difficulty
            );

            // --- 修改点：使用 Map 构造请求体，让 Jackson 自动转 JSON，避免格式错误 ---
            java.util.Map<String, Object> requestBody = new java.util.HashMap<>();
            requestBody.put("model", "deepseek-ai/DeepSeek-V3");
            requestBody.put("messages", new Object[]{
                    new java.util.HashMap<String, String>() {{
                        put("role", "user");
                        put("content", prompt);
                    }}
            });
            // 强制要求返回 JSON 格式
            requestBody.put("response_format", new java.util.HashMap<String, String>() {{
                put("type", "json_object");
            }});

            String body = mapper.writeValueAsString(requestBody); // 自动生成标准的 JSON 字符串

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            // 解析逻辑保持不变
            JsonNode root = mapper.readTree(response.getBody());
            String contentJsonString = root.path("choices").get(0).path("message").path("content").asText();

            return mapper.readValue(contentJsonString, Question.class);

        } catch (Exception e) {
            e.printStackTrace(); // 依然保留，方便观察
            return new Question("系统忙，请稍后再试", "1");
        }
    }
}