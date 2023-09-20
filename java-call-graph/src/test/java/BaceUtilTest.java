import bean.TestBean;
import cn.hutool.core.lang.Console;
import com.rlax.corebin.tools.callgraph.model.CallItem;
import com.rlax.corebin.tools.callgraph.util.BaceUtil;
import com.rlax.corebin.tools.callgraph.util.CallUtil;
import lombok.SneakyThrows;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Rlax
 * @date 2023/09/18
 */
public class BaceUtilTest {

    @SneakyThrows
    @Test
    void findMethodByReflectMethod() {
        String fullCallClassName = "bean.TestBean";
        JavaClass callerJavaClass = BaceUtil.loadClass(fullCallClassName);

        java.lang.reflect.Method m = TestBean.class.getDeclaredMethod("save", String.class);
        Method method = BaceUtil.findMethodByReflectMethod(callerJavaClass, m);
        Console.log(method);
    }

}
