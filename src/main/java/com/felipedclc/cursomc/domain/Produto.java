package com.felipedclc.cursomc.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity // ENTIDADE DO JPA 
public class Produto implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Integer id;
	private String name;
	private Double price;
	
	@JsonBackReference // OMITE A LISTA DE CATEGORIAS, POIS JA FORAM BUSCADAS NA CLASSE CATEGORIA(@JsonManagedReference)
	@ManyToMany
	@JoinTable(name = "PRODUTO_CATEGORIA",  // TABELA QUE JUNTA CATEGORIAS E PRODUTOS
		joinColumns = @JoinColumn(name = "produto_id"), // CHAVE ESTRANGEIRA DO PRODUTO 
		inverseJoinColumns = @JoinColumn(name = "categoria_id")) // CHAVE ESTRANGEIRA QUE REFERENCIA A CATEGORIA
	private List<Categoria> categorias = new ArrayList<>();
	
	@JsonIgnore // O PRODUTO NÃO PRECISA SERIALIZAR A LISTA ONDE ESTÃO OUTROS PEDIDOS
	@OneToMany(mappedBy = "id.produto") // id (ItemPedidoPK) QUE É ONDE ESTÁ A REFERÊNCIA DO PRODUTO 
	private Set<ItemPedido> itens = new HashSet<>();
	
	public Produto() {
		
	}
	
	public Produto(Integer id, String name, Double price) {
		super();
		this.id = id;
		this.name = name;
		this.price = price;
	}
	
	@JsonIgnore // TUDO QUE COMEÇAR COM GET É AUTOMATICAMENTE SERIALIZADO 
	public List<Pedido> getPedidos() { // PADRAO JAVA BINS (GET PARA OBTER DADOS)
		List<Pedido> lista = new ArrayList<>();
		for(ItemPedido ip : itens) {
			lista.add(ip.getPedido());
		}
		return lista;
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}

	public List<Categoria> getCategorias() {
		return categorias;
	}

	public void setCategorias(List<Categoria> categorias) {
		this.categorias = categorias;
	}
	
	public Set<ItemPedido> getItens() {
		return itens;
	}

	public void setItens(Set<ItemPedido> itens) {
		this.itens = itens;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		Produto other = (Produto) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
