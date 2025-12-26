package com.example.typeburst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class AiQuestionService {

    private final String API_KEY = "sk-hbnrcukflzqljmjhgcbwzlzpexkowgielzalsoouxbexxfcm";
    private final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";

    public Question generateQuestion(String category, String difficulty) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String taskDescription = switch (category) {
                case "单词手速" -> "随机1个中长英语单词(6-12字母)。content和answer完全一致。";
                case "中文手速" -> "随机1个中文短句(5-10字)。content和answer完全一致。";
                case "趣味数学" -> "随机1道100以内加减法算式。content为算式(如12+34=??)，answer为结果数字。";
                default -> "随机一个常用成语。";
            };

            String prompt = String.format(
                    "任务：%s。要求：1.严格只返回JSON。2.禁止Markdown。格式：{\"content\":\"...\",\"answer\":\"...\"}",
                    taskDescription
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-ai/DeepSeek-V3");
            requestBody.put("messages", new Object[]{
                    new HashMap<String, String>() {{ put("role", "user"); put("content", prompt); }}
            });
            requestBody.put("response_format", new HashMap<String, String>() {{ put("type", "json_object"); }});

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            JsonNode root = mapper.readTree(response.getBody());
            String jsonContent = root.path("choices").get(0).path("message").path("content").asText();

            return mapper.readValue(jsonContent, Question.class);

        } catch (Exception e) {
            // 本地保底题库
            return switch (category) {
                case "趣味数学" -> new Question("55 + 44 = ??", "99");
                case "单词手速" -> new Question("Excellent", "Excellent");
                default -> new Question("路漫漫其修远兮", "路漫漫其修远兮");
            };
        }
    }
}