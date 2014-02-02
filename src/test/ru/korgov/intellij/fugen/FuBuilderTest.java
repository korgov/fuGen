package ru.korgov.intellij.fugen;

import org.junit.Test;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;

import java.util.Comparator;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 28.02.13 3:36
 */
public class FuBuilderTest {

    private long x = 100;

    @Test
    public void testConsts() throws Exception {
        final FuLiveTester fuLiveTester = new FuLiveTester("MyClass", "id", "long", "getId");

        final String testStr = fuLiveTester.buildTestText(new PersistentStateProperties().getProperties().get(0));

        System.out.println(testStr);
    }


    private static final Comparator<Long> BY_X = new Comparator<Long>() {

        @Override
        public int compare(final Long o1, final Long o2) {
            return o1.compareTo(o2);
        }
    };
}
