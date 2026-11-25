package console;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.alexisdamien22.servermanager.console.ConsoleManager;

public class ConsoleManagerTest {

    @Test
    void shouldStoreMessage() {

        ConsoleManager manager =
                new ConsoleManager();

        manager.append(
                "console-test",
                "HELLO_MANAGER"
        );

        assertEquals(
                1,
                manager.getMessageCount(
                        "console-test"
                )
        );

        assertEquals(
                "HELLO_MANAGER",
                manager.getMessages(
                        "console-test"
                )
                .get(0)
                .getMessage()
        );
    }

    @Test
    void shouldStoreApplicationOutput()
            throws Exception {

        ConsoleManager consoleManager =
                new ConsoleManager();

        consoleManager.append(
                "console-test",
                "HELLO_MANAGER"
        );

        assertEquals(
                "HELLO_MANAGER",
                consoleManager
                        .getMessages(
                                "console-test"
                        )
                        .get(0)
                        .getMessage()
        );
    }
}