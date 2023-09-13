package com.rlax.corebin.tools;

import com.rlax.corebin.launch.listener.CorebinApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 服务启动类
 * @author Rlax
 *
 */
@SpringBootApplication
public class ToolsApplication {

    public static final String APPLICATION_NAME = "corebin-tools";

    public static void main(String[] args) {
        CorebinApplication.run(APPLICATION_NAME, ToolsApplication.class, args);
    }

}
