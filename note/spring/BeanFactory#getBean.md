# 三级缓存
```java
// 一级缓存      存放完整Bean对象(实例化+初始化)
private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);
// 二级缓存      存放一个半成品bean对象(只是实例化还未初始化)，提前暴露
private final Map<String, Object> earlySingletonObjects = new ConcurrentHashMap<>(16);
// 三级缓存      存放一个ObjectFactory
private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);
```

# AbstractBeanFactory#getBean
> 获取bean流程，可能创建bean
1. (系统)`AbstractBeanFactory#transformedBeanName`转换bean名称: 去掉工厂解引用前缀及处理别名
2. (系统)获取已创建的单例bean(可能是正在创建的bean)
   1. (系统)`DefaultSingletonBeanRegistry#getSingleton`: 三级缓存获取bean
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
        1. (系统)从`singletonObjects`获取bean
        2. (系统)若获取不到，则`AbstractAutowireCapableBeanFactory#createBean`创建bean, 流程见下文
        3. (系统)`AbstractBeanFactory#getObjectForBeanInstance`: 对FactoryBean的一些处理
        4. (系统)得到bean实例
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
1. (系统)`AbstractBeanFactory#resolveBeanClass`: 解析bean定义，获取class引用
2. (系统)`RootBeanDefinition#prepareMethodOverrides`: 校验和准备 bean 中的方法覆盖
3. (系统)`AbstractBeanFactory#resolveBeforeInstantiation`: 给BeanPostProcessors一个返回代理而不是目标bean实例的机会
4. (系统)`AbstractBeanFactory#doCreateBean`: 实际创建bean
   1. (系统)`AbstractBeanFactory#createBeanInstance`: 实例化bean
   2. `MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition`
   3. (系统)`AbstractBeanFactory#addSingletonFactory`: 添加到三级缓存`singletonFactories`
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
   4. (系统)`AbstractAutowireCapableBeanFactory#populateBean`: 设置属性
   5. (系统)`AbstractAutowireCapableBeanFactory#initializeBean`
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
          - (系统)`LoadBalancerRestClientBuilderBeanPostProcessor`处理包含`@LoadBalanced`的`RestClient.Builder`
          - (系统)`LoadBalancerWebClientBuilderBeanPostProcessor`处理包含`@LoadBalanced`的`WebClient.Builder`
      3. (系统)`AbstractAutowireCapableBeanFactory#invokeInitMethods`
          1. 执行`InitializingBean#afterPropertiesSet`
          2. 执行`@Bean#initMethod`方法
      4. `BeanPostProcessor#postProcessAfterInitialization`
          - (系统)`ConfigurationPropertiesBindingPostProcessor`处理`@ConfigurationProperties`
