package com.example.typeburst;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class GameController {

    // 自动连接我们刚才写的 AI 服务
    @Autowired
    private AiQuestionService aiQuestionService;

    // 负责显示主页面
    @GetMapping("/")
    public String index() {
        return "index";
    }

    // 负责给前端提供题目接口
    @GetMapping("/api/question")
    @ResponseBody
    public Question getQuestion(
            @RequestParam(defaultValue = "成语") String type,
            @RequestParam(defaultValue = "简单") String diff) {

        // 调用 AI 服务生成题目并返回
        return aiQuestionService.generateQuestion(type, diff);
    }
}