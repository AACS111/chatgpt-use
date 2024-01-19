package com.zheng.chat.test;

//import com.zheng.chat.utli.CustomChatGpt;
import com.zheng.chat.util.CustomChatGpt1;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.util.Scanner;

/**
 * @author 郑福平2403
 * @version 1.0
 * @description ----测试---
 * @date 2024/1/3 20:30:43
 */
public class Test {
    public static void main(String[] args) throws IOException {
        String api = "/api/files/1";
//        String url = String.format("%s%s", BASE_URL, api);
//        HttpGet httpGet = new HttpGet(url);
        CloseableHttpClient httpClient = HttpClients.createDefault();

//        CloseableHttpResponse response = httpClient.execute(httpGet);

//        test2();
        test1();
    }

    private static void test2() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        httpClient.execute()
        String apiKey = "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";
        CustomChatGpt customChatGpt = new CustomChatGpt(apiKey);
        // 根据自己的网络设置吧
        customChatGpt.setResponseTimeout(20000);
        while (true) {
            System.out.print("\n请输入问题(q退出)：");
            String question = new Scanner(System.in).nextLine();
            if ("q".equals(question)) break;
            long start = System.currentTimeMillis();
            String answer = customChatGpt.getAnswer(httpClient, question);
            long end = System.currentTimeMillis();
            System.out.println("该回答花费时间为：" + (end - start) / 1000.0 + "秒");
            System.out.println(answer);
        }
        httpClient.close();

    }


    private static void test1() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
//        String apiKey = ""
        CustomChatGpt1 customChatGpt = new CustomChatGpt1();

        while (true){
            System.out.print("\n请输入问题(q退出)：");
            String question = new Scanner(System.in).nextLine();
            if ("q".equals(question))break;
            long start = System.currentTimeMillis();
            String answer = customChatGpt.getAnswer(httpClient, question);
            long end = System.currentTimeMillis();
            System.out.println("该回答花费时间为：" + (end - start) / 1000.0 + "秒");
            System.out.println(answer);
        }
        httpClient.close();
    }

}
