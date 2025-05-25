package springstudy.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

import java.time.Duration;
import java.util.Arrays;

/**
 * NOTE: 需要在spring.factories配置该类，否则不生效
 */
@Slf4j
public class RunListener implements SpringApplicationRunListener {

    public RunListener() {
        System.out.println("created RunListener...");
    }

    @Override
    public void starting(ConfigurableBootstrapContext bootstrapContext) {
        System.out.println("starting...");
    }

    @Override
    public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext, ConfigurableEnvironment environment) {
        log.info("environmentPrepared...");
        // 在contextPrepared后，contextLoaded前执行
        bootstrapContext.addCloseListener(event -> log.info("bootstrapContext close: {}", event));
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        log.info("contextPrepared...");
        // 在contextLoaded后，初始化实例前
//        context.addBeanFactoryPostProcessor(beanFactory -> log.info("BeanDefinitionNames: {}", Arrays.toString(beanFactory.getBeanDefinitionNames())));
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        log.info("contextLoaded...");
    }

    @Override
    public void started(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("started: {}", timeTaken);
    }

    @Override
    public void ready(ConfigurableApplicationContext context, Duration timeTaken) {
        log.info("ready: {}", timeTaken);
    }

    @Override
    public void failed(ConfigurableApplicationContext context, Throwable exception) {
        log.info("failed:", exception);
    }

}
