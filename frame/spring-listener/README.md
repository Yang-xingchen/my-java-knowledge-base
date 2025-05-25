# spring 事件/生命周期

## 可用类型
### org.springframework.boot.SpringApplicationRunListener
> 处理程序启动相关生命周期, 一般不直接使用, 而是通过`org.springframework.context.ApplicationListener`监听`SpringApplicationEvent`相关事件处理(部分早期事件监听不到)

需要在[spring.factories](src/main/resources/META-INF/spring.factories)中定义。

[RunListener.java](src/main/java/springstudy/event/RunListener.java)

### org.springframework.beans.factory.config.BeanFactoryPostProcessor
> 处理及修改bean定义, 也可获取`ConfigurableListableBeanFactory`, **不能与bean实例交互**

可以使用`@Component`配置。

[MyBeanFactoryPostProcessor.java](src/main/java/springstudy/event/MyBeanFactoryPostProcessor.java)

### org.springframework.beans.factory.config.BeanPostProcessor
> 处理及修改bean实例, 参数仅有名称和实例对象

可以使用`@Component`配置。

[MyBeanPostProcessor.java](src/main/java/springstudy/event/MyBeanPostProcessor.java)

### org.springframework.context.ApplicationListener
> 处理程序运行相关生命周期事件，通过观察者模式扩展

可以使用`@Component`配置。

泛型可限定接收事件类型

[Listener.java](src/main/java/springstudy/event/Listener.java)

### org.springframework.context.SmartLifecycle
> 处理生命周期，用处不大

可以使用`@Component`配置

[MyLifecycle.java](src/main/java/springstudy/event/MyLifecycle.java)

## 执行顺序
[SpringBoot启动流程.md](../../note/spring/SpringBoot启动流程.md)