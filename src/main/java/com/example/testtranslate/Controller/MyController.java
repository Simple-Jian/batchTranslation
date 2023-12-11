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
                    if(originIsBlank||origin_en!=null) return;          //if origin is null or has been translated, break this cycle
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
}
