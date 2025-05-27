package springstudy.event.component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import springstudy.event.AnnotationComponent;

@Slf4j
@AnnotationComponent("b class")
public class B implements BeanNameAware, BeanClassLoaderAware, BeanFactoryAware,  InitializingBean, DisposableBean {

    private A a;

    public B() {
        log.info("created B");
    }

    public String test() {
        return a == null ? "null" : a.toString();
    }

    @PostConstruct
    public void postConstruct() {
        log.info("B postConstruct");
    }

    @Autowired
    public B setA(A a) {
        this.a = a;
        log.info("B set A");
        return this;
    }

    @Override
    public void setBeanName(String name) {
        log.info("B setBeanName");
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        log.info("B setBeanClassLoader");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        log.info("B setBeanFactory");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("afterPropertiesSet B");
    }

    @PreDestroy
    public void preDestroy() {
        log.info("B preDestroy");
    }

    @Override
    public void destroy() throws Exception {
        log.info("destroy B");
    }

}
