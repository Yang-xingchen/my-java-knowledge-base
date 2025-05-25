package springstudy.event.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.*;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import springstudy.event.AnnotationComponent;

@Slf4j
public class A implements InitializingBean, DisposableBean {

    public A() {
        log.info("created A");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("afterPropertiesSet A");
    }

    public void init() {
        log.info("A init");
    }

    @Override
    public void destroy() throws Exception {
        log.info("destroy A");
    }

    public void destroyMethod() {
        log.info("destroyMethod A");
    }

}
