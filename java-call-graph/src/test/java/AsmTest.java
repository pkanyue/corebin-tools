import cn.hutool.core.lang.Console;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.objectweb.asm.ClassReader;

/**
 * @author Rlax
 * @date 2023/09/18
 */
public class AsmTest {

    @SneakyThrows
    @Test
    void tt() {
        String fullCallClassName = "bean.TestBean";
        ClassReader classReader = new ClassReader(fullCallClassName);
        Console.log(classReader);
    }

}
