package invoke;

import org.junit.jupiter.api.Test;

import java.lang.invoke.*;
import java.util.Arrays;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * <pre>
 * {@link LambdaMetafactory#metafactory(MethodHandles.Lookup, String, MethodType, MethodType, MethodHandle, MethodType)}
 *
 * 使用方法
 * 1. 调用方法获取函数对象工厂{@link CallSite}
 * 2. 调用{@link CallSite#dynamicInvoker()}获取工厂句柄
 * 3. 执行句柄得到函数对象
 *
 * 参数说明
 * {@code caller} {@link MethodHandles#lookup()}调用者
 * {@code interfaceMethodName} 函数对象接口方法名称
 * {@code factoryType} 函数对象工厂方法句柄类型（得到函数对象方法的句柄）
 * {@code interfaceMethodType} 函数对象接口方法的原始句柄类型（即泛型擦除后结果）
 * {@code implementation} 代理的方法实例方法句柄（必须为直接句柄，即不能由{@link MethodHandles}方法组合）
 * {@code dynamicMethodType} 代理的方法的句柄类型
 *
 *
 * {@link LambdaMetafactory#altMetafactory(MethodHandles.Lookup, String, MethodType, Object...)}
 * 比{@link LambdaMetafactory#metafactory(MethodHandles.Lookup, String, MethodType, MethodType, MethodHandle, MethodType)}更加灵活的使用
 *
 * 参数说明
 * 前6个参数与{@link LambdaMetafactory#metafactory(MethodHandles.Lookup, String, MethodType, MethodType, MethodHandle, MethodType)}相同（虽然后3个参数是可变参数，但实际上也是固定含义）
 * 第7个参数为flag标志，使用{@link LambdaMetafactory#FLAG_SERIALIZABLE}(可序列化)、{@link LambdaMetafactory#FLAG_MARKERS}(标记接口)、{@link LambdaMetafactory#FLAG_BRIDGES}(桥接方法)采用逻辑或运算得到
 * 如果包含{@link LambdaMetafactory#FLAG_MARKERS}标记，则下一个参数为标记接口数量N
 * 如果包含{@link LambdaMetafactory#FLAG_MARKERS}标记，则下N个参数为标记接口类型{@link Class}
 * 如果包含{@link LambdaMetafactory#FLAG_BRIDGES}标记，则下一个参数为桥接方法数量M
 * 如果包含{@link LambdaMetafactory#FLAG_BRIDGES}标记，则下M个参数为标记方法句柄类型{@link MethodType}
 * </pre>
 *
 */
public class LambdaMetafactoryTest {

    MethodHandles.Lookup lookup = MethodHandles.lookup();

    @Test
    public void staticRunTest() throws Throwable {
        java.lang.invoke.MethodHandle methodHandle = LambdaMetafactory.metafactory(
                lookup,
                "run",
                MethodType.methodType(Runnable.class),
                MethodType.methodType(void.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "staticRun", MethodType.methodType(void.class)),
                MethodType.methodType(void.class)
        ).dynamicInvoker();
        Runnable runnable = (Runnable) methodHandle.invokeExact();
        // [static]hello world!
        runnable.run();

        Runnable runnable2 = LambdaMetafactoryTest::staticRun;
        // [static]hello world!
        runnable2.run();
    }

    private static void staticRun() {
        System.out.println("[static]hello world!");
    }

    @Test
    public void runTest() throws Throwable {
        java.lang.invoke.MethodHandle methodHandle = LambdaMetafactory.metafactory(
                lookup,
                "run",
                MethodType.methodType(Runnable.class, LambdaMetafactoryTest.class),
                MethodType.methodType(void.class),
                lookup.findVirtual(LambdaMetafactoryTest.class, "run", MethodType.methodType(void.class)),
                MethodType.methodType(void.class)
        ).dynamicInvoker();
        Runnable runnable = (Runnable) methodHandle.invokeExact(this);
        // [instance]hello world
        runnable.run();

        Runnable runnable2 = this::run;
        // [instance]hello world
        runnable2.run();
    }

    private void run() {
        System.out.println("[instance]hello world");
    }

    @Test
    @SuppressWarnings("unchecked")
    public void retTest() throws Throwable {
        MethodHandle biFunction = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                MethodType.methodType(BiFunction.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "ret", MethodType.methodType(String.class, String.class, String.class)),
                MethodType.methodType(String.class, String.class, String.class)
        ).dynamicInvoker();
        MethodHandle function = LambdaMetafactory.metafactory(
                lookup,
                "apply",
                MethodType.methodType(Function.class, String.class),
                MethodType.methodType(Object.class, Object.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "ret", MethodType.methodType(String.class, String.class, String.class)),
                MethodType.methodType(String.class, String.class)
        ).dynamicInvoker();
        MethodHandle supplier = LambdaMetafactory.metafactory(
                lookup,
                "get",
                MethodType.methodType(Supplier.class, String.class, String.class),
                MethodType.methodType(Object.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "ret", MethodType.methodType(String.class, String.class, String.class)),
                MethodType.methodType(String.class)
        ).dynamicInvoker();

        // 以下结果相同
        System.out.println(ret("arg0", "arg1"));
        System.out.println(((BiFunction<String, String, String>) biFunction.invokeExact()).apply("arg0", "arg1"));
        System.out.println(((Function<String, String>) function.invokeExact("arg0")).apply("arg1"));
        System.out.println(((Supplier<String>) supplier.invokeExact("arg0", "arg1")).get());
    }

    public static String ret(String s, String s1) {
        return "ret2[" + s + "/" + s1 + "]";
    }

    @Test
    public void altTest() throws Throwable {
        // 基础使用
        MethodHandle base = LambdaMetafactory.altMetafactory(
                lookup,
                "apply",
                MethodType.methodType(BiFunction.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "ret", MethodType.methodType(String.class, String.class, String.class)),
                MethodType.methodType(String.class, String.class, String.class),
                0
        ).dynamicInvoker();
        BiFunction<String, String, String> baseFunction = (BiFunction<String, String, String>) base.invokeExact();
        System.out.println(baseFunction.apply("arg0", "arg1"));

        // 添加实现接口
        // 该接口必须是无未实现方法的接口，即用于标记的接口
        MethodHandle mark = LambdaMetafactory.altMetafactory(
                lookup,
                "apply",
                MethodType.methodType(BiFunction.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "ret", MethodType.methodType(String.class, String.class, String.class)),
                MethodType.methodType(String.class, String.class, String.class),
                LambdaMetafactory.FLAG_MARKERS,
                2,
                MyMark1.class, MyMark2.class
        ).dynamicInvoker();
        BiFunction<String, String, String> markFunction = (BiFunction<String, String, String>) mark.invokeExact();
        // [interface java.util.function.BiFunction, interface invoke.LambdaMetafactoryTest$MyMark1, interface invoke.LambdaMetafactoryTest$MyMark2]
        System.out.println(Arrays.stream(markFunction.getClass().getInterfaces()).toList());
        System.out.println(markFunction.apply("arg0", "arg1"));
        // mark1
        System.out.println(((MyMark1) markFunction).mark1());
        // mark2
        System.out.println(((MyMark2) markFunction).mark2());

        // 添加桥接方法
        // 该方法会调用原方法
        MethodHandle bridges = LambdaMetafactory.altMetafactory(
                lookup,
                "apply",
                MethodType.methodType(BiFunction.class),
                MethodType.methodType(Object.class, Object.class, Object.class),
                lookup.findStatic(LambdaMetafactoryTest.class, "ret", MethodType.methodType(String.class, String.class, String.class)),
                MethodType.methodType(String.class, String.class, String.class),
                LambdaMetafactory.FLAG_BRIDGES,
                1,
                MethodType.methodType(String.class, String.class, String.class)
        ).dynamicInvoker();
        BiFunction<String, String, String> bridgesFunction = (BiFunction<String, String, String>) bridges.invokeExact();
        // [
        // public java.lang.Object invoke.LambdaMetafactoryTest$$Lambda/0x000001cf6510f9c0.apply(java.lang.Object,java.lang.Object),
        // public java.lang.String invoke.LambdaMetafactoryTest$$Lambda/0x000001cf6510f9c0.apply(java.lang.String,java.lang.String),
        // ...省略其他方法
        // ]
        System.out.println(Arrays.stream(bridgesFunction.getClass().getMethods()).toList());
        try {
            System.out.println(baseFunction.getClass().getMethod("apply", String.class, String.class));
        } catch (NoSuchMethodException e) {
            System.out.println("base no find");
        }
        System.out.println(bridgesFunction.getClass().getMethod("apply", String.class, String.class));
    }

    public interface MyMark1 {
        default String mark1() {
            return "mark1";
        }
    }

    public interface MyMark2 {
        default String mark2() {
            return "mark2";
        }
    }

}
