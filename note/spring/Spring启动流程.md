# AbstractApplicationContext#refresh
> spring启动时构造函数最终将会调用到该方法
1. _(系统)`ApplicationStartup#start`_
2. `AbstractApplicationContext#prepareRefresh`: 准备刷新
3. `AbstractApplicationContext#obtainFreshBeanFactory`: 供子类执行`refreshBeanFactory`
   - `AbstractRefreshableApplicationContext`: 可多次调用`refresh`，每次创建一个新的内部bean工厂实例。
   - `GenericApplicationContext`: 只能调用一次`refresh`。
4. `AbstractApplicationContext#prepareBeanFactory`: 配置标准上下文特性
5. `AbstractApplicationContext#postProcessBeanFactory`: 供子类注册BeanPostProcessors等
6. _(系统)`ApplicationStartup#start`_
7. `AbstractApplicationContext#invokeBeanFactoryPostProcessors`: 调用`BeanFactoryPostProcessor`
    - `BeanDefinitionRegistryPostProcessor`
        1. `BeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry`获取`BeanDefinitionRegistry`
        2. `BeanFactoryPostProcessor#postProcessBeanFactory`
    - **(系统)`ConfigurationClassPostProcessor`: 处理`@Configuration`, 注册bean定义**
    - (系统)`ServletComponentRegisteringPostProcessor`: 处理`@WebServlet`, `@WebFilter`, `@WebListener`
    - (系统)`PropertyOverrideConfigurer`: 处理bean属性值: `beanName.property=value`
    - (系统)`PlaceholderConfigurerSupport`: 处理使用占位符的属性值: `${...}`
    - (系统)`GenericScope`: 记录扩展的`scope`，如`refresh`
8. (系统)`AbstractApplicationContext#registerBeanPostProcessors`: 注册`BeanPostProcessor`
9. _(系统)`StartupStep#end`_
10. _(系统)`AbstractApplicationContext#initMessageSource`: 初始化信息源`MessageSource`，和国际化相关_
11. (系统)`AbstractApplicationContext#initApplicationEventMulticaster`: 初始化容器事件传播器`ApplicationEventMulticaster`
12. (系统)`AbstractApplicationContext#onRefresh`: 供子类某些特殊bean的初始化
    - (系统)初始化tomcat
13. (系统)`AbstractApplicationContext#registerListeners`: 注册`ApplicationListener`到`ApplicationEventMulticaster`
14. (系统)`AbstractApplicationContext#finishBeanFactoryInitialization`: 初始化Bean
    1. 初始化`ConversionService`
    2. 注册`StringValueResolver`
    3. 初始化`LoadTimeWeaverAware`
    4. _(系统)`ConfigurableListableBeanFactory#setTempClassLoader`: 停止使用临时类加载器_
    5. _(系统)`ConfigurableListableBeanFactory#freezeConfiguration`: 冻结配置，使用缓存_
    6. (系统)`ConfigurableListableBeanFactory#preInstantiateSingletons`: 创建非`lazy-init`bean
       1. **(系统)`AbstractBeanFactory#getBean`: [创建bean](BeanFactory%23getBean.md)**
       2. `SmartInitializingSingleton#afterSingletonsInstantiated`
15. (系统)`AbstractApplicationContext#finishRefresh`: 刷新完成处理，发布相应事件
    1. (系统)`AbstractApplicationContext#resetCommonCaches`: 清理核心缓存
    2. (系统)`AbstractApplicationContext#clearResourceCaches`: 清理上下文级缓存
    3. (系统)`AbstractApplicationContext#initLifecycleProcessor`: 初始化`LifecycleProcessor`
        - `SmartLifecycle#start`
        - `ApplicationListener`接收事件`ServletWebServerInitializedEvent`
    4. (系统)`AbstractApplicationContext#publishEvent`: 发布事件
        - `ApplicationListener`接收事件`ContextRefreshedEvent`
16. _(系统)`ApplicationStartup#end`_
