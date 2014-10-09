package spindlebox.util;

import junit.framework.TestCase;

import java.util.Properties;

public class PropertiesKeyValueStoreTest extends TestCase {
    private PropertiesKeyValueStore store;

    public void setUp() throws Exception {
        super.setUp();

        Properties p = new Properties();
        p.setProperty("root", "rootValue");
        p.setProperty("child1.value", "child1Value");
        p.setProperty("child1.grandchild1.value", "grandChild1Value");

        store = new PropertiesKeyValueStore(p);
    }

    public void testGet() throws Exception {
        assertEquals("rootValue", store.get("root").get());
    }

    public void testChild() throws Exception {
        KeyValueStore child = store.child("child1");
        assertEquals("child1Value", child.get("value").get());

        child = child.child("grandchild1");
        assertEquals("grandChild1Value", child.get("value").get());
    }
}
