package br.com.dbc.vemser.pessoaapi.service;

import br.com.dbc.vemser.pessoaapi.dto.ClienteCreateDTO;
import br.com.dbc.vemser.pessoaapi.dto.ClienteDTO;
import br.com.dbc.vemser.pessoaapi.entity.Cliente;
import br.com.dbc.vemser.pessoaapi.exceptions.RegraDeNegocioException;
import br.com.dbc.vemser.pessoaapi.repository.ClienteRepository;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ObjectMapper objectMapper;
    private final EmailService emailService;

    public ClienteService(ClienteRepository clienteRepository, ObjectMapper objectMapper, EmailService emailService){
        this.clienteRepository = clienteRepository;
        this.objectMapper = objectMapper;
        this.emailService = emailService;
    }

    public List<ClienteDTO> list(){
        List<ClienteDTO> list = clienteRepository.getAllClientes()
                .stream()
                .map(cliente -> objectMapper.convertValue(cliente, ClienteDTO.class))
                .collect(Collectors.toList());
        return list;
    }

    public List<ClienteDTO> listByName(String nome) throws SQLException {
        List<ClienteDTO> list = clienteRepository.getAClientByName(nome)
                .stream()
                .map(cliente -> objectMapper.convertValue(cliente, ClienteDTO.class))
                .collect(Collectors.toList());
        return list;
    }

    public List<ClienteDTO> listById(Integer id) throws SQLException {
        List<ClienteDTO> list = clienteRepository.getAClientById(id)
                .stream()
                .map(cliente -> objectMapper.convertValue(cliente, ClienteDTO.class))
                .collect(Collectors.toList());
        return list;
    }

    public ClienteDTO createCliente(ClienteCreateDTO dto) throws Exception {
        log.debug("Entrando na ClienteService");
        Cliente clienteEntity = objectMapper.convertValue(dto, Cliente.class);
        clienteRepository.createCliente(clienteEntity);
        ClienteDTO clienteDTO = objectMapper.convertValue(clienteEntity, ClienteDTO.class);
        emailService.sendEmail(clienteEntity, "cp");
        return clienteDTO;
    }

    public ClienteDTO create(ClienteCreateDTO dto) throws Exception {
        log.debug("Entrando na PessoaService");
        Cliente clienteEntity = objectMapper.convertValue(dto, Cliente.class);
        clienteRepository.create(clienteEntity);
        ClienteDTO clienteDTO = objectMapper.convertValue(clienteEntity, ClienteDTO.class);
        emailService.sendEmail(clienteEntity, "cp");
        return clienteDTO;
    }

    public ClienteDTO update(Integer id, ClienteCreateDTO dto) throws Exception {
        log.debug("Entrando na PessoaService");
        Cliente clienteEntity = objectMapper.convertValue(dto, Cliente.class);
        Cliente clienteRecuperada = getPessoa(id);
        clienteRepository.update(id, clienteEntity, clienteRecuperada);
        ClienteDTO clienteDTO = objectMapper.convertValue(clienteEntity, ClienteDTO.class);
        return clienteDTO;
    }

    public void delete(Integer id) throws Exception {
        log.debug("Entrando na PessoaService");
        Cliente clienteRecuperada = getPessoa(id);
        clienteRepository.delete(clienteRecuperada);
    }

    public Cliente getPessoa(Integer id) throws Exception {
        Cliente clienteRecuperada = clienteRepository.list().stream()
                .filter(pessoa -> pessoa.getIdPessoa().equals(id))
                .findFirst()
                .orElseThrow(() -> new RegraDeNegocioException("Pessoa não encontrada!"));
        return clienteRecuperada;
    }

} 
