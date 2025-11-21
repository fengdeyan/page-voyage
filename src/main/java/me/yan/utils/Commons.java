package me.yan.utils;

import io.micrometer.common.util.StringUtils;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class Commons {
    /**
     * 获取网站标题
     *
     * @return
     */
    public  String site_title() {
        return site_option("site_title");
    }
    /**
     * 获取google网站验证码
     *
     * @return
     */
    public  String google_site_verification() {
        return site_option("google_site_verification");
    }
    /**
     * 获取百度网站验证码
     *
     * @return
     */
    public  String baidu_site_verification() {
        return site_option("baidu_site_verification");
    }
    /**
     * 网站配置项
     *
     * @param key
     * @return
     */
    public  String site_option(String key) {
        return site_option(key, "");
    }
    /**
     * 获取网站的备案信息
     *
     * @return
     */
    public  String site_record() {
        return site_option("site_record");
    }
    /**
     * 网站配置项
     *
     * @param key
     * @param defalutValue 默认值
     * @return
     */
    public  String site_option(String key, String defalutValue) {
        System.out.println("key->"+key);
        if (StringUtils.isBlank(key)) {
            return "";
        }
        String str = "";
        System.out.println(key+"->"+str);
        if (StringUtils.isNotBlank(str)) {
            return str;
        } else {
            return defalutValue;
        }
    }
    /**
     * 网站链接
     *
     * @return
     */
    public String site_url() {
        return site_url("/");
    }
    /**
     * 返回网站链接下的全址
     *
     * @param sub 后面追加的地址
     * @return
     */
    public  String site_url(String sub) {
        return site_option("site_url") + sub;
    }
    /**
     * 获取GitHub地址
     *
     * @return
     */
    public  String social_github() {
        return site_option("social_github");
    }

    /**
     * 返回作品文章地址
     *
     * @param cid
     * @return
     */
    public  String photoPermalink(Integer cid) {
        return site_url("/article/" + cid.toString());
    }


    /**
     * 返回blog文章地址
     *
     * @param cid
     * @return
     */
    public String blogPermalink(Integer cid) {
        return site_url("/article/" + cid.toString());
    }

    /**
     * 获取随机数
     *
     * @param max
     * @param str
     * @return
     */
    public static String random(int max, String str) {
        return UUID.random(1, max) + str;
    }
    /**
     * 返回github头像地址
     *
     * @para
     * @return
     */
    public static String gravatar() {
        String avatarUrl = "https://github.com/identicons/";

        return avatarUrl  + "afe94ddc67b57b89f353a02e1ffe3ede.png";
    }
    /**
     * 判断分页中是否有数据
     *
     * @param
     * @return
     */
    public static boolean is_empty(List list) {
        return list == null || (list.size() == 0);
    }
    /**
     * 截取字符串
     *
     * @param str
     * @param len
     * @return
     */
    public static String substr(String str, int len) {
        if (str.length() > len) {
            return str.substring(0, len);
        }
        return str;
    }
}
