package regex;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Function;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    @Test
    public void baseMatch() {
        Pattern pattern = Pattern.compile("^asd$");
        Assertions.assertTrue(pattern.matcher("asd").matches());
        Assertions.assertFalse(pattern.matcher("asdf").matches());
    }

    @Test
    public void baseFind() {
        Pattern pattern = Pattern.compile("asd");
        Matcher matcher = pattern.matcher("asf asd asf asd ");
        Assertions.assertTrue(matcher.find());
        Assertions.assertTrue(matcher.find());
        Assertions.assertFalse(matcher.find());
    }

    @Test
    public void multilineMatch() {
        Pattern pattern = Pattern.compile("^asd$", Pattern.MULTILINE);
        // matches表示完全匹配
        Assertions.assertFalse(pattern.matcher("asf\nasd").matches());
        // find表示存在即可
        Assertions.assertTrue(pattern.matcher("asd\nasdf").find());
    }

    @Test
    public void zeroWidthMatch() {
        // 正向先行断言
        Pattern positiveLookahead = Pattern.compile("^([a-z])(?=\\d).*$");
        Matcher pma1 = positiveLookahead.matcher("a1");
        Assertions.assertTrue(pma1.matches());
        Assertions.assertEquals("a", pma1.group(1));
        Matcher pma2 = positiveLookahead.matcher("ab");
        Assertions.assertFalse(pma2.matches());

        // 负向先行断言
        Pattern negativeLookahead = Pattern.compile("^([a-z])(?!\\d).*$");
        Matcher nma1 = negativeLookahead.matcher("ab");
        Assertions.assertTrue(nma1.matches());
        Assertions.assertEquals("a", nma1.group(1));
        Matcher nma2 = negativeLookahead.matcher("a1");
        Assertions.assertFalse(nma2.matches());

        // 正向后发断言
        Pattern positiveLookbehind = Pattern.compile("^.*(?<=\\d)([a-z])$");
        Matcher pmb1 = positiveLookbehind.matcher("1a");
        Assertions.assertTrue(pmb1.matches());
        Assertions.assertEquals("a", pmb1.group(1));
        Matcher pmb2 = positiveLookbehind.matcher("ab");
        Assertions.assertFalse(pmb2.matches());

        // 负向后发断言
        Pattern negativeLookbehind = Pattern.compile("^.*(?<!\\d)([a-z])$");
        Matcher nmb1 = negativeLookbehind.matcher("ab");
        Assertions.assertTrue(nmb1.matches());
        Assertions.assertEquals("b", nmb1.group(1));
        Matcher nmb2 = negativeLookbehind.matcher("1a");
        Assertions.assertFalse(nmb2.matches());
    }

    @Test
    public void groupMatch() {
        // 可用\表示引用分组
        Pattern pattern = Pattern.compile("^([^-]+)-\\1$");
        Assertions.assertTrue(pattern.matcher("1-1").matches());
        Assertions.assertTrue(pattern.matcher("a-a").matches());
        Assertions.assertTrue(pattern.matcher("asd-asd").matches());
        Assertions.assertFalse(pattern.matcher("1-asd").matches());
    }

    @Test
    public void group() {
        // ()捕获分组，(?:)不捕获
        Pattern point = Pattern.compile("^https?://([^/]+)(?:/.*)*$");
        Matcher pointMatch1 = point.matcher("http://baidu.com");
        // 需调用matches方法初始化
        Assertions.assertTrue(pointMatch1.matches());
        // 只捕获到1个分组(baidu.com)
        Assertions.assertEquals(1, pointMatch1.groupCount());
        // 分组0表示匹配字符串
        Assertions.assertEquals("http://baidu.com", pointMatch1.group(0));
        // 捕获到的分组
        Assertions.assertEquals("baidu.com", pointMatch1.group(1));

        // 不捕获(/Yang-xingchen)
        Matcher pointMatch2 = point.matcher("https://github.com/Yang-xingchen");
        Assertions.assertTrue(pointMatch2.matches());
        Assertions.assertEquals(1, pointMatch2.groupCount());
        Assertions.assertEquals("https://github.com/Yang-xingchen", pointMatch2.group(0));
        Assertions.assertEquals("github.com", pointMatch2.group(1));

        // 不捕获(github.com), 捕获到的分组从1开始获取
        Pattern point1 = Pattern.compile("^https?://(?:[^/]+)(/[^/]+)*$");
        Matcher pointMatch3 = point1.matcher("https://github.com/Yang-xingchen/my-java-knowledge-base/blob/master/base/src/main/java/regex/Main.java");
        Assertions.assertTrue(pointMatch3.matches());
        Assertions.assertEquals(1, pointMatch3.groupCount());
        Assertions.assertEquals("https://github.com/Yang-xingchen/my-java-knowledge-base/blob/master/base/src/main/java/regex/Main.java", pointMatch3.group(0));
        Assertions.assertEquals("/Main.java", pointMatch3.group(1));
    }

    @Test
    public void nameGroup() {
        // 分组可命名
        Pattern point = Pattern.compile("^https?://(?<host>[^/]+)(?:.*)$");
        Matcher pointMatch1 = point.matcher("http://baidu.com");
        Assertions.assertTrue(pointMatch1.matches());
        // 可直接通过名称捕获
        Assertions.assertEquals("baidu.com", pointMatch1.group("host"));

        Matcher pointMatch2 = point.matcher("https://github.com/Yang-xingchen");
        Assertions.assertTrue(pointMatch2.matches());
        Assertions.assertEquals("github.com", pointMatch2.group("host"));

        Pattern point1 = Pattern.compile("^https?://(?:[^/]+)(?<path>/[^/]*)*?$");
        Matcher pointMatch3 = point1.matcher("https://github.com/Yang-xingchen/my-java-knowledge-base/blob/master/base/src/main/java/regex/Main.java");
        Assertions.assertTrue(pointMatch3.matches());
        Assertions.assertEquals(1, pointMatch3.groupCount());
        Assertions.assertEquals("https://github.com/Yang-xingchen/my-java-knowledge-base/blob/master/base/src/main/java/regex/Main.java", pointMatch3.group(0));
        // 只取最后一个
        Assertions.assertEquals("/Main.java", pointMatch3.group("path"));

    }

    @Test
    public void baseReplace() {
        Pattern pattern = Pattern.compile("hello");
        // 全部替换
        Assertions.assertEquals("hi world, hi java", pattern.matcher("hello world, hello java").replaceAll("hi"));
        // 只替换第一个
        Assertions.assertEquals("hi world, hello java", pattern.matcher("hello world, hello java").replaceFirst("hi"));
    }

    @Test
    public void refReplace() {
        // 可通过$进行捕获的替换
        Pattern pattern = Pattern.compile("^(\\d{4}).?(\\d{1,2}).?(\\d{1,2})日?$");
        Assertions.assertEquals("2025-3-21", pattern.matcher("2025/3/21").replaceAll("$1-$2-$3"));
        Assertions.assertEquals("2025-3-21", pattern.matcher("2025年3月21日").replaceAll("$1-$2-$3"));
        Assertions.assertEquals("2025-3-21", pattern.matcher("2025 3 21").replaceAll("$1-$2-$3"));
    }

    @Test
    public void funReplace() {
        // 可用Function<MatchResult, String>自定义处理
        Pattern pattern = Pattern.compile("^(?<y>\\d{4}).?(?<m>\\d{1,2}).?(?<d>\\d{1,2})日?$");
        Function<MatchResult, String> replaceFunction = matchResult -> {
            return matchResult.group("y") + "-" + matchResult.group("m") + "-" + matchResult.group("d");
        };
        Assertions.assertEquals("2025-3-21", pattern.matcher("2025/3/21").replaceAll(replaceFunction));
        Assertions.assertEquals("2025-3-21", pattern.matcher("2025年3月21日").replaceAll(replaceFunction));
        Assertions.assertEquals("2025-3-21", pattern.matcher("2025 3 21").replaceAll(replaceFunction));
    }

}
