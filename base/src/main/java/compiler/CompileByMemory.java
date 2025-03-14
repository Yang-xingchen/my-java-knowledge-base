package compiler;

import javax.tools.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;

public class CompileByMemory {

    public static void main(String[] args) throws Exception {
        String code = """
                public class CompilerTest {
                    public static void main() {
                        System.out.println("hello world");
                    }
                }
                """;
        compile(code);

        invoke(Paths.get("CompilerTest.class"));
    }

    private static void compile(String code) throws IOException {
        JavaCompiler javaCompiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> listener = new DiagnosticCollector<>();
        try (StandardJavaFileManager fileManager = javaCompiler.getStandardFileManager(listener, Locale.CHINA, StandardCharsets.UTF_8)) {
            List<StringJavaObject> compilationUnits = List.of(new StringJavaObject("CompilerTest", code));
            JavaCompiler.CompilationTask task = javaCompiler.getTask(null, fileManager, listener, null, null, compilationUnits);
            task.call();
            listener.getDiagnostics().forEach(System.out::println);
        }
    }

    /**
     * 调用
     */
    private static void invoke(Path target) throws Exception {
        new ClassLoader(){
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                try {
                    byte[] bytes = Files.readAllBytes(target);
                    return defineClass(name, bytes, 0, bytes.length);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
                .loadClass("CompilerTest")
                .getMethod("main")
                .invoke(null);
    }

}
