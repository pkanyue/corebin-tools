import cn.hutool.core.lang.Console;
import cn.hutool.json.JSONUtil;
import com.rlax.corebin.tools.callgraph.model.CallItem;
import com.rlax.corebin.tools.callgraph.util.CallUtil;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * @author Rlax
 * @date 2023/09/13
 */
public class CallUtilTest {

    @Test
    void generateModel() {
        List<CallItem> list = CallUtil.generateModel("bean.TestBean", "public void save(String ps1)");
        Console.log(JSONUtil.toJsonPrettyStr(list));
    }

    @Test
    void test() {
        List<CallItem> list = CallUtil.generateModel("bean.TestBean", "callable", String.class);
        Console.log(JSONUtil.toJsonPrettyStr(list));
    }

}
