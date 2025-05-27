# 三级缓存
> 用于处理单例bean的循环依赖问题
```java
/**
 * 一级缓存
 * 存放完整bean(即已经实例化+初始化完成的bean，可直接使用的bean对象)
 */
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
/**
 * 二级缓存
 * 存放半成品bean(只是实例化还未初始化)
 */
private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
/**
 * 三级缓存
 * 存放bean的ObjectFactory
 * 主要用于处理AOP
 */
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
```
处理情况:
- 程序获取(手动`getBean`, 非懒加载等):
   - 一级: 已创建好的单例bean
   - 三级缓存都获取不到: 完全未创建的的单例bean
- 依赖处理(`@DependsOn`, 构造函数引用, `@Autowired`等)、初始化(见`AbstractAutowireCapableBeanFactory#createBean.4.5`):
   - 一级: 已创建好的单例bean
   - 二级: 正在创建的单例bean(正在处理依赖或正在初始化)且已被其他循环依赖获取过
   - 三级: 正在创建的单例bean(正在处理依赖或正在初始化)且未被循环依赖引用
   - 三级缓存都获取不到: 完全未创建的的单例bean
下文引用:
- `AbstractBeanFactory#getBean.2.1`: 从一级缓存开始逐级获取bean，即上文的处理情况
- `AbstractBeanFactory#getBean.3.7.单例.1`: 此处仅处理一级缓存, 若无则创建并存入一级缓存
- `AbstractAutowireCapableBeanFactory#createBean.4.3`: 此处仅处理三级缓存，提前暴露正在处理的bean

# AbstractBeanFactory#getBean
> 获取bean流程，可能创建bean
1. (系统)`AbstractBeanFactory#transformedBeanName`转换bean名称: 去掉工厂解引用前缀及处理别名
2. (系统)获取已创建的单例bean(可能是正在创建的bean)
   1. (系统)`DefaultSingletonBeanRegistry#getSingleton`: 三级缓存获取bean
      1. (系统)一级缓存获取
      2. (系统)获取不到，且当前未创建，从二级缓存获取
      3. (系统)获取不到，加锁重新从一级缓存获取
      4. (系统)获取不到，再次从二级缓存获取
      5. (系统)获取不到，从三级缓存获取ObjectFactory
      6. (系统)若获取到，得到结果，并加入二级缓存且移除三级缓存
      7. (系统)4.5.6结束后，解锁
   2. (系统)`AbstractBeanFactory#getObjectForBeanInstance`: 对FactoryBean的一些处理
   3. (系统)得到bean实例
3. (系统)未获取到bean
   1. (系统)`AbstractBeanFactory#isPrototypeCurrentlyInCreation`检查原型类型bean是否正在创建，于`3.7.原型.1`/`3.7.其他.2.1`标记及`3.7.原型.3`/`3.7.其他.2.3`移除标记，避免循环引用
   2. (系统)若该bean不是该容器定义的bean，则从父容器查找bean，会一直递归到根容器，若找到则返回
   3. (系统)若是该容器定义的bean或者父容器中未找到该bean，则继续
   4. (系统)_标记bean正在创建_
   5. (系统)获取及检查bean定义
   6. (系统)递归该bean依赖的bean进行初始化
   7. (系统)创建及初始化bean
      - 单例
        1. (系统)`DefaultSingletonBeanRegistry#getSingleton`: 获取单例bean
           1. (系统)加锁
           2. (系统)从一级缓存获取bean，获取到则解锁并返回
           3. (系统)检查创建标记
           4. (系统)`AbstractAutowireCapableBeanFactory#createBean`创建bean, 流程见下文
           5. (系统)移除创建标记
           6. (系统)添加到一级缓存
        2. (系统)`AbstractBeanFactory#getObjectForBeanInstance`: 对FactoryBean的一些处理
        3. (系统)得到bean实例
      - 原型
        1. (系统)标记该bean正在创建
        2. (系统)`AbstractAutowireCapableBeanFactory#createBean`创建bean, 流程见下文
        3. (系统)移除bean正在创建标记
        4. (系统)`AbstractBeanFactory#getObjectForBeanInstance`: 对FactoryBean的一些处理
        5. (系统)得到bean实例
      - 其他
        1. (系统)获取对应scope处理对象
        2. (系统)获取bean
           - 若需要创建，则
             1. (系统)标记该bean正在创建
             2. (系统)`AbstractAutowireCapableBeanFactory#createBean`创建bean, 流程见下文
             3. (系统)移除bean正在创建标记
        3. (系统)`AbstractBeanFactory#getObjectForBeanInstance`: 对FactoryBean的一些处理
        4. (系统)得到bean实例
   8. (系统)得到bean实例
4. (系统)检查2.3步获取的bean实例类型，并返回

# AbstractAutowireCapableBeanFactory#createBean
> 创建bean流程，由实例化、设置属性、初始化构成
1. (系统)`AbstractBeanFactory#resolveBeanClass`: 解析bean定义，获取class引用
2. (系统)`RootBeanDefinition#prepareMethodOverrides`: 校验和准备 bean 中的方法覆盖
3. (系统)`AbstractBeanFactory#resolveBeforeInstantiation`: 给BeanPostProcessors一个返回代理而不是目标bean实例的机会
   1. `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`
   2. `BeanPostProcessor#postProcessAfterInitialization`
4. (系统)`AbstractBeanFactory#doCreateBean`: 实际创建bean
   1. (系统)`AbstractBeanFactory#createBeanInstance`: 实例化bean
       1. (系统)`AbstractBeanFactory#resolveBeanClass`: 解析bean定义，获取class引用
       2. (系统)`AbstractBeanFactory#obtainFromSupplier`: 特殊方法创建bean
       3. (系统)`AbstractBeanFactory#instantiateUsingFactoryMethod`: 工厂方法创建bean
       4. (系统)使用容器的自动装配方法进行实例化
           - 配置了自动装配属性，使用容器的自动装配实例化，即，根据参数类型匹配 bean 的构造方法
           - 使用无参构造函数创建
       5. `SmartInstantiationAwareBeanPostProcessor#determineCandidateConstructors`: 查找构造函数
           - (系统)`AutowiredAnnotationBeanPostProcessor`: 查找使用`@Autowire`或`@Inject`定义的构造函数
       6. (系统)获取首选的构造函数
       7. (系统)使用无参构造函数
      > 3,4,5,6,7将调用以下几个方法创建
       - (系统)`ConstructorResolver#instantiateUsingFactoryMethod`: 使用工厂方法创建
           1. (系统)如果不是静态方法，创建对应bean
           2. (系统)准备使用的工厂方法及参数
           3. (系统)如果没有确定的工厂方法，尝试匹配该类中名称一致的方法(即有多个同名的工厂方法)
           4. (系统)准备可用的方法
           5. (系统)如果方法只有一个且无参数，使用该参数创建实例，流程同`AbstractBeanFactory#instantiateBean`(非调用该方法)
           6. (系统)多个方法进行排序，规则: 是否public->参数数量
           7. _(系统)准备参数_
           8. (系统)循环排序后的工厂方法, 获取创建的构造函数及参数(递归创建bean)
           9. (系统)错误处理
           10. (系统)创建实例，流程同`AbstractBeanFactory#instantiateBean`(非调用该方法)
       - (系统)`ConstructorResolver#autowireConstructor`: 通过构造函数参数类型创建(可能无参，如`4.3.5`中查找到的)
           1. (系统)准备使用的构造函数及参数
           2. (系统)如果没有提供的构造函数，从类中查找构造函数
           3. (系统)如果构造函数只有一个且无参数，使用该参数创建实例，流程同`AbstractBeanFactory#instantiateBean`(非调用该方法)
           4. _(系统)准备参数_
           5. (系统)`AutowireUtils#sortConstructors`对可用构造函数进行排序，规则: 是否public->参数数量
           6. (系统)循环排序后的构造函数, 获取创建的构造函数及参数(递归创建bean)
           7. (系统)错误处理
           8. (系统)创建实例，流程同`AbstractBeanFactory#instantiateBean`(非调用该方法)
       - (系统)`AbstractBeanFactory#instantiateBean`: 使用无参构造函数
           1. (系统)获取实例化策略
               - `SimpleInstantiationStrategy`: 反射方法
           2. (系统)实例化
           3. (系统)封装`BeanWrapper`
   2. `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`
   3. (系统)`AbstractBeanFactory#addSingletonFactory`: 添加到三级缓存`singletonFactories`
      1. 加锁
      2. 如果一级缓存获取到，解锁并返回
      3. 添加到三级缓存，添加的方法为使用`SmartInstantiationAwareBeanPostProcessor#getEarlyBeanReference`处理的bean
         - (系统)`AbstractAutoProxyCreator`: AOP
      4. 移除二级缓存
      5. 注册创建
   4. (系统)`AbstractAutowireCapableBeanFactory#populateBean`: 设置属性
      1. 空值检查
      2. record检查
      3. `InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation`
      4. **通过名称或类型获取注入的值**
      5. `InstantiationAwareBeanPostProcessor#postProcessProperties`
         - `AutowiredAnnotationBeanPostProcessor`: 处理`@Autowired`或`@Inject`属性注入
           1. `ConfigurableListableBeanFactory#resolveDependency`: 获取依赖对象，此处处理`@Lazy`注解
      6. 依赖检查
      7. 实际注入
   5. (系统)`AbstractAutowireCapableBeanFactory#initializeBean`: 初始化bean
      1. (系统)`AbstractAutowireCapableBeanFactory#invokeAwareMethods`
          1. `BeanNameAware#setBeanName`
          2. `BeanClassLoaderAware#setBeanClassLoader`
          3. `BeanFactoryAware#setBeanFactory`
      2. `BeanPostProcessor#postProcessBeforeInitialization`
          - (系统)`ApplicationContextAwareProcessor`
              1. `EnvironmentAware#setEnvironment`
              2. `EmbeddedValueResolverAware#setEmbeddedValueResolver`
              3. `ResourceLoaderAware#setResourceLoader`
              4. `ApplicationEventPublisherAware#setApplicationEventPublisher`
              5. `MessageSourceAware#setMessageSource`
              6. `ApplicationStartupAware#setApplicationStartup`
              7. `ApplicationContextAware#setApplicationContext`
          - (系统)`ServletContextAwareProcessor`
              1. `ServletContextAware#setServletContext`
              2. `ServletConfigAware#setServletConfig`
          - (系统)`InitDestroyAnnotationBeanPostProcessor`处理`@PostConstruct`
          - (系统)`LoadBalancerRestClientBuilderBeanPostProcessor`处理包含`@LoadBalanced`的`RestClient.Builder`
          - (系统)`LoadBalancerWebClientBuilderBeanPostProcessor`处理包含`@LoadBalanced`的`WebClient.Builder`
      3. (系统)`AbstractAutowireCapableBeanFactory#invokeInitMethods`
          1. 执行`InitializingBean#afterPropertiesSet`
          2. 执行`@Bean#initMethod`方法
      4. `BeanPostProcessor#postProcessAfterInitialization`
          - (系统)`ConfigurationPropertiesBindingPostProcessor`处理`@ConfigurationProperties`
          - (系统)`AbstractAutoProxyCreator`: AOP
