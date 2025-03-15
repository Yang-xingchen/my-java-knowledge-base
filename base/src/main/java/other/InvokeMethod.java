package other;

import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.function.Consumer;

public class InvokeMethod {

    private static final InvokeMethod INSTANCE = new InvokeMethod();
    public static final Class<? extends InvokeMethod> CLS = InvokeMethod.class;

    /**
     * 直接调用
     */
    @Test
    public void byInvoke() {
        sayByStatic();
        INSTANCE.sayByInstance();
    }

    /**
     * Lambda / 方法引用
     * 局限性较大
     */
    @Test
    public void byLambda() {
        Runnable staticRunnable = InvokeMethod::sayByStatic;
        Consumer<InvokeMethod> instanceConsumer = InvokeMethod::sayByInstance;
        Runnable instanceRunnable = INSTANCE::sayByInstance;

        staticRunnable.run();
        instanceConsumer.accept(INSTANCE);
        instanceRunnable.run();
    }

    /**
     * 反射
     */
    @Test
    public void byReflect() throws Exception {
        Method staticMethod = CLS.getDeclaredMethod("sayByStatic");
        staticMethod.setAccessible(true);
        Method instanceMethod = CLS.getDeclaredMethod("sayByInstance");
        instanceMethod.setAccessible(true);

        staticMethod.invoke(null);
        staticMethod.invoke(INSTANCE);
    }

    /**
     * 方法句柄
     */
    @Test
    public void byMethodHandle() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle staticHandle = lookup.findStatic(CLS, "sayByStatic", MethodType.methodType(void.class));
        MethodHandle instanceHandle = lookup.findVirtual(CLS, "sayByInstance", MethodType.methodType(void.class));

        staticHandle.invoke();
        instanceHandle.invoke(INSTANCE);
    }

    private static void sayByStatic() {
        System.out.println("[static]hello world!");
    }

    private void sayByInstance() {
        System.out.println("[instance]hello world!");
    }

}
