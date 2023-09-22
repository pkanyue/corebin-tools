package bean;

import cn.hutool.core.lang.Console;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * @author Rlax
 * @date 2023/09/07
 */
public class TestBean {

    public void save(String ps1) throws Exception {
        String tmp = StrUtil.trim(ps1);
        tmp = StrUtil.format("{}", tmp);
        ms1();
        m1();
    }

    public static void ms1() {

    }

    public void m1() {

    }

    public void callable(String var) throws Exception {
        int i = RandomUtil.randomInt();
        String call = ((Callable<String>) () -> var).call();
        ((Runnable) () -> {
            Console.log(var, call);
            ms1();
        }).run();
        new Runnable() {
            @Override
            public void run() {
                Console.log(var, call, call);
            }
        }.run();
        List<String> languages = Arrays.asList("java","scala","python");
        languages.forEach(Console::log);
    }

}
