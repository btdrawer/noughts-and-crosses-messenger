package protocol;

public class Protocol {
	public String getAction(String a) {
		return a.split("/")[0];
	}
	
	public String transmit(String action, String[] a) {
		StringBuilder sb = new StringBuilder();
		
		for(String s : a) {
			sb.append(s + "/");
		}
		
		return action + "/" + sb.toString();
	}
	
	public String transmit(String action, String a) {
		return action + "/" + a;
	}
	
	public String[] receive(String a) {
		String[] s0 = a.split("/");
		String[] s1 = new String[s0.length-1];
		
		for(int i = 1; i < s0.length; i++) {
			s1[i] = s0[i];
		}
		
		return s1;
	}
}
