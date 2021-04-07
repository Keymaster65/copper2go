package de.wolfsvl.copper2go.testutil;

public class Data {

    public static String getName() {
        return "Wolf" + System.currentTimeMillis();
    }

    public static String getExpectedPartMappingBranch(final String name) {
        return  "Hello " + name + "! Please transfer";
    }

    public static String getExpectedPartMaster(final String name) {
        return  "HEllo " + name + "! (Fix the bug;-)";
    }
}
