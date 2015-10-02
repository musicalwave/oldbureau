package tap.execounting.data;

public enum ClientState {
	trial("Пробует"),
	beginner("Новичок"),
	continuer("Продливший"),
	canceled("Ушел"),
	frozen("Заморожен"),
	inactive("Не активен"),
	active("Активен"); 	//aggregation for trial, beginner, continuer

	private String translation;
	
	private ClientState(String translation){
		this.translation = translation;
	}
	
	@Override
	public String toString(){
		return translation;
	}
}
