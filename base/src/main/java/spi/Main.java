package spi;

import org.junit.jupiter.api.Test;

import java.util.ServiceLoader;

public class Main {

    @Test
    public void stream() {
        ServiceLoader<Service> load = ServiceLoader.load(Service.class);
        load.stream().map(ServiceLoader.Provider::get).forEach(Service::say);
    }

    @Test
    public void loop() {
        for (Service service : ServiceLoader.load(Service.class)) {
            service.say();
        }
    }

}
