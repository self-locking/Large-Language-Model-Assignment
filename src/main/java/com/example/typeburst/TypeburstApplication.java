package com.example.typeburst;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TypeburstApplication {
    public static void main(String[] args) {
        // 已移除代理设置，确保直接连接物理网络
        SpringApplication.run(TypeburstApplication.class, args);
    }
}