public class UISO {
public UISO () {}
}
class Dog extends UISO {
void method (Dog dog, UISO u) {
Dog d = dog;
if (d instanceof UISO) // always true.
System.out.println(¡°Dog is a UISO¡±);
UISO uiso = u;
if (uiso instanceof Object) // always true.
System.out.println(¡°uiso is an Object¡±);
}
}