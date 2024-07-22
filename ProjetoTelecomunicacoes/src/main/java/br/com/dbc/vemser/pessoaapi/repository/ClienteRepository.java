package br.com.dbc.vemser.pessoaapi.repository;

import br.com.dbc.vemser.pessoaapi.entity.Fatura;
import br.com.dbc.vemser.pessoaapi.entity.Cliente;
import br.com.dbc.vemser.pessoaapi.entity.planos.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ClienteRepository {
    private static List<Cliente> clientes = new ArrayList<>();
    List<Cliente> clientePorNome = new ArrayList<>();
    private AtomicInteger COUNTER = new AtomicInteger();
    private final FaturaRepository faturaRepository;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //18/10/2020

    public ClienteRepository(FaturaRepository faturaRepository) {
        this.faturaRepository = faturaRepository;
    }

    public List<Cliente> getAllClientes() {
        String sql = "SELECT * FROM tele_comunicacoes.TB_CLIENTE";

        try (Connection connection = ConexaoBancoDeDados.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdPessoa(resultSet.getInt("id_cliente"));
                cliente.setNome(resultSet.getString("nome"));
                cliente.setDataNascimento(resultSet.getDate("dt_nascimento").toLocalDate());
                cliente.setCpf(resultSet.getString("cpf"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setNumeroTelefone(resultSet.getLong("numero_telefone"));
                cliente.setTipoDePlano(TipoDePlano.valueOf(resultSet.getString("tipo_plano")));
                cliente.setStatus(resultSet.getBoolean("status"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public List<Cliente> getAClientByName(String nome) throws SQLException {
        String sql = "SELECT * FROM tele_comunicacoes.TB_CLIENTE c WHERE c.nome = ?";
        Connection connection = ConexaoBancoDeDados.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, nome);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdPessoa(resultSet.getInt("id_cliente"));
                cliente.setNome(resultSet.getString("nome"));
                cliente.setDataNascimento(resultSet.getDate("dt_nascimento").toLocalDate());
                cliente.setCpf(resultSet.getString("cpf"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setNumeroTelefone(resultSet.getLong("numero_telefone"));
                cliente.setTipoDePlano(TipoDePlano.valueOf(resultSet.getString("tipo_plano")));
                cliente.setStatus(resultSet.getBoolean("status"));
                clientePorNome.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public List<Cliente> getAClientById(Integer id) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "SELECT * FROM tele_comunicacoes.TB_CLIENTE c WHERE c.id_cliente = ?";
        Connection connection = ConexaoBancoDeDados.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1, id);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdPessoa(resultSet.getInt("id_cliente"));
                cliente.setNome(resultSet.getString("nome"));
                cliente.setDataNascimento(resultSet.getDate("dt_nascimento").toLocalDate());
                cliente.setCpf(resultSet.getString("cpf"));
                cliente.setEmail(resultSet.getString("email"));
                cliente.setNumeroTelefone(resultSet.getLong("numero_telefone"));
                cliente.setTipoDePlano(TipoDePlano.valueOf(resultSet.getString("tipo_plano")));
                cliente.setStatus(resultSet.getBoolean("status"));
                clientes.add(cliente);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public List<Cliente> createCliente(Cliente cliente) throws SQLException {
        List<Cliente> clientes = new ArrayList<>();
        String sql = "INSERT INTO tele_comunicacoes.TB_CLIENTE (nome, dt_nascimento, cpf, email, numero_telefone, tipo_plano, status) VALUES(?, ?, ?, ?, ?, ?, ?)";
        Connection connection = ConexaoBancoDeDados.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, cliente.getNome());
        preparedStatement.setDate(2, Date.valueOf(cliente.getDataNascimento()));
        preparedStatement.setString(3, cliente.getCpf());
        preparedStatement.setString(4, cliente.getEmail());
        preparedStatement.setLong(5, cliente.getNumeroTelefone());
        preparedStatement.setString(6, cliente.getTipoDePlano().toString());
        preparedStatement.setBoolean(7, true);

        try (ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                Cliente cliente2 = new Cliente();
                cliente2.setIdPessoa(resultSet.getInt("id_cliente"));
                cliente2.setNome(resultSet.getString("nome"));
                cliente2.setDataNascimento(resultSet.getDate("dt_nascimento").toLocalDate());
                cliente2.setCpf(resultSet.getString("cpf"));
                cliente2.setEmail(resultSet.getString("email"));
                cliente2.setNumeroTelefone(resultSet.getLong("numero_telefone"));
                cliente2.setTipoDePlano(TipoDePlano.valueOf(resultSet.getString("tipo_plano")));
                cliente2.setStatus(resultSet.getBoolean("status"));
                clientes.add(cliente2);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return clientes;
    }

    public Cliente create(Cliente cliente) {
        log.debug("Entrando na PessoaRepository");
        cliente.setIdPessoa(COUNTER.incrementAndGet());
        Plano plano = null;
        switch (cliente.getTipoDePlano()){
            case BASICO:
                plano = new PlanoBasico();
                break;
            case MEDIUM:
                plano = new PlanoMedium();
                break;
            case PREMIUM:
                plano = new PlanoPremium();
                break;
        }
        LocalDate dataAtual = LocalDate.now();
        for (int i = 0; i < 12; i++) {
            LocalDate dataVencimento = dataAtual.plusMonths((i + 1));
            Fatura fatura = new Fatura(faturaRepository.getIdFatura(), cliente.getIdPessoa(), dataVencimento, null, plano.getValor(), 0, (i+1));
            faturaRepository.create(fatura);
        }
        //listaClientes.add(cliente);
        return cliente;
    }

    public Cliente update(Integer id, Cliente clienteAtualizar, Cliente clienteRecuperada) {
        log.debug("Entrando na PessoaRepository");
        clienteRecuperada.setCpf(clienteAtualizar.getCpf());
        clienteRecuperada.setNome(clienteAtualizar.getNome());
        clienteRecuperada.setDataNascimento(clienteAtualizar.getDataNascimento());
        return clienteRecuperada;
    }

    public void delete(Cliente cliente) {
        log.debug("Entrando na PessoaRepository");
        cliente.setStatus(false);
    }

}