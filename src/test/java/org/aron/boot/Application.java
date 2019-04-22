package org.aron.boot;

import com.alibaba.fastjson.JSON;
import org.aron.boot.annotation.FlyBootApplication;
import org.aron.commons.utils.Utils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author: Y-Aron
 * @create: 2019-01-21 17:36
 **/
@FlyBootApplication
public class Application {

    public static void main(String[] args) {
        FlyApplication.run(Application.class, args);
    }

    @Test
    public void test() {
        System.out.println(JSON.toJSONString("asdsad"));
        System.exit(0);
        String path = "/test/{ccc}{c}}/{bbb}";
        System.out.println(Utils.matchUrl(path, true));
//        System.out.println(Utils.matchUrl(path, true));
        String url = "/test/21331241244214/123";
        String[] array = path.split("/");
        Map<String, Integer> map = new HashMap<>(0);
        Pattern compile = Pattern.compile("\\{\\w+}");
        for (int i = 0; i < array.length; i++) {

//            int count = 0;
//            Matcher matcher = compile.matcher(array[i]);
//            while (matcher.find()) {
//                count ++;
//                map.put(matcher.group(), i);
//            }
//            if (count >= 2) {
////                System.out.println("no");
//            }
        }
        map.forEach((k, v) -> {
            System.out.println(k + " " + v);
        });
    }
}
