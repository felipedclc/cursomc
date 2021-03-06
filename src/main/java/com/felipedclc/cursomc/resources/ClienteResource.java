package com.felipedclc.cursomc.resources;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.felipedclc.cursomc.domain.Cliente;
import com.felipedclc.cursomc.dto.ClienteDTO;
import com.felipedclc.cursomc.dto.ClienteNewDTO;
import com.felipedclc.cursomc.services.ClienteService;

@RestController // CONTROLADOR REST (ACESSA A CAMADA DE SERVIÇOS)
@RequestMapping(value= "/clientes") // END POINT 
public class ClienteResource {
	
	@Autowired
	private ClienteService service;

	@RequestMapping(value= "/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> find(@PathVariable Integer id) { // ESSE id RECEBE O "/{id}"
		  // Response Entity: ARMAZENA RESPOSTAS HTTP PARA UM SERVIÇO REST (PROTOCOLO HTTP) 
		Cliente obj = service.find(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@RequestMapping(value="/email", method=RequestMethod.GET) // END POINT EMAIL 
	public ResponseEntity<Cliente> find(@RequestParam(value="value") String email) { // RECEBE EMAIL COMO VALOR(PARAMETRO)
		Cliente obj = service.findByEmail(email); // CHAMANDO O SERVIÇO 
		return ResponseEntity.ok().body(obj); // RETORNANDO O OBJ
	}
	
	@Transactional
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> insert(@Valid @RequestBody ClienteNewDTO objDTO) { // @RequestBody CONVERTE O JSON PARA O OBJETO JAVA
		Cliente obj = service.fromDTO(objDTO); // CONVERTENDO PARA DTO
		obj = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId()).toUri();
		return ResponseEntity.created(uri).build(); // CRIANDO O CODIGO URI 201
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT) // METODO BUSCA O ID E EDITA
	public ResponseEntity<Void> update(@Valid @RequestBody ClienteDTO objDTO, @PathVariable Integer id) {
		Cliente obj = service.fromDTO(objDTO);
		obj.setId(id);
		obj = service.update(obj);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasAnyRole('ADMIN')") // PERMISSÃO APENAS DO ADMIN
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Void> delete(@PathVariable Integer id) { // PATH VARIABLE ASSOCIA O ID(STRING) COM O ID(URL)
		service.delete(id);
		return ResponseEntity.noContent().build(); // RESPOSTA 204
	}

	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<List<ClienteDTO>> findAll() {
		List<Cliente> list = service.findAll();
		List<ClienteDTO> listDTO = list.stream().map(obj -> new ClienteDTO(obj)).
				collect(Collectors.toList()); // CONVERTENDO UMA LISTA(list) PARA OUTRA LISTA(listDTO) 
		return ResponseEntity.ok().body(listDTO);
	}
	@PreAuthorize("hasAnyRole('ADMIN')")
	@RequestMapping(value =  "/page", method = RequestMethod.GET) // END POINT DE BUSCA POR PAGINAS
	public ResponseEntity<Page<ClienteDTO>> findPage(
			@RequestParam(value = "page", defaultValue = "0") Integer page, // RequestParam (PARAMETRO OPCIONAL)
			@RequestParam(value = "linesPerPage", defaultValue = "24") Integer linesPerPage, // 24 É MULTIPLO DE 2, 3 E 4
			@RequestParam(value = "orderBy", defaultValue = "name") String orderBy, // ORDENADO POR NOME
			@RequestParam(value = "direction", defaultValue = "ASC") String direction) { // ASCENDENTE
		Page<Cliente> list = service.findPage(page, linesPerPage, orderBy, direction);
		Page<ClienteDTO> listDTO = list.map(obj -> new ClienteDTO(obj)); // PAGE NÃO NESSECITA A COVERSAO (STREAM/COLLECT)
		return ResponseEntity.ok().body(listDTO);
	}
	
	@RequestMapping(value = "/picture", method = RequestMethod.POST)
	public ResponseEntity<Void> uploadProfilePicture(@RequestParam(name = "file") MultipartFile file) { 
		URI uri = service.uploadProfilePicture(file); // FAZENDO UPLOAD DE UMA IMAGEM E OBTENDO A URI
		return ResponseEntity.created(uri).build();
	}
}
