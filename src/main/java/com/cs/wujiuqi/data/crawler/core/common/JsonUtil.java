package com.cs.wujiuqi.data.crawler.core.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JsonUtil {
    /*
     * 根据key和josn字符串取出特定的value
     * 单字段测试需要耗时 1ms
     */
    public static String findJosnValue(String josn, String key) {
        String regex = "\"" + key + "\":(\"(.*?)\"|(\\d*))";
        Matcher matcher = Pattern.compile(regex).matcher(josn);
        String value = null;
        while (matcher.find()) {
            value = matcher.group();
            if (value != null) {
//                return value.split(":")[1].replaceAll("\"","");//如果value="2020-02-02 11:12:13"，则用:分割会有问题。所以采用以下方式
               return value.substring(value.indexOf(":")+1).replaceAll("\"","");
            }
        }
        return value;
    }
    /*
     * 根据key和josn字符串取出特定的value
     * 单字段测试需要耗时 1ms
     */
    public static List<String> findJosnValues(String josn, String key) {
        String regex = "\""+key+"\":(\"(.*?)\"|(\\d*))";
        Matcher matcher = Pattern.compile(regex).matcher(josn);
        List<String> values = new ArrayList<>();
        String value;
        while (matcher.find()) {
            value= matcher.group();
            if (value != null) {
                values.add(value.split(":")[1].replaceAll("\"",""));
            }
        }
        return values;


        /*
       String regex = "\"taskId\":\"(.*?)\"";//使用非贪婪模式
       Matcher matcher = Pattern.compile(regex).matcher(json);
        while (matcher.find()) {
            String ret = matcher.group(1);
            System.out.println(ret);
           *//* if("0".equals(ret)){
                json2dto = new ObjectMapper().readValue(jsonString, JstOrdersOutQueryDTO.class);
            }else{
                throw new Exception("没有成功得到json信息，请检查！！");
            }*//*
        }*/
    }

    /**
     * 根据key和josn字符串取出特定不重复的value
     * @param josn
     * @param key
     * @return
     * @remark
     *  josn字符串未经压缩过则可能出错
     */
    public static HashSet<String> findNoRepeatJosnValues(String josn, String key) {
        String regex = "\"" + key + "\":(\"(.*?)\"|(\\d*))";
        Matcher matcher = Pattern.compile(regex).matcher(josn);
        HashSet<String> values = new HashSet<>();
        String value;
        while (matcher.find()) {
            value= matcher.group();
            if (value != null) {
                values.add(value.split(":")[1].replaceAll("\"",""));
            }
        }
        System.out.println("key:"+key+",realjobs/page="+values.size());
        return values;
    }
}
