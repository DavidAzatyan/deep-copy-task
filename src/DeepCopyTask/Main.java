package DeepCopyTask;

import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        // Create an instance of Man
        Man originalMan = new Man("John Doe", Integer.valueOf(30), Arrays.asList("Book1", "Book2", "Book3"));

        // Perform deep copy
        Man copiedMan = DeepCopyUtils.deepCopy(originalMan);

        // Print original and copied objects
        System.out.println("Original: " + originalMan);
        System.out.println("Copied: " + copiedMan);

        // Modify the copied object to demonstrate that the copy is deep
        copiedMan.setName("Jane Doe");
        copiedMan.getFavoriteBooks().add("Book4");

        System.out.println("After modification:");
        System.out.println("Original: " + originalMan);
        System.out.println("Copied: " + copiedMan);
    }
}
