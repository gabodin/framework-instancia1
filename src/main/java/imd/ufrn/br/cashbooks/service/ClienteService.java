package imd.ufrn.br.cashbooks.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import imd.ufrn.br.cashbooks.interfaces.IGerarRelatorio;
import imd.ufrn.br.cashbooks.model.Cliente;
import imd.ufrn.br.cashbooks.model.Movimentacao;
import imd.ufrn.br.cashbooks.model.enums.MovimentacaoStatus;
import imd.ufrn.br.cashbooks.repository.ClienteRepository;
import imd.ufrn.br.cashbooks.service.exceptions.DatabaseException;
import imd.ufrn.br.cashbooks.service.exceptions.ResourceNotFoundException;
import imd.ufrn.br.cashbooks.service.exceptions.ValidationException;

@Service
public class ClienteService {
	@Autowired
	private ClienteRepository repository;
	
	@Autowired
	private MovimentacaoService serviceMovimentacao;
	
	private IGerarRelatorio relatorioStrategy;
	
	public void SetRelatorioStrategy(IGerarRelatorio strategy) {
		this.relatorioStrategy = strategy;
	}
	
	
	public List<Cliente> findAll(){
		for(Cliente c : repository.findAll()) {
			calcularDivida(c);
		}
		return repository.findAll();
	}
	
	public Cliente findById(Long id) {
		Optional<? extends Cliente> obj = repository.findById(id);

        return obj.orElseThrow(() -> new ResourceNotFoundException(id));
	}
	
	public Cliente insert(Cliente obj) {
		
		ValidationException exception = new ValidationException("errors");
		
		if(obj.getNome() == null) {
			exception.addError("nome", "campo vazio");
		}
		
		if(obj.getCpf() == null ) {
			exception.addError("cpf", "Campo vazio");		
		} else if(!CPFService.validaCPF(obj.getCpf())) {
			exception.addError("cpf-validade", "valor inválido");
		}
		
		if(obj.getEmail() == null) {
			exception.addError("e-mail", "campo vazio");
		}
		
		if(obj.getEndereco() == null) {
			exception.addError("endereco", "campo vazio");
		}
		
		if(obj.getTelefone() == null) {
			exception.addError("telefone", "campo vazio");
		}
		
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		System.out.println(exception.getErrors());
        return repository.save(obj);
    }
	
	public void delete(Long id) {
        try {
            repository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
        	throw new ResourceNotFoundException(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }
	
	public Cliente update(Long id, Cliente obj) {
		Cliente entity = null;
		try {
            entity = repository.getById(id);
            updateData(entity, obj);
            return repository.save((Cliente) entity);
        } catch(EntityNotFoundException e) {
            throw new ResourceNotFoundException(id);
        }
    }

	private void updateData(Cliente entity, Cliente obj) {
		ValidationException exception = new ValidationException("errors");
		
		if(obj.getNome() == null) {
			exception.addError("nome", "campo vazio");
		}
		
		if(obj.getCpf() == null ) {
			exception.addError("cpf", "Campo vazio");		
		} else if(!(CPFService.validaCPF(obj.getCpf()))) {
			exception.addError("cpf-validade", "valor inválido");
		}
		
		if(obj.getEmail() == null) {
			exception.addError("e-mail", "campo vazio");
		}
		
		if(obj.getEndereco() == null) {
			exception.addError("endereco", "campo vazio");
		}
		
		if(obj.getTelefone() == null) {
			exception.addError("telefone", "campo vazio");
		}
		
		
		if(exception.getErrors().size() > 0) {
			throw exception;
		}
		
		entity.setNome(obj.getNome());
		entity.setCpf(obj.getCpf());
		entity.setEmail(obj.getEmail());
		entity.setEndereco(obj.getEndereco());
		entity.setTelefone(obj.getTelefone());
	}
	
	public List<Cliente> getClientesDevendo(){
		List<? extends Cliente> clientes = repository.findAll();
		List<Cliente> clientesDevendo = new ArrayList<>();
		
		for(Cliente cliente : clientes) {
			calcularDivida(cliente);
			if(cliente.getDivida() > 0) {
				clientesDevendo.add(cliente);
			}
		}
		
		return clientesDevendo;
	}
	
	public double calcularDivida(Cliente cliente) {
		List<Movimentacao> movimentacoesClientes = serviceMovimentacao.findAllByCliente(cliente);
		double divida = 0.0;
		
		for(Movimentacao mov : movimentacoesClientes) {
			if(mov.getStatus() == MovimentacaoStatus.ENTRADA && !mov.isPago()) {
				divida += mov.getValor();
			}
		}
		cliente.setDivida(divida);
		update(cliente.getId(), cliente);
		
		return cliente.getDivida();
	}
	
}
