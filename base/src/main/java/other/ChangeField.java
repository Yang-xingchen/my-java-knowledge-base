package other;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sun.misc.Unsafe;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.lang.reflect.Field;

public class ChangeField {

    /**
     * 直接分配
     */
    @Test
    public void byAssign() {
        Entry entry = new Entry();

        entry.pubString = "assign";

        Assertions.assertEquals("assign", entry.getPubString());
    }

    /**
     * 反射
     */
    @Test
    public void byReflect() throws Exception {
        Entry entry = new Entry();

        Field field = entry.getClass().getDeclaredField("string");
        field.setAccessible(true);
        field.set(entry, "hack");

        Assertions.assertEquals("hack", entry.getString());
    }

    /**
     * 方法句柄
     */
    @Test
    public void byMethodHandle() throws Throwable {
        Entry entry = new Entry();

        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Entry.class, MethodHandles.lookup());
        MethodHandle methodHandle = lookup.findSetter(Entry.class, "string", String.class);
        methodHandle.invokeExact(entry, "hack");

        Assertions.assertEquals("hack", entry.getString());
    }

    /**
     * 变量句柄
     */
    @Test
    public void byVarHandle() throws Exception {
        Entry entry = new Entry();

        MethodHandles.Lookup lookup = MethodHandles.privateLookupIn(Entry.class, MethodHandles.lookup());
        VarHandle varHandle = lookup.findVarHandle(Entry.class, "string", String.class);
        varHandle.set(entry, "hack");

        Assertions.assertEquals("hack", entry.getString());
    }

    /**
     * Unsafe
     */
    @Test
    public void byUnsafe() throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        Unsafe unsafe = (Unsafe) unsafeField.get(null);

        Entry entry = new Entry();

        Field field = entry.getClass().getDeclaredField("string");
        long offset = unsafe.objectFieldOffset(field);
        unsafe.putObject(entry, offset, "hack");

        Assertions.assertEquals("hack", entry.getString());
    }

}
