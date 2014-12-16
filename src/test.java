import java.util.HashMap;


public class test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		HashMap<String,String> map = new HashMap<String,String> ();
		map.put("a","a");
		System.out.println(map.get("a"));
		String c = map.get("a")+"c";
		map.put("a", c);
		System.out.println(map.get("a"));
	}

}
