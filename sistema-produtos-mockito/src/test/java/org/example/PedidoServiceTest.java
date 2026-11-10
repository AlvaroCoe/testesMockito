package org.example;

import org.example.erros.ErroAoSalvarPedidoException;
import org.example.erros.EstoqueInsuficienteException;
import org.example.erros.ProdutoNaoEncontradoException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PedidoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @Mock
    private PedidoRepository pedidoRepository;

    @InjectMocks
    private PedidoService service;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto("Mouse", 49.90, 10);
    }

    // PED01 - Pedido válido: valor total = preço x quantidade
    @Test
    void deveCalcularValorTotalDoPedido() {

        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 2);

        assertNotNull(pedido);
        assertEquals(99.80, pedido.getValorTotal(), 0.001);
        assertEquals(2, pedido.getQuantidade());
        assertEquals("Mouse", pedido.getProduto().getNome());

        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    // PED02 - Pedido válido: cada repositório é chamado exatamente 1 vez
    @Test
    void deveConsultarProdutoESalvarPedidoUmaVez() {

        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        service.criarPedido(1L, 2);

        verify(produtoRepository, times(1)).buscarPorId(1L);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    // PED03 - Quantidade igual a zero
    @Test
    void deveLancarExcecaoQuandoQuantidadeForZero() {

        assertThrows(IllegalArgumentException.class,
                () -> service.criarPedido(1L, 0));

        verify(pedidoRepository, never()).salvar(any());
        verifyNoInteractions(produtoRepository);
    }

    // PED04 - Quantidade negativa
    @Test
    void deveLancarExcecaoQuandoQuantidadeForNegativa() {

        assertThrows(IllegalArgumentException.class,
                () -> service.criarPedido(1L, -3));

        verify(pedidoRepository, never()).salvar(any());
        verifyNoInteractions(produtoRepository);
    }

    // PED05 - Produto inexistente
    @Test
    void deveLancarExcecaoQuandoProdutoNaoExiste() {

        when(produtoRepository.buscarPorId(99L)).thenReturn(null);

        assertThrows(ProdutoNaoEncontradoException.class,
                () -> service.criarPedido(99L, 1));

        verify(produtoRepository).buscarPorId(99L);
        verify(pedidoRepository, never()).salvar(any());
    }

    // PED06 - Quantidade maior que o estoque (11 com estoque 10)
    @Test
    void deveLancarExcecaoQuandoEstoqueInsuficiente() {

        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class,
                () -> service.criarPedido(1L, 11));

        verify(pedidoRepository, never()).salvar(any());
    }

    // PED07 - Quantidade igual ao estoque (valor de fronteira)
    @Test
    void deveAceitarQuantidadeIgualAoEstoque() {

        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);

        Pedido pedido = service.criarPedido(1L, 10);

        assertEquals(499.0, pedido.getValorTotal(), 0.001);
        verify(pedidoRepository, times(1)).salvar(any(Pedido.class));
    }

    // ---------------- Atividade 7 - Simulando falhas com doThrow ----------------

    // Cenário A - Banco fora do ar ao salvar o pedido.
    // Com o Service original a RuntimeException sobe sem tratamento; após o
    // Desafio (Cenário D) sobe ErroAoSalvarPedidoException, que também é
    // RuntimeException. Por isso o teste passa nas duas versões.
    @Test
    void deveLancarExcecaoQuandoBancoCairAoSalvarPedido() {

        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        assertThrows(RuntimeException.class,
                () -> service.criarPedido(1L, 2));

        verify(pedidoRepository).salvar(any(Pedido.class));
    }

    // Cenário B - when() em método void NÃO compila:
    //
    //   when(pedidoRepository.salvar(any())).thenReturn(...);   // ERRO
    //
    // when() exige um valor de retorno para configurar, e salvar() é void.
    // Forma correta: doThrow(...).when(pedidoRepository).salvar(any(Pedido.class));
    // ou doNothing().when(pedidoRepository).salvar(any(Pedido.class));

    // Cenário C - Falha no ProdutoRepository
    @Test
    void naoDeveSalvarPedidoQuandoProdutoRepositoryFalhar() {

        when(produtoRepository.buscarPorId(1L))
                .thenThrow(new RuntimeException("Falha na conexão"));

        assertThrows(RuntimeException.class,
                () -> service.criarPedido(1L, 2));

        verify(pedidoRepository, never()).salvar(any());
    }

    // Cenário D (Desafio) - Service trata a falha e lança exceção própria
    @Test
    void deveLancarErroAoSalvarPedidoQuandoRepositoryFalhar() {

        when(produtoRepository.buscarPorId(1L)).thenReturn(produto);
        doThrow(new RuntimeException("Falha na conexão"))
                .when(pedidoRepository).salvar(any(Pedido.class));

        ErroAoSalvarPedidoException erro = assertThrows(
                ErroAoSalvarPedidoException.class,
                () -> service.criarPedido(1L, 2));

        assertEquals("Falha ao salvar o pedido", erro.getMessage());
        assertEquals("Falha na conexão", erro.getCause().getMessage());
        verify(pedidoRepository).salvar(any(Pedido.class));
    }
}
