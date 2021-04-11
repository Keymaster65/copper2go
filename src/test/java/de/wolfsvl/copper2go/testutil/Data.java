package de.wolfsvl.copper2go.testutil;

public class Data {

    public static String getName() {
        return "Wolf" + System.currentTimeMillis();
    }

    private Data() {}
    public static String getExpectedHello2Mapping(final String name) {
        return  "Hello " + name + "! Please transfer";
    }

    public static String getExpectedHello(final String name) {
        return  "HEllo " + name + "! (Fix the bug;-)";
    }
}
