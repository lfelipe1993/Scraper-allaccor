package br.net.digitalzone.scrapper;

public enum Usuarios {
	//"@lfelipe1993"
	LUIZ("Luiz", "221268", "5518998180540"),
	//"@Tadeu"
	TADEU("Tadeu", "8875487", "553195658869");
	
	private String nome;
	private String keyForWhats;
	private String phoneNumber;
	
	private Usuarios(String nome, String keyForWhats,String phoneNumber) {
		this.nome = nome;
		this.keyForWhats = keyForWhats;
		this.phoneNumber = phoneNumber;
	}

	public String getNome() {
		return nome;
	}


	public String getKeyForWhats() {
		return keyForWhats;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}
	
}
