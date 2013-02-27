package ru.korgov.intellij.fugen;

import org.junit.Test;
import ru.korgov.intellij.fugen.properties.PersistentStateProperties;
import ru.korgov.intellij.fugen.properties.ui.FuLiveTester;

/**
 * Author: Kirill Korgov (korgov@yandex-team.ru))
 * Date: 28.02.13 3:36
 */
public class FuBuilderTest {

    @Test
    public void testConsts() throws Exception {
        final FuLiveTester fuLiveTester = new FuLiveTester("MyClass", "id", "long", "getId");

        final String testStr = fuLiveTester.buildTestText(new PersistentStateProperties());

        System.out.println(testStr);
    }
}
