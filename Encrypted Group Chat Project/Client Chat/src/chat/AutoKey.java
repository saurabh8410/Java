package chat;

class AutoKey{
	private static int key=6;
	static String encryption(String msg){
		String en="";
		en=en+(char)(msg.charAt(0)+key);
		for(int i=1;i<msg.length();i++){
			en=en+(char)(msg.charAt(i)+msg.charAt(i-1));
		}
		return en;
	}
	
	static String decryption(String msg){
		String original="";
		original=original+(char)(msg.charAt(0)-key);
		for(int i=1;i<msg.length();i++){
			original=original+(char)(msg.charAt(i)-original.charAt(i-1));
		}
		return original;
	}
	
	/*public static void main(String[] args){
		String m=new AutoKey().encryption("to try");
		System.out.println(m);
		System.out.println(new AutoKey().decryption(m));
	}*/
	
}