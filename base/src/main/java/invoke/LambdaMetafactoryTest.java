package invoke;

import org.junit.jupiter.api.Test;

import java.lang.invoke.*;

/**
 * Xxx1和Xxx2结果相同
 */
public class LambdaMetafactoryTest {

    @Test
    public void staticRun1() {
        Runnable runnable = LambdaMetafactoryTest::staticRun;

        // [static]hello world!
        runnable.run();
    }

    @Test
    public void staticRun2() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                "run",
                MethodType.methodType(Runnable.class),
                MethodType.methodType(void.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "staticRun", MethodType.methodType(void.class)),
                MethodType.methodType(void.class)
        );
        Runnable runnable = (Runnable) callSite.dynamicInvoker().invokeExact();

        // [static]hello world!
        runnable.run();
    }

    private static void staticRun() {
        System.out.println("[static]hello world!");
    }

    @Test
    public void run1() {
        Runnable runnable = this::run;

        // [instance]hello world
        runnable.run();
    }

    @Test
    public void run2() throws Throwable {
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        CallSite callSite = LambdaMetafactory.metafactory(
                lookup,
                "run",
                MethodType.methodType(Runnable.class, LambdaMetafactoryTest.class),
                MethodType.methodType(void.class),
                lookup.findVirtual(LambdaMetafactoryTest.class, "run", MethodType.methodType(void.class)),
                MethodType.methodType(void.class)
        );
        Runnable runnable = (Runnable) callSite.dynamicInvoker().invokeExact(this);

        // [instance]hello world
        runnable.run();
    }

    private void run() {
        System.out.println("[instance]hello world");
    }

}
