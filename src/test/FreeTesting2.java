package test;

import org.junit.Test;
public class FreeTesting2 {
    @Test
    public void getterVsPublicTest(){
//
//        Object1 o1= new Object1();
//
//        String o1PrivateName = o1.getPrivateName();
//        System.out.println(o1PrivateName);
//        o1PrivateName = "o1PrivateName";
//
//        System.out.println(o1.getPrivateName());

//        Object2 o2= new Object2();

//        Object2 o2 = new Object2();
//        String o2PublicName = o2.publicName;
//        o2PublicName = "123123123";
//        System.out.println(o2.publicName);

        int aa = Integer.parseInt("11223");

        System.out.println(aa);

    }


}

class Object1{
    public String privateName = "privateName";

    public String getPrivateName() {
        return privateName;
    }

    public void setPrivateName(String privateName) {
        this.privateName = privateName;
    }
}
class Object2{
    public String publicName = "publicName";
}