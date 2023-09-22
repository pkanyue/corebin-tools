import cn.hutool.core.lang.Console;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.junit.jupiter.api.Test;

/**
 * JavassisTest
 *
 * @author Rlax
 * @date 2023/09/22
 */
public class JavassisTest {

    @Test
    void loadClassMethod() throws NotFoundException {
        ClassPool pool = ClassPool.getDefault();
        CtClass ctClass = pool.get("bean.TestBean");

        Console.log(ctClass.getName());
        CtMethod ctMethod = ctClass.getMethod("callable", "");
        Console.log(ctMethod.getName());
        for (CtMethod method : ctClass.getMethods()) {
            Console.log(method.getName());
            Console.log(method.getParameterTypes());
            Console.log(method.getReturnType());
        }
    }

}
