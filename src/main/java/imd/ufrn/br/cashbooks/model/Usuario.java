package imd.ufrn.br.cashbooks.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Usuario implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue (strategy = GenerationType.IDENTITY)
	private Long id;
	
	private String nome;
	
	private String email;
	private double saldo;
	//TODO private String senha;
	

	private String cnpj;

	// Variáveis de configuração do site, para customizar da forma que o comerciante
	// bem desejar
	private String nomeComercio="NOME DA EMPRESA";

	private String cor="#ff0000";
	
	public Usuario(Long id, String nome, String email, double saldo, String cnpj, String nomeComercio,
			String cor) {
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.saldo = saldo;
		this.cnpj = cnpj;
		this.nomeComercio = nomeComercio;
		this.cor = cor;
	}

	public Usuario() {
		
	}
	
	@JsonIgnore
	@OneToMany(mappedBy = "usuario")
	private List<Movimentacao> movimentacoes = new ArrayList<>();
	
	public List<Movimentacao> getMovimentacoes() {
		return movimentacoes;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public double getSaldo() {
		return saldo;
	}


	public void setSaldo(double saldo) {
		this.saldo = saldo;
	}


	public String getCnpj() {
		return cnpj;
	}
	

	public void setCnpj(String cnpj) {			
		this.cnpj = cnpj;
	}

	public String getNomeComercio() {
		return nomeComercio;
	}

	public void setNomeComercio(String nomeComercio) {
		this.nomeComercio = nomeComercio;
	}

	public String getCor() {
		return cor;
	}

	public void setCor(String cor) {		
		this.cor = cor;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Usuario other = (Usuario) obj;
		if (id != other.getId())
			return false;
		return true;
	}
	
	
	
}