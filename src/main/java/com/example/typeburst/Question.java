package com.example.typeburst;

// 这个类代表一道题，Spring 会自动把它变成 JSON 发给前端
public class Question {
    private String content; // 气泡里显示的文字
    private String answer;  // 正确答案

    public Question() {}
    public Question(String content, String answer) {
        this.content = content;
        this.answer = answer;
    }

    // Getter 和 Setter (必须写，否则 Spring 找不到数据)
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }
}