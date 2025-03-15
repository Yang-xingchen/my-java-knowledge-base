# spi
spi(Service Provider Interface, 服务者提供接口), 可加载特定接口未知实现类。

使用方式(基础):
1. 本jar包定义接口[Service.java](Service.java)
2. 其他jar包实现该接口，需要公共无参构造函数。
3. 实现该接口的jar包添加文件 `/resources/META-INF/services/{serviceName})`, 其中`{serviceName}`替换为实现的接口全限定名。内容为实现该接口的类全限定名，每行一条。可使用`#`进行注释。[spi.Service](../../resources/META-INF/services/spi.Service)
4. 即可使用`ServiceLoader`加载对应类。[Main.java](Main.java)

使用方式(模块):
1. 本jar包定义接口[Service.java](Service.java), 且`module-info`需导出该对应包`exports {package};`
2. 其他jar包实现该接口，需要公共无参构造函数, 或者提供包含公共静态无参`provider`方法的工厂类。
3. `module-info`添加`provides {serviceName} with {implName};`, 其中`{serviceName}`替换为实现的接口全限定名。内容为实现该接口的类全限定名, `{implName}`替换为实现的类或工厂类全限定名。
4. 即可使用`ServiceLoader`加载对应类。[Main.java](Main.java)。该包`module-info`需使用对应类`uses {serviceName};`

