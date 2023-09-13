package com.rlax.corebin.tools.callgraph;

import cn.hutool.core.util.StrUtil;

/**
 * @author Rlax
 * @date 2023/09/07
 */
public class TestBean {

    public void save(String ps1) {
        String tmp = StrUtil.trim(ps1);
        tmp = StrUtil.format("{}", tmp);
        ms1();
        m1();
    }

    public static void ms1() {

    }

    public void m1() {

    }

}
