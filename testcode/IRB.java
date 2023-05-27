public class IRB
{
    void method () {
        int[] array1 = new int [100];
        for (int i = 0; i < array1.length; i++) {
            array1 [i] = i;
        }
        int[] array2 = new int [100];
        for (int i = 0; i < array2.length; i++) {
            array2 [i] = array1 [i];                 // Violation
        }
    }
}
