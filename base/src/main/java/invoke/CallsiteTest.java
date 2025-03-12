package invoke;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.invoke.*;

public class CallsiteTest {

    @Test
    public void constant() throws Throwable {
        MethodHandle printA = MethodHandles.lookup().findVirtual(CallsiteTest.class, "printA", MethodType.methodType(void.class, String.class));
        MethodHandle printB = MethodHandles.lookup().findVirtual(CallsiteTest.class, "printB", MethodType.methodType(void.class, String.class));
        CallSite callSite = new ConstantCallSite(printA);
        MethodHandle methodHandle = callSite.dynamicInvoker().bindTo(this);

        // printA: 1
        methodHandle.invokeExact("1");
        // printA: 2
        methodHandle.invokeExact("2");

        Assertions.assertThrows(UnsupportedOperationException.class, () -> callSite.setTarget(printB));

        // printA: 1
        callSite.dynamicInvoker().invokeExact(this, "1");
        // printA: 2
        callSite.dynamicInvoker().invokeExact(this, "2");
    }

    @Test
    public void mutable() throws Throwable {
        MethodType type = MethodType.methodType(void.class, String.class);
        MethodHandle printA = MethodHandles.lookup().findVirtual(CallsiteTest.class, "printA", type);
        MethodHandle printB = MethodHandles.lookup().findVirtual(CallsiteTest.class, "printB", type);
        CallSite callSite = new MutableCallSite(printA);
        MethodHandle methodHandle = callSite.dynamicInvoker().bindTo(this);

        // printA: 1
        methodHandle.invokeExact("1");
        // printA: 2
        methodHandle.invokeExact("2");

        callSite.setTarget(printB);
        // printB: 1
        methodHandle.invokeExact("1");
        // printB: 2
        methodHandle.invokeExact("2");
    }

    private void printA(String s) {
        System.out.println("printA: " + s);
    }

    private void printB(String s) {
        System.out.println("printB: " + s);
    }

}
