package whatsapp;


public class Main {
	public static void main(String args[]){
	try{
		Automate automate=new Automate();
		automate.initiateProcess();
	}
	catch(Exception e){
		Global.exception.error("Exception occurred while calling initiateProcess", e);
	}
}
}
