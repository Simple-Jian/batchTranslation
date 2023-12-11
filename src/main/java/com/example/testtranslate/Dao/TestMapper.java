package com.example.testtranslate.Dao;

import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Transactional
@Mapper
public interface TestMapper {
    List<JSONObject> getTest(String tName);

    void updateField(String tName,String field,String newStr,Integer id);

}
