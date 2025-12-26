package com.example.typeburst;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class AiQuestionService {

    private final String API_KEY = "sk-hbnrcukflzqljmjhgcbwzlzpexkowgielzalsoouxbexxfcm"; //
    private final String API_URL = "https://api.siliconflow.cn/v1/chat/completions";

    public Question generateQuestion(String category, String difficulty) {
        RestTemplate restTemplate = new RestTemplate();
        ObjectMapper mapper = new ObjectMapper();

        try {
            String taskDescription = "";
            // 根据不同模式定制任务
            if ("单词手速".equals(category)) {
                taskDescription = "随机给出1个常用英语单词(5-12字母)或短相。content和answer必须完全一致。";
            } else if ("中文手速".equals(category)) {
                taskDescription = "随机给出1个有趣的中文短句(4-12字)。content和answer必须完全一致。";
            } else if ("趣味数学".equals(category)) {
                taskDescription = "随机给出1道基础加减乘除算术题。content为算式(如12+34=??)，answer为计算结果数字。";
            }

            String prompt = String.format(
                    "你是一个游戏出题助手。任务：%s。要求：1.只返回JSON。2.不要解释。3.难度%s。" +
                            "格式：{\"content\":\"题目内容\", \"answer\":\"答案\"}",
                    taskDescription, difficulty
            );

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-ai/DeepSeek-V3"); //
            requestBody.put("messages", new Object[]{
                    new HashMap<String, String>() {{ put("role", "user"); put("content", prompt); }}
            });
            requestBody.put("response_format", new HashMap<String, String>() {{ put("type", "json_object"); }}); //

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(API_KEY);

            HttpEntity<String> entity = new HttpEntity<>(mapper.writeValueAsString(requestBody), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(API_URL, entity, String.class);

            JsonNode root = mapper.readTree(response.getBody());
            String contentJsonString = root.path("choices").get(0).path("message").path("content").asText();

            return mapper.readValue(contentJsonString, Question.class);

        } catch (Exception e) {
            // 保底逻辑
            if ("趣味数学".equals(category)) return new Question("25 + 15 = ??", "40");
            if ("单词手速".equals(category)) return new Question("Challenge", "Challenge");
            return new Question("欲穷千里目", "欲穷千里目");
        }
    }
}