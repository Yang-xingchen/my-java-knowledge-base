# Java内存模型
所有变量(即对象中的数据)存储于共享的主内存中(Main Memory)，每个线程有自己的工作线程(Working Memory)。线程对变量的所有操作，都必须在自己的工作线程中进行，禁止对主内存或其他线程的工作线程操作。

# 内存屏障 & 内存排序模式
> 参考: https://gee.cs.oswego.edu/dl/html/j9mm.html

内存屏障分为以下指令:
- LoadLoad: 保证指令前的加载先于指令后的加载
- LoadStore: 保证指令前的加载先于指令后的存储(刷到主内存)
- StoreStore: 保证指令前的存储先于指令后的存储
- StoreLoad: 保证指令前的存储先于指令后的加载

内存排序模式按弱到强:
1. Plain: 普通访问，不使用内存屏障，可能任意指令重排
2. Opaque: JDK9新增，不使用内存屏障，不会指令重排
3. Release/Acquire: JDK9新增，使用内存屏障，可能部分重排
    - Release: 使用`LoadStore`+`StoreStore`屏障，不会导致指令前的操作重排到指令后的存储之后，用于写入前
    - Acquire: 使用`LoadLoad`+`LoadStore`屏障，不会导致指令后的操作重排到指令前的加载之前，用于读取后
4. Volatile: `volatile`定义的字段，使用内存屏障(前加`LoadStore`+`StoreStore`, 后加`StoreLoad`+`LoadLoad`)，不会导致重排

# 先行发生(Happens-Before)
两个操作间的关联性，如果`操作A`先行发生`操作B`，则`操作A`的结果在`操作B`中是可见的。

JMM下的先行发生关系:
- 程序次序(Program Order Rule): 在同一个线程内，`写在前面的操作`先行发生`写在后面的操作`
- 管程锁定(Monitor Lock Rule): 同一个锁的`解锁操作`先行发生`后续的加锁操作`
- volatile变量规则(Volatile Variable Rule): 对`volatile`变量的`写操作`先行发生`后续的读操作`
- 线程启动规则(Thread Start Rule): `Thread.start()`先行发生`该线程所有操作`
- 线程终止规则(Thread End Rule): `线程所有操作`先行发生`Thread.join()后的操作`
- 线程中断规则(Thread Interruption Rule): `Thread.interrupt()`先行发生`线程中检测中断代码`
- 对象终结规则(Finalizer Rule): `对象的构造方法完成`先行发生`对象的finalizer()开始`
- 传递性(Transitivity): 若`操作A`先行发生`操作B`且`操作B`先行发生`操作C`，则`操作A`先行发生`操作C`