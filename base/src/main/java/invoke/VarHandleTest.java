package invoke;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;

/**
 * NOTE: 使用了静态变量，请每次单测试执行
 */
public class VarHandleTest {

    public static int psi = 42;
    public int pi = 42;

    @Test
    public void get() throws Throwable {
        // getXxx: 获取字段值
        // get(): 只获取, 可能指令重排
        // getOpaque(): 获取, 不会指令重排, 不适用内存屏障
        // getAcquire(): 获取, 不会使执行指令后重排到执行前
        // getVolatile(): 获取, 不会指令重排, 使用内存屏障
        VarHandle psiHandle = MethodHandles.lookup().findStaticVarHandle(VarHandleTest.class, "psi", int.class);
        VarHandle piHandle = MethodHandles.lookup().findVarHandle(VarHandleTest.class, "pi", int.class);
        // 静态字段无需参数
        Assertions.assertEquals(42, psiHandle.get());
        // 实例字段需要一个参数: 实例
        Assertions.assertEquals(42, piHandle.get(new VarHandleTest()));
    }
    
    @Test
    public void set() throws Throwable {
        // setXxx: 设置字段值
        // set(): 只设置, 可能指令重排
        // setOpaque(): 设置, 不会指令重排, 不适用内存屏障
        // setRelease(): 设置, 不会使执行指令前重排到执行后
        // setVolatile(): 设置, 不会指令重排, 使用内存屏障
        VarHandle psiHandle = MethodHandles.lookup().findStaticVarHandle(VarHandleTest.class, "psi", int.class);
        VarHandle piHandle = MethodHandles.lookup().findVarHandle(VarHandleTest.class, "pi", int.class);
        VarHandleTest obj = new VarHandleTest();
        // 静态字段需要一个参数: 值
        psiHandle.set(43);
        // 实例字段需要两个参数: 实例、值
        piHandle.set(obj, 43);
        Assertions.assertEquals(43, psiHandle.get());
        Assertions.assertEquals(43, piHandle.get(obj));
    }

    @Test
    public void compareAndSet() throws Throwable {
        // if (get() == arg[0]) {
        //     set(arg[1]);
        //     return true;
        // }
        // return false;
        // compareAndSet: CAS, 设置值时不会失败, 不会指令重排, 使用内存屏障
        // weakCompareAndSetPlain: CAS, 设置值时可能失败, 可能指令重排
        // weakCompareAndSetAcquire: CAS, 设置值时可能失败, 不会使执行后指令重排到执行前
        // weakCompareAndSetRelease: CAS, 设置值时可能失败, 不会使执行前指令重排到执行后
        // weakCompareAndSet: CAS, 设置值时可能失败, 不会指令重排, 使用内存屏障
        VarHandle psiHandle = MethodHandles.lookup().findStaticVarHandle(VarHandleTest.class, "psi", int.class);
        VarHandle piHandle = MethodHandles.lookup().findVarHandle(VarHandleTest.class, "pi", int.class);
        VarHandleTest obj = new VarHandleTest();
        // 静态字段需要两个参数: 预计值、设置值
        Assertions.assertTrue(psiHandle.compareAndSet(42, 43));
        // 实例字段需要三个参数: 实例、预计值、设置值
        Assertions.assertTrue(piHandle.compareAndSet(obj, 42, 43));
        Assertions.assertEquals(43, psiHandle.get());
        Assertions.assertEquals(43, piHandle.get(obj));

        Assertions.assertFalse(psiHandle.compareAndSet(42, 44));
        Assertions.assertFalse(piHandle.compareAndSet(obj, 42, 44));
        Assertions.assertEquals(43, psiHandle.get());
        Assertions.assertEquals(43, piHandle.get(obj));
    }

    @Test
    public void compareAndExchange() throws Throwable {
        // var ret = get();
        // if (get() == arg[0]) {
        //     set(arg[1]);
        // }
        // return ret;
        // CompareAndSetAcquire: CAS, 设置值时可能失败, 不会使执行后指令重排到执行前
        // CompareAndSetRelease: CAS, 设置值时可能失败, 不会使执行前指令重排到执行后
        // compareAndSet: CAS, 设置值时不会失败, 不会指令重排, 使用内存屏障
        VarHandle psiHandle = MethodHandles.lookup().findStaticVarHandle(VarHandleTest.class, "psi", int.class);
        VarHandle piHandle = MethodHandles.lookup().findVarHandle(VarHandleTest.class, "pi", int.class);
        VarHandleTest obj = new VarHandleTest();
        // 静态字段需要两个参数: 预计值、设置值
        Assertions.assertEquals(42, psiHandle.compareAndExchange(42, 43));
        // 实例字段需要三个参数: 实例、预计值、设置值
        Assertions.assertEquals(42, piHandle.compareAndExchange(obj, 42, 43));
        Assertions.assertEquals(43, psiHandle.get());
        Assertions.assertEquals(43, piHandle.get(obj));

        Assertions.assertEquals(43, psiHandle.compareAndExchange(42, 44));
        Assertions.assertEquals(43, piHandle.compareAndExchange(obj, 42, 44));
        Assertions.assertEquals(43, psiHandle.get());
        Assertions.assertEquals(43, piHandle.get(obj));
    }

    @Test
    public void getAndSet() throws Throwable {
        // var ret = get();
        // set(arg[0]);
        // return ret;
        // getAndSetAcquire: 获取字段值并设置, 不会使执行后指令重排到执行前
        // getAndSetRelease: 获取字段值并设置, 不会使执行前指令重排到执行后
        // getAndSet: 获取字段值并设置, 不会指令重排, 使用内存屏障
        VarHandle psiHandle = MethodHandles.lookup().findStaticVarHandle(VarHandleTest.class, "psi", int.class);
        VarHandle piHandle = MethodHandles.lookup().findVarHandle(VarHandleTest.class, "pi", int.class);
        VarHandleTest obj = new VarHandleTest();
        // 静态字段无需参数
        Assertions.assertEquals(42, psiHandle.getAndSet(43));
        // 实例字段需要一个参数: 实例
        Assertions.assertEquals(42, piHandle.getAndSet(obj, 43));

        Assertions.assertEquals(43, psiHandle.get());
        Assertions.assertEquals(43, piHandle.get(obj));
    }

    @Test
    public void toMethodHandle() throws Throwable {
        // toMethodHandle: 转成MethodHandle调用
        VarHandle psiHandle = MethodHandles.lookup().findStaticVarHandle(VarHandleTest.class, "psi", int.class);
        VarHandle piHandle = MethodHandles.lookup().findVarHandle(VarHandleTest.class, "pi", int.class);
        VarHandleTest obj = new VarHandleTest();

        MethodHandle psiGet = psiHandle.toMethodHandle(VarHandle.AccessMode.GET);
        MethodHandle psiSet = psiHandle.toMethodHandle(VarHandle.AccessMode.SET);
        MethodHandle piGet = piHandle.toMethodHandle(VarHandle.AccessMode.GET);
        MethodHandle piSet = piHandle.toMethodHandle(VarHandle.AccessMode.SET);

        Assertions.assertEquals(42, (int) psiGet.invokeExact());
        Assertions.assertEquals(42, (int) piGet.invokeExact(obj));

        psiSet.invokeExact(43);
        piSet.invokeExact(obj, 43);
        Assertions.assertEquals(43, (int) psiGet.invokeExact());
        Assertions.assertEquals(43, (int) piGet.invokeExact(obj));
    }

}
