# java.lang.invoke
调用的抽象，主要类为:
1. [`MethodHandle`(方法句柄)](./MethodHandleTest.java)
2. [`VarHandle`(变量句柄)](./VarHandleTest.java)
3. [`MethodHandles`(操作工具类)](./MethodHandlesTest.java)
4. [`Callsite`(调用点)](./CallsiteTest.java)
4. [`LambdaMetafactory`(lambda实现)](./LambdaMetafactoryTest.java)


## 方法句柄 MethodHandle
方法句柄是对底层方法、构造函数、字段或类似的低级操作的有类型的、直接可执行的引用，具有参数或返回值的可选转换。这些转换非常通用，包括转换、插入、删除和替换等模式。

类似`java.lang.reflect.Method`

不同点: 
1. MethodHandle权限判断在获取lookup时, Method每次调用都需要判断权限
2. MethodHandle参数、返回值(有返回值调用时返回值不可忽略)需和原方法一致, Method可以使用继承，可忽略返回值
3. MethodHandle无法获取方法信息(标识、参数类型、返回值、注解等), Method可以获取方法信息
4. MethodHandle支持调整参数, Method每次调用需按方法签名调用
5. MethodHandel可相互组合，实现图灵完备

## 变量句柄 VarHandle
变量句柄，是对变量字段的底层的抽象, 类似`java.lang.reflect.Field`

不同点:
1. VarHandle权限判断在获取lookup时, Field每次调用都需要判断权限
2. VarHandle可对访问设值进行更底层的控制(内存屏障、指令重排等，类似使用Atom类及Unsafe类), Field只能按字段类型处理
3. VarHandle无法获取方法信息(标识、类型、注解等), Field可以获取方法信息
4. VarHandle方法可转成MethodHandle, 以支持MethodHandle优点

## 调用点 Callsite
对`MethodHandle`的一层封装。
- ConstantCallSite: `dynamicInvoker()`返回MethodHandle为常量。
- MutableCallSite: `dynamicInvoker()`返回`MethodHandle`为原`MethodHandle`的代理，可通过`setTarget()`更改目标`MethodHandle`。
- VolatileCallSite: 同`MutableCallSite`，但是线程安全。

## LambdaMetafactory
java8 lambda的实现方式
