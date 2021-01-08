package test;

public class PassTesting
{

    String testABC = "ABC";
    String testABC2 = "ABC";

    public static void main(String[] args) {

        PassTesting pst = new PassTesting();

        System.out.println(pst.testABC2==pst.testABC);

        System.out.println(foo(9));


    }

    public static Integer foo(Integer d) {

        d = 6;

        return d;

    }


}
