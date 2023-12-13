package com.example.testtranslate;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SpringBootTest
class TestTranslateApplicationTests {

//    @Test
    void contextLoads() {
                // 测试字符串
                String testString = "{\"url\":\"\",\"type\":\"\",\"inputs\":[{\"name\":\"cube\",\"type\":\"Cube\",\"description\":\"要可视化的Cube\"},{\"name\":\"products\",\"type\":\"List<String>\",\"description\":\"要可视化的产品列表\"},{\"name\":\"bands\",\"type\":\"List<String>\",\"description\":\"要映射到 RGB 的三个波段名称的逗号分隔列表\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"gain\",\"type\":\"Number/List<Number>\",\"description\":\"与每个像素值相乘的值,单个值或三个值的列表，每个波段一个\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"bias\",\"type\":\"Number/List<Number>\",\"description\":\"每个Dn值都相加的值,单个值或三个值的列表，每个波段一个\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"min\",\"type\":\"Number/List<Number>\",\"description\":\"映射到0的值,单个值或三个值的列表，每个波段一个\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"max\",\"type\":\"Number/List<Number>\",\"description\":\"映射到255的值,单个值或三个值的列表，每个波段一个\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"gamma\",\"type\":\"Number/List<Number>\",\"description\":\"Gamma校正系数,单个值或三个值的列表，每个波段一个\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"opacity\",\"type\":\"Float\",\"description\":\"图层的不透明度(0.0 为完全透明, 1.0 为完全不透明)\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"palette\",\"type\":\"List<String>\",\"description\":\"CSS 样式颜色字符串列表（仅限单波段图像）\",\"default\":\"None\",\"optional\":\"True\"},{\"name\":\"format\",\"type\":\"String\",\"description\":\"\",\"default\":\"png\",\"optional\":\"True\"}],\"outputs\":[{\"name\":\"Cube\",\"type\":\"Cube\",\"description\":\"可视化结果\"}]}";

                // 定义匹配连续中文字符的正则表达式
                String regex ="\"[^\"]*[\u4e00-\u9fa5]+[^\"]*\"";


        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        // 创建 Matcher 对象
        Matcher matcher = pattern.matcher(testString);

        // 循环匹配并替换每一处连续的中文字符
        StringBuffer resultBuffer = new StringBuffer();
        int replacementCount = 1;

        while (matcher.find()) {
            // 获取匹配到的中文字符
            String chineseMatch = matcher.group();

            // 根据匹配到的中文字符进行不同的替换
            String replacementText = "替换文本" + replacementCount;

            // 替换并追加到结果字符串
            matcher.appendReplacement(resultBuffer, replacementText);

            // 更新替换计数
            replacementCount++;
        }

        // 将剩余的部分追加到结果字符串
        matcher.appendTail(resultBuffer);

        // 输出结果
        System.out.println("替换前：" + testString);
        System.out.println("替换后：" + resultBuffer.toString());
            }

}
