package com.example.testtranslate.Controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.testtranslate.Dao.TestMapper;
import com.example.testtranslate.utils.TransApi;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对需要翻译的字段，先在数据库中指定一个除名字为"原字段名_en"以外其它完全相同的字段
 * 配置数据库和百度API key
 * 启动这个应用
 * get请求需要两个参数，一个是表名tName，一个是需要翻译的字段名fieldName
 *
 */
@RestController
@Slf4j
public class MyController {
    @Autowired
    private TestMapper testMapper;
    //Baidu translator API
    private static final String APP_ID = "";
    private static final String SECURITY_KEY = "";

    @GetMapping("/testTranslate")
    public List<JSONObject> test(String tName,String fieldName){
        //tName是表名，fieldName是字段名
        List<JSONObject> test = testMapper.getTest(tName);  //get all the fields content in the specified sheet in the database
        test.forEach(
                t->{
                    String origin = t.getString(fieldName);  //get the field content
                    System.out.println(origin);
                    String origin_en=t.getString(fieldName+"_en");   //field content translated

                    boolean originIsBlank=origin==null?true:origin.isBlank();
                    boolean originEnIsBlank=origin_en==null?true:origin_en.isBlank();
                    if(originIsBlank||!originEnIsBlank) return;          //if origin is null or has been translated, break this cycle
                    TransApi transApi=new TransApi(APP_ID,SECURITY_KEY);   //translate
                    String result="";
                    //If the request fails, try again until success.
                    while (!result.contains("trans_result")){
                        result = transApi.getTransResult(origin, "zh", "en");
                        System.out.println("try translate..."+origin);
                    }
                    //parse the translated result
                    JSONObject jsonObject = JSONObject.parseObject(result);
                    JSONArray temp1 = jsonObject.getJSONArray("trans_result");
                    JSONObject temp2 = temp1.getJSONObject(0);
                    String translatedResult=temp2.getString("dst");
                    System.out.println(translatedResult);
                    String id = t.getString("id");

                    //update the field content with translated result
                    testMapper.updateField(tName,fieldName+"_en",translatedResult,Integer.parseInt(id));
                }
        );
       return test;
    }

    /**
     * 用于翻译Oge_model_resource中的paramJson字段
     * @return
     */
    @GetMapping("/translateParamJson")
    public List<JSONObject> translateParamJson(){
        String tName="oge_model_resource_param";
        String fieldName="paramjson";
        // 定义匹配连续中文字符的正则表达式
        String regex = "\"[^\"]*[\u4e00-\u9fa5]+[^\"]*\"";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);

        //tName是表名，fieldName是字段名
        List<JSONObject> test = testMapper.getTest(tName);  //get all the fields content in the specified sheet in the database
        test.forEach(
                t->{
                    String origin = t.getString(fieldName);  //get the field content
//                    System.out.println(origin);
                    String origin_en=t.getString(fieldName+"_en");   //field content translated

                    boolean originIsBlank=origin==null?true:origin.isBlank();
//                    boolean originEnIsBlank=origin_en==null?true:origin_en.isBlank();
                    if(originIsBlank) return;          //if origin is null or has been translated, break this cycle

                    TransApi transApi=new TransApi(APP_ID,SECURITY_KEY);   //translate
                    //If the json contains chinese, translate it
                    // 创建 Matcher 对象
                    Matcher matcher = pattern.matcher(origin);
                    // 循环匹配并替换每一处连续的中文字符
                    StringBuffer resultBuffer = new StringBuffer();
                    System.out.println("这个项目开始了："+t.getString("id"));
                    while (matcher.find()) {
                        // 获取匹配到的中文字符
                        String chineseMatch = matcher.group();
                        // 根据匹配到的中文字符进行不同的替换
                        String result="";
                        //If the request fails, try again until success.
                        while (!result.contains("trans_result")){
                            result = transApi.getTransResult(chineseMatch, "zh", "en");
                            System.out.println("try translate items in paramJson..."+chineseMatch);
                        }
                        //parse the translated result
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        JSONArray temp1 = jsonObject.getJSONArray("trans_result");
                        JSONObject temp2 = temp1.getJSONObject(0);
                        String translatedResult=temp2.getString("dst");
                        System.out.println(translatedResult);
                        // 替换并追加到结果字符串
                        matcher.appendReplacement(resultBuffer, translatedResult);
                    }
                    if(!resultBuffer.isEmpty()) {
                        // 将剩余的部分追加到结果字符串
                        matcher.appendTail(resultBuffer);
                        origin=resultBuffer.toString();
                    }

                    String id = t.getString("id");
                    //update the field content with translated result
                    testMapper.updateField(tName,fieldName+"_en",origin,Integer.parseInt(id));}
        );
        return test;
}
    @GetMapping("/translateScenes")
    public List<JSONObject> translateScenes(String tName){
        //tName是表名，fieldName是字段名
        List<JSONObject> test = testMapper.getTest(tName);  //get all the fields content in the specified sheet in the database
        test.forEach(
                t->{

                    String input =  t.getString("image_amount");;
                    String id = t.getString("id");
                    boolean inputIsBlank=input==null?true:input.isBlank();
                    if(inputIsBlank) {
                        System.out.println("input is blank!");
                        return;
                    };
                    // 定义正则表达式，匹配数字后面紧跟着一个汉字的模式
                    String pattern = "(\\d+)景";

                    // 编译正则表达式
                    Pattern regex = Pattern.compile(pattern);

                    // 创建 Matcher 对象
                    Matcher matcher = regex.matcher(input);

                    // 查找匹配
                    if (matcher.find()) {
                        // 提取匹配到的数字部分
                        String number = matcher.group(1);
                        System.out.println("提取到的数字是: " + number);
                        //update the field content with translated result
                        testMapper.updateField(tName,"image_amount_en",number,Integer.parseInt(id));
                    } else {
                        System.out.println("未找到匹配的模式");
                    }
                }
        );
        return test;
    }
}
