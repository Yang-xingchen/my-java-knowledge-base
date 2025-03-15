package spi;

public class ServiceFactory {

    public static Service provider() {
        return () -> System.out.println("hello world3!");
    }

}
