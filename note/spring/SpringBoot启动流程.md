# SpringApplication.run(String...)
1. _(系统)`Startup.create()`: 创建秒表，记录运行时间_
2. (系统)`SpringApplication#createBootstrapContext`: 创建`ConfigurableBootstrapContext`
3. _(系统)`SpringApplication#configureHeadlessProperty`: 配置环境变量`java.awt.headless`_
4. (系统)`SpringApplication#getRunListeners`: 创建`SpringApplicationRunListener`(生命周期监听器)
5. `SpringApplicationRunListener#starting`: 调用生命周期函数`starting`
6. _(系统)`new DefaultApplicationArguments(args)`: 封装参数_
7. (系统)`SpringApplication#prepareEnvironment`: 初始化环境
    1. (系统)`SpringApplication#getOrCreateEnvironment`: 创建环境，根据web类型创建`ApplicationServletEnvironment`或`ApplicationReactiveWebEnvironment`或`ApplicationEnvironment`
    2. _(系统)`SpringApplication#configureEnvironment`: 配置环境_
    3. _`ConfigurationPropertySources.attach`_
    4. `SpringApplicationRunListener#environmentPrepared`: 调用生命周期函数`environmentPrepared`
    5. _`SpringApplication#bindToSpringApplication`_
    6. _`ConfigurationPropertySources.attach`_
8. (系统)`SpringApplication#printBanner`: 打印`Banner`·
9. (系统)`SpringApplication#createApplicationContext`: 创建`ConfigurableApplicationContext`，根据web类型创建`ServletWebServerApplicationContext`或`ReactiveWebServerApplicationContext`或`GenericApplicationContext`
10. _(系统)`ConfigurableApplicationContext#setApplicationStartup`: 设置ApplicationStartup_
11. `SpringApplication#prepareContext`
    1. _(系统)`ConfigurableApplicationContext#setEnvironment`: 设置环境_
    2. _(系统)`SpringApplication#postProcessApplicationContext`: 设置一些属性_
    3. `ApplicationContextInitializer#initialize`
    4. `SpringApplicationRunListener#contextPrepared`: 调用生命周期函数`contextPrepared`
    5. (系统)关闭`ConfigurableBootstrapContext`
    6. _(系统)添加一系列内置bean及钩子bean_
    7. _`SpringApplication#load`: 加载上下文(此处加载主类定义)_
    8. `SpringApplicationRunListener#contextLoaded`: 调用生命周期函数`contextLoaded`
12. **(系统)`AbstractApplicationContext#refresh`: [启动Spring](./Spring启动流程.md)**
13. (系统)`SpringApplication#afterRefresh`: 空方法
14. _(系统)`Startup#started`_
15. `SpringApplicationRunListener#started`: 调用生命周期函数`started`
    - `ApplicationListener`接收事件`AvailabilityChangeEvent`, state: `ACCEPTING_TRAFFIC`
16. `SpringApplication#callRunners`执行`CommandLineRunner`
17. `SpringApplicationRunListener#ready`: 调用生命周期函数`ready`
    - `ApplicationListener`接收事件`ApplicationReadyEvent`
18. (系统)`SpringApplication#run`方法结束
19. *============================== 程序运行 ==============================*
20. (用户)发送http请求结束后: `ApplicationListener`接收事件`ServletRequestHandledEvent`
21. (用户)发布自定义事件时: `ApplicationListener`接收事件
22. *============================== 程序退出 ==============================*
23. `ApplicationListener`接收事件`AvailabilityChangeEvent`, state: `REFUSING_TRAFFIC`
24. `ApplicationListener`接收事件`ContextClosedEvent`
25. `SmartLifecycle#stop`
26. 执行`DisposableBean#destroy`
27. 执行`@Bean#destroyMethod`方法
