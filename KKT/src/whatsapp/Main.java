package whatsapp;


public class InitiateAutomation {
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
