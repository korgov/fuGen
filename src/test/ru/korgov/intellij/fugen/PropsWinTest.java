package ru.korgov.intellij.fugen;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.junit.Test;
import ru.korgov.intellij.fugen.properties.ui.PropertiesWindow;

import javax.swing.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Author: Kirill Korgov (kirill@korgov.ru))
 * Date: 01.03.13 23:24
 */
public class PropsWinTest extends LightCodeInsightFixtureTestCase {

    private final JFrame frame = new JFrame("Test");
    private final ExecutorService service = Executors.newSingleThreadExecutor();

    @Test
    public void testAll() throws Exception {
        final PropertiesWindow propertiesWindow = new PropertiesWindow();
        frame.setContentPane(propertiesWindow.getMainPanel());
        frame.pack();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

    }
}
