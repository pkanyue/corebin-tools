import cn.hutool.core.lang.Console;
import cn.hutool.core.thread.ThreadUtil;
import com.rlax.corebin.tools.callgraph.CallGraphGenerator;
import com.rlax.corebin.tools.callgraph.call.MethodCallInfo;
import org.apache.bcel.generic.Type;
import org.junit.jupiter.api.Test;

/**
 * @author Rlax
 * @date 2023/09/27
 */
public class CallGraphGeneratorTest {

    @Test
    void run() {
        CallGraphGenerator callGraphGenerator = new CallGraphGenerator();
        callGraphGenerator.init();

        MethodCallInfo methodCallInfo = MethodCallInfo.builder()
                .calledClassName("bean.TestBean")
                .calledMethodName("save2")
                .calledMethodArguments(Type.getTypes(new Class[]{String.class}))
                .build();

        callGraphGenerator.submitMethodCall(methodCallInfo);

        ThreadUtil.safeSleep(10000);
        Console.log(callGraphGenerator.getCallInfoList());
    }

}
