package com.rlax.corebin.tools.callgraph;

import cn.hutool.core.util.StrUtil;

/**
 * @author Rlax
 * @date 2023/09/07
 */
public class TestBean2 {

    public static String S1 = "s1";
    private String ps1;

    public void save(String ps1) {
        String tmp = StrUtil.trim(ps1);
        tmp = StrUtil.format("{}", tmp);
        getPs1();
        this.ps1 = tmp;
    }

    public static String getS1() {
        return S1;
    }

    public String getPs1() {
        return ps1 + getS1();
    }
}
