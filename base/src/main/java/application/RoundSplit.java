package application;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * 范围封装，可进行按点拆分范围或按范围求差集
 */
public class RoundSplit {

    @Test
    public void split() {
        Round round = new Round(0, 10);
        System.out.println("===== -5 =====");
        round.split(-5).forEach(System.out::println);
        System.out.println("===== 5 =====");
        round.split(5).forEach(System.out::println);
        System.out.println("===== 15 =====");
        round.split(15).forEach(System.out::println);
        System.out.println("===== -10, -5 =====");
        round.split(-10, -5).forEach(System.out::println);
        System.out.println("===== -5, 5 =====");
        round.split(-5, 5).forEach(System.out::println);
        System.out.println("===== -5, 15 =====");
        round.split(-5, 15).forEach(System.out::println);
        System.out.println("===== 3, 7 =====");
        round.split(3, 7).forEach(System.out::println);
        System.out.println("===== 5, 15 =====");
        round.split(5, 15).forEach(System.out::println);
        System.out.println("===== 15, 20 =====");
        round.split(15, 20).forEach(System.out::println);
    }

    @Test
    public void cycle() {
        System.out.println(" 0 - - + - 5 - + - -10 - - + -15 - + - -20 - - + -25 - + - -30");
        System.out.println("   1 2 [       ]           [       ]           [       ]");
        cycleRes(new Round(1, 2), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [   5   ]           [       ]           [       ]");
        cycleRes(new Round(1, 5), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [       ]   9       [       ]           [       ]");
        cycleRes(new Round(1, 9), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [       ]      11   [       ]           [       ]");
        cycleRes(new Round(1, 11), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [       ]           [  15   ]           [       ]");
        cycleRes(new Round(1, 15), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [       ]           [       ]  19       [       ]");
        cycleRes(new Round(1, 19), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [       ]           [       ]           [  25   ]");
        cycleRes(new Round(1, 25), 3, 4, 10).forEach(System.out::println);
        System.out.println("   1   [       ]           [       ]           [       ]  29");
        cycleRes(new Round(1, 29), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [   5 6 ]           [       ]           [       ]");
        cycleRes(new Round(5, 6), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [   5   ]   9       [       ]           [       ]");
        cycleRes(new Round(5, 9), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [   5   ]      11   [       ]           [       ]");
        cycleRes(new Round(5, 11), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [   5   ]           [  15   ]           [       ]");
        cycleRes(new Round(5, 15), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [   5   ]           [       ]  19       [       ]");
        cycleRes(new Round(5, 19), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [   5   ]           [       ]           [  25   ]");
        cycleRes(new Round(5, 25), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]           [       ]           [       ]  29");
        cycleRes(new Round(5, 29), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]   9  11   [       ]           [       ]");
        cycleRes(new Round(9, 11), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]   9       [  15   ]           [       ]");
        cycleRes(new Round(9, 15), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]   9       [       ]  19       [       ]");
        cycleRes(new Round(9, 19), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]           [  1516 ]           [       ]");
        cycleRes(new Round(15, 16), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]           [  15   ]  19       [       ]");
        cycleRes(new Round(15, 19), 3, 4, 10).forEach(System.out::println);
        System.out.println("       [       ]           [       ]1819       [       ]");
        cycleRes(new Round(18, 19), 3, 4, 10).forEach(System.out::println);
    }

    /**
     *
     * @param src 需要拆分的{@link Round}
     * @param offset 开始偏移量
     * @param length 长度
     * @param round 相邻开始点间隔
     * @return
     */
    private List<Round> cycleRes(Round src, double offset, double length, double round) {
        List<Round> res = new ArrayList<>();
        while (offset < src.start) {
            List<Round> split = src.split(offset, offset + length);
            if (split.isEmpty()) {
                return split;
            }
            src = split.get(0);
            offset += round;
        }
        while (true) {
            List<Round> split = src.split(offset, offset + length);
            res.add(split.get(0));
            if (split.size() == 1) {
                break;
            }
            src = split.get(1);
            offset += round;
        }
        return res;
    }


    private static class Round {
        private final double start;
        private final double end;

        public Round(double start, double end) {
            if (start > end) {
                throw new IllegalArgumentException("start > end");
            }
            this.start = start;
            this.end = end;
        }

        private List<Round> split(double split) {
            if (split <= start || split >= end) {
                return List.of(this);
            }
            return List.of(new Round(start, split), new Round(split, end));
        }

        private List<Round> split(double splitStart, double splitEnd) {
            if (splitStart > splitEnd) {
                throw new IllegalArgumentException();
            }
            if (splitEnd < start || end < splitStart) {
                return List.of(this);
            }
            if (splitStart < start && end < splitEnd) {
                return List.of();
            }
            if (splitStart < start) {
                return List.of(new Round(splitEnd, end));
            }
            if (end < splitEnd) {
                return List.of(new Round(start, splitStart));
            }
            return List.of(new Round(start, splitStart), new Round(splitEnd, end));
        }

        @Override
        public String toString() {
            return "[" + start + ", " + end + "]";
        }
    }


}
