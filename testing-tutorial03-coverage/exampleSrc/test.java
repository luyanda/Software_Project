public class test {

    public static void main(String[] args) {
        printOne();
    }

    public static void printOne() {
        System.out.println("Hello world!");
    }

    public static void printTwo() {
        printOne();
        printOne();
    }

}
