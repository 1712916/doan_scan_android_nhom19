import java.util.ArrayList;

// Java có String và StringBuffer
// String dùng cho xâu tĩnh (không thay đổi được nội dung và size)
// StringBuffer là xâu động (thay đổi được nội dung và size) 
/**
 * string
 */

public class string {

    public static void main(String[] args) {
        char[] ch = { 'i', 'j', 'k' };
        byte[] i = { 105, 106, 107 };
        
        String str = new String(i,0,3);
        StringBuffer str1=new StringBuffer("ijk");
        String str2=new String("asldkafjhsdkjhasertaa");
       
        System.out.println(String.valueOf(ch));
    }
}